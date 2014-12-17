/*
 * Copyright (C) 2014 The Android Open Source Project
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

package com.android.jack.incremental;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import com.android.jack.Options;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Common part of {@link InputFilter}
 */
public abstract class CommonFilter {

  @Nonnull
  protected List<String> getJavaFileNamesSpecifiedOnCommandLine(@Nonnull Options options) {
    final List<File> folders = new ArrayList<File>();
    final String extension = ".java";

    List<String> javaFileNames =
        Lists.newArrayList(Collections2.filter(options.getEcjArguments(), new Predicate<String>() {
          @Override
          public boolean apply(String arg) {
            File argFile = new File(arg);
            if (argFile.isDirectory()) {
              folders.add(argFile);
            }
            return arg.endsWith(extension);
          }
        }));

    for (File folder : folders) {
      fillFiles(folder, extension, javaFileNames);
    }

    return (javaFileNames);
  }

  private void fillFiles(@Nonnull File folder, @Nonnull String fileExt,
      @Nonnull List<String> fileNames) {
    for (File subFile : folder.listFiles()) {
      if (subFile.isDirectory()) {
        fillFiles(subFile, fileExt, fileNames);
      } else {
        String path = subFile.getPath();
        if (subFile.getName().endsWith(fileExt) && !fileNames.contains(path)) {
          fileNames.add(path);
        }
      }
    }
  }
}