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

package com.android.jack.optimizations.notsimplifier.test002.jack;

public class NotWithLT implements NotWithComparisonOperator {

  @Override
  public boolean testWithDouble(double n) {
    return !(n < 0);
  }

  @Override
  public boolean testWithFloat(float n) {
    return !(n < 0);
  }

  @Override
  public boolean testWithLong(long n) {
    return !(n < 0);
  }

  @Override
  public boolean testWithInt(int n) {
    return !(n < 0);
  }

  @Override
  public boolean testWithByte(byte n) {
    return !(n < 0);
  }

  @Override
  public boolean testWithChar(char n) {
    return !(n < 0);
  }

  @Override
  public boolean testWithShort(short n) {
    return !(n < 0);
  }
}
