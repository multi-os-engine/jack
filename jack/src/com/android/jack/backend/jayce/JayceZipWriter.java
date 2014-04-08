/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.jack.backend.jayce;

import com.android.jack.JackFileException;
import com.android.jack.Options;
import com.android.jack.ir.JackFormatIr;
import com.android.jack.ir.NonJackFormatIr;
import com.android.jack.ir.ast.JDefinedClassOrInterface;
import com.android.jack.ir.ast.JSession;
import com.android.jack.jayce.JayceWriter;
import com.android.jack.scheduling.feature.JackFileZipOutput;
import com.android.jack.util.BytesStreamSucker;
import com.android.sched.item.Description;
import com.android.sched.item.Name;
import com.android.sched.schedulable.Constraint;
import com.android.sched.schedulable.Produce;
import com.android.sched.schedulable.RunnableSchedulable;
import com.android.sched.schedulable.Support;
import com.android.sched.util.config.ThreadConfig;
import com.android.sched.util.location.Location;
import com.android.sched.util.location.ZipLocation;
import com.android.sched.vfs.InputVFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Nonnull;

/**
 * Writer of Jayce files in a zip organized according to package names.
 */
@Description("Writer of Jayce files in a zip organized according to package names")
@Name("JayceZipWriter")
@Constraint(need = {JackFormatIr.class}, no = {NonJackFormatIr.class})
@Produce(JackFormatProduct.class)
@Support(JackFileZipOutput.class)
public class JayceZipWriter implements RunnableSchedulable<JSession> {

  @Nonnull
  private final File outputZip = ThreadConfig.get(Options.JACK_FILE_OUTPUT_ZIP);

  @Override
  public void run(@Nonnull JSession session) throws Exception {

    try {
      ZipOutputStream zos =
          new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outputZip)));
      try {
        for (JDefinedClassOrInterface type : session.getTypesToEmit()) {
          String filePath = JayceSingleTypeWriter.getFilePath(type);
          ZipEntry zipEntry = new ZipEntry(filePath);
          zos.putNextEntry(zipEntry);
          JayceWriter writer = new JayceWriter(zos);
          writer.write(type, "jack-zip");
        }

        ResourceContainerMarker resourceContainer =
            session.getMarker(ResourceContainerMarker.class);
        if (resourceContainer != null) {
          for (InputVFile resource : resourceContainer.getResources()) {
            Location location = resource.getLocation();
            String entryName;
            if (location instanceof ZipLocation) {
              ZipLocation zipLocation = (ZipLocation) location;
              entryName = zipLocation.getEntryName();
            } else {
              entryName = resource.getName();
            }
            ZipEntry resourceEntry = new ZipEntry(entryName);
            zos.putNextEntry(resourceEntry);
            BytesStreamSucker sucker =
                new BytesStreamSucker(resource.openRead(), zos);
            sucker.run();
          }
        }
      } finally {
        zos.close();
      }
    } catch (IOException e) {
      throw new JackFileException(
          "Could not write Jack archive to output '" + outputZip.getAbsolutePath() + "'", e);
    }
  }
}
