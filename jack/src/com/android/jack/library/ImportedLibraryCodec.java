/*
 * Copyright (C) 2015 The Android Open Source Project
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

package com.android.jack.library;

import com.android.sched.util.codec.CodecContext;
import com.android.sched.util.codec.OrCodec;
import com.android.sched.util.codec.ParsingException;
import com.android.sched.util.codec.StringCodec;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

/**
 * A {@link StringCodec} is used to create an instance of an {@link InputLibrary} representing a
 * library that needs to be imported.
 */
public class ImportedLibraryCodec extends OrCodec<InputLibrary> {

  @SuppressWarnings("unchecked")
  public ImportedLibraryCodec() {
    super(new InputJackLibraryCodec(), new JarLibraryCodec());
  }

  @Override
  @CheckForNull
  public InputLibrary checkString(@Nonnull CodecContext context, @Nonnull String string)
      throws ParsingException  {
    StringCodec<? extends InputLibrary> jackLibCodec = codecList.get(0);
    StringCodec<? extends InputLibrary> jarCodec = codecList.get(1);
    try {
      return jackLibCodec.checkString(context, string);
    } catch (ParsingException e) {
      Throwable cause = e.getCause();
      if (cause instanceof LibraryVersionException || cause instanceof LibraryFormatException) {
        // do not try other codec, just fail
        throw e;
      }
      try {
        return jarCodec.checkString(context, string);
      } catch (ParsingException e2) {
        // neither codec succeeded, return the error of the first one
        return jackLibCodec.checkString(context, string);
      }
    }
  }

  @Override
  @Nonnull
  public String getUsage() {
    return "a path to a Jack library or a Jar";
  }

  @Override
  @Nonnull
  public String getVariableName() {
    return "jack-or-jar";
  }

  @Override
  @Nonnull
  public String formatValue(@Nonnull InputLibrary data) {
    return data.getPath();
  }

}
