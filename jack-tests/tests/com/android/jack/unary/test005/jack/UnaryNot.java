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

package com.android.jack.unary.test005.jack;

/**
 * JUnit test allowing to check that '!' support does not generate useless 'if' instructions.
 */
public class UnaryNot {

  // One 'if' instruction should be used into the generated code.
  public static int getValue1(int i1, int i2) {
    if (!(i1 < i2)) {
      return 1;
    } else {
      return 2;
    }
  }

  // Two 'if' instructions should be used into the generated code.
  public static int getValue2(int i1, int i2, int i3, int i4) {
    if (!(i1 < i2 && i3 < i4)) {
      return 1;
    } else {
      return 2;
    }
  }

  public static boolean flipBooleans(int count) {
    boolean result = true;
    for (int i = 0; i < count; i++) {
      result = !result;
    }
    return result;
  }

  public static boolean flipBooleansTwice(int count) {
    boolean result = true;
    for (int i = 0; i < count; i++) {
      result = !result;
      result = !result;
    }
    return result;
  }

  public static boolean flipBooleansWithDep(int count) {
    boolean result = true;
    for (int i = 0; i < count; i++) {
      if (result) {
        result = !result;
      }
    }
    return result;
  }

  public static boolean flipBooleansTwiceWithDep(int count) {
    boolean result = true;
    for (int i = 0; i < count; i++) {
      if (result) {
        result = !result;
        result = !result;
      }
    }
    return result;
  }
}

