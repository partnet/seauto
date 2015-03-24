/*
 * Copyright 2015 Partnet, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.partnet.automation.download;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.partnet.automation.Browser;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class TestUnpack
{

  private static final String DRIVER_NAME = "DriverBin";

  private static final String BIN_CONTENT = "This is a dummy driver binary\n";

  @After
  public void tearDown()
  {
    System.setProperty("test.config.phantomjs.bin.url.linux_64", "");
    System.setProperty("test.config.phantomjs.bin.name", "");
    System.setProperty("test.config.testos", "");
    // be sure the binaries don't exist from different tests

    // delete fake binary
    File bin = new File(StandaloneDriverDownloadAssistant.SEAUTO_BIN_DIR + DRIVER_NAME);
    bin.delete();

    // delete folder
    File folder = new File(StandaloneDriverDownloadAssistant.SEAUTO_BIN_DIR);
    folder.delete();

  }

  @Test
  public void test_zip()
      throws IOException
  {
    StandaloneDriverDownloadAssistant driverAssist = new StandaloneDriverDownloadAssistant();
    String path = TestUnpack.class.getResource("testzip.zip").getPath().toString();

    System.setProperty("test.config.testos", "LINUX_64");
    System.setProperty("test.config.phantomjs.bin.url.linux_64", "file:" + path);
    System.setProperty("test.config.phantomjs.bin.name", DRIVER_NAME);

    driverAssist.downloadDriverFor(Browser.PHANTOMJS);

    File bin = new File(StandaloneDriverDownloadAssistant.SEAUTO_BIN_DIR + DRIVER_NAME);
    String content = new String(Files.readAllBytes(bin.toPath()));

    Assert.assertEquals("The .zip binary was not extracted correctly!", BIN_CONTENT, content);
  }

  @Test
  public void test_bz2()
      throws IOException
  {
    StandaloneDriverDownloadAssistant driverAssist = new StandaloneDriverDownloadAssistant();
    String path = TestUnpack.class.getResource("testbz2.tar.bz2").getPath().toString();
    System.setProperty("test.config.testos", "LINUX_32");
    System.setProperty("test.config.phantomjs.bin.url.linux_32", "file:" + path);
    System.setProperty("test.config.phantomjs.bin.name", DRIVER_NAME);

    driverAssist.downloadDriverFor(Browser.PHANTOMJS);

    File bin = new File(StandaloneDriverDownloadAssistant.SEAUTO_BIN_DIR + DRIVER_NAME);
    String content = new String(Files.readAllBytes(bin.toPath()));

    Assert.assertEquals("The tar.bz2 binary was not extracted correctly!", BIN_CONTENT, content);
  }
}
