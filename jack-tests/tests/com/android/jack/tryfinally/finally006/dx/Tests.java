/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.jack.tryfinally.finally006.dx;

import com.android.jack.tryfinally.finally006.jack.Finally006;

import junit.framework.Assert;

import org.junit.Test;

public class Tests {

  @Test
  public void test001() {
    Finally006 finally006 = new Finally006();
    finally006.get(1);
    Assert.assertEquals(1, finally006.result);
  }

}