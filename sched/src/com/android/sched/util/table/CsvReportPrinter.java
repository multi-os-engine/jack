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

package com.android.sched.util.table;

import com.android.sched.util.codec.ByteFormatter;
import com.android.sched.util.codec.DoubleCodec;
import com.android.sched.util.codec.Formatter;
import com.android.sched.util.codec.ImplementationName;
import com.android.sched.util.codec.LongCodec;
import com.android.sched.util.codec.PercentFormatter;
import com.android.sched.util.config.ThreadConfig;
import com.android.sched.util.file.OutputStreamFile;
import com.android.sched.util.log.LoggerFactory;

import java.io.PrintStream;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

/**
 * {@link ReportPrinter} implementation which dumps tables in csv raw format and escapes all
 * occurrences of "," with "\".
 */
@ImplementationName(iface = ReportPrinter.class, name = "csv")
public class CsvReportPrinter implements ReportPrinter {
  @Nonnull
  private static Logger logger = LoggerFactory.getLogger();

  @Nonnull
  private final OutputStreamFile reportFile =
      ThreadConfig.get(ReportPrinterFactory.REPORT_PRINTER_FILE);

  @Override
  public void printReport(@Nonnull Report report) {
    PrintStream printStream = reportFile.getPrintStream();
    try {
      for (Table table : report) {

        // Change some formatters to raw formatters here
        Formatter<?>[] formatters = table.getFormatters();
        for (int idx = 0; idx < formatters.length; idx++) {
          Formatter<?> formatter = formatters[idx];

          if (formatter instanceof PercentFormatter) {
            formatters[idx] = new DoubleCodec();
          } else if (formatter instanceof ByteFormatter) {
            formatters[idx] = new LongCodec();
          }
        }

        // Dump table
        for (Iterable<String> row : table) {
          boolean first = true;
          for (String data : row) {
            if (first) {
              first = false;
            } else {
              printStream.print(',');
            }
            printStream.print(data.replace(",", "\\,"));
          }
          printStream.println();
        }
        printStream.println();
      }
    } finally {
      printStream.close();
    }
  }
}
