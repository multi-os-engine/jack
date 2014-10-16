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

package com.android.jack.error;

import com.android.jack.IllegalOptionsException;
import com.android.jack.Main;
import com.android.jack.NothingToDoException;
import com.android.jack.test.helper.ErrorTestHelper;
import com.android.jack.test.toolchain.AbstractTestTools;
import com.android.jack.test.toolchain.JackApiToolchain;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * JUnit test checking Jack behavior on exceptions.
 */
public class CommandLineErrorTest {

  @BeforeClass
  public static void setUpClass() {
    Main.class.getClassLoader().setDefaultAssertionStatus(true);
  }

  /**
   * Checks that compilation fails correctly when an unsupported options is passed to ecj.
   */
  @Test
  public void testCommandLineError001() throws Exception {
    ErrorTestHelper ite = new ErrorTestHelper();

    JackApiToolchain jackApiToolchain = AbstractTestTools.getCandidateToolchain(JackApiToolchain.class);
    ByteArrayOutputStream errOut = new ByteArrayOutputStream();
    jackApiToolchain.setErrorStream(errOut);
    jackApiToolchain.addEcjArgs("-unsupported");
    try {
      jackApiToolchain.srcToExe(
          AbstractTestTools.getClasspathAsString(jackApiToolchain.getDefaultBootClasspath())
          + File.pathSeparator + ite.getJackFolder(), ite.getOutputDexFolder(),
          ite.getSourceFolder());
      Assert.fail();
    } catch (IllegalOptionsException e) {
      // Failure is ok since a bad options is passed to ecj.
    } finally {
      Assert.assertEquals("", errOut.toString());
    }
  }

  /**
   * Checks that compilation fails correctly when no source files are passed to ecj.
   */
  @Test
  public void testCommandLineError002() throws Exception {
    ErrorTestHelper ite = new ErrorTestHelper();

    JackApiToolchain jackApiToolchain = AbstractTestTools.getCandidateToolchain(JackApiToolchain.class);
    ByteArrayOutputStream errOut = new ByteArrayOutputStream();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    jackApiToolchain.setErrorStream(errOut);
    jackApiToolchain.setOutputStream(out);

    try {
      jackApiToolchain.srcToExe(
          AbstractTestTools.getClasspathAsString(jackApiToolchain.getDefaultBootClasspath())
          + File.pathSeparator + ite.getJackFolder(), ite.getOutputDexFolder(),
          ite.getSourceFolder());
      Assert.fail();
    } catch (NothingToDoException e) {
      // Failure is ok since there is no source files.
    } finally {
      Assert.assertEquals("", errOut.toString());
      Assert.assertTrue(out.toString().contains("Usage:"));
    }
  }

}