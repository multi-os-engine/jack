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

package com.android.sched.util.config;

import com.android.sched.util.codec.ImplementationName;
import com.android.sched.util.config.id.PropertyId;
import com.android.sched.util.file.OutputStreamFile;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;

import javax.annotation.Nonnull;

/**
 * {@link ConfigPrinter} which prints a properties file
 */
@ImplementationName(iface = ConfigPrinter.class, name = "properties-file")
public class PropertiesConfigPrinter implements ConfigPrinter {
  @Override
  public void printConfig(@Nonnull Config config) {
    OutputStreamFile stream = ThreadConfig.get(ConfigPrinterFactory.CONFIG_PRINTER_FILE);
    PrintStream printer = stream.getPrintStream();

    try {
      // Print header
      printer.println("#");
      printer.println("# Generated by " + PropertiesConfigPrinter.class.getSimpleName());
      printer.println("# on " + new Date());
      printer.println("#");

      // Get and sort properties
      Collection<PropertyId<?>> collec = config.getPropertyIds();
      PropertyId<?>[] properties = collec.toArray(new PropertyId<?>[collec.size()]);
      Arrays.sort(properties, new Comparator<PropertyId<?>>() {
        @Override
        public int compare(PropertyId<?> o1, PropertyId<?> o2) {
          return o1.getName().compareTo(o2.getName());
        }
      });

      // Print properties
      for (PropertyId<?> property : properties) {
        StringBuilder sb = new StringBuilder();

        String value = config.getAsString(property);
        sb.append(property.getName());
        sb.append(" = ");
        sb.append(value);

        printer.println(sb);
      }
    } finally {
      printer.close();
    }
  }
}
