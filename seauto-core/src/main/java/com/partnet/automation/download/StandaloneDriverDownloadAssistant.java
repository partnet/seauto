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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.partnet.automation.Browser;
import com.partnet.automation.util.PathUtils;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class StandaloneDriverDownloadAssistant
{

  private static final Logger LOG = LoggerFactory.getLogger(StandaloneDriverDownloadAssistant.class);

  Map<Browser, Properties> downloadProperties = new HashMap<>();

  private static final Pattern FILENAME_PATTERN = Pattern.compile("([^/])+$");

  private static final String TAR_BZ2_EXTENSION = ".tar.bz2";

  private static final String ZIP_EXTENSION = ".zip";

  // public to append extension to default driver name in other places
  public static final String EXE_EXTENSION = ".exe";

  private static final String OS_PROP = "test.config.testos";

  // protected for testing purposes
  protected static final String SEAUTO_DOWNLOAD_DIR = System.getProperty("user.dir") + File.separator + "target" + File.separator + "seauto-download-manager" + File.separator;

  // public to allow AbstractConfigurableDriverProvider access for a default
  // location to search for drivers
  public static final String SEAUTO_BIN_DIR = System.getProperty("user.dir") + File.separator + "selenium-drivers" + File.separator;

  @Test
  public void test_removeMeWhenFinished()
  {
    try {
      downloadDriverFor(Browser.PHANTOMJS);
      downloadDriverFor(Browser.CHROME);
      downloadDriverFor(Browser.IE);
    }
    catch (IOException e) {
      e.printStackTrace();
      throw new IllegalArgumentException("There was a problem downloading resources!", e);
    }
  }

  // TODO: Mar 18, 2015 (bbarker) - Is there a better way to add items to these
  // maps?
  private static final String DEFAULT_EXTRACTED_CHROME_BIN_NAME = "chromedriver";

  private final Map<OperatingSystem, String> chromeUrlMap = new HashMap<>();
  {

    chromeUrlMap.put(OperatingSystem.MAC, "http://chromedriver.storage.googleapis.com/2.14/chromedriver_mac32.zip");
    chromeUrlMap.put(OperatingSystem.LINUX_64, "http://chromedriver.storage.googleapis.com/2.14/chromedriver_linux64.zip");
    chromeUrlMap.put(OperatingSystem.LINUX_32, "http://chromedriver.storage.googleapis.com/2.14/chromedriver_linux32.zip");
    chromeUrlMap.put(OperatingSystem.WINDOWS, "http://chromedriver.storage.googleapis.com/2.14/chromedriver_win32.zip");
  }

  private static final String DEFAULT_EXTRACTED_IE_BIN_NAME = "IEDriverServer";

  private Map<OperatingSystem, String> ieUrlMap = new HashMap<>();
  {
    ieUrlMap.put(OperatingSystem.WINDOWS, "http://selenium-release.storage.googleapis.com/2.45/IEDriverServer_Win32_2.45.0.zip");
  }

  private static final String DEFAULT_EXTRACTED_PHANTOMJS_BIN_NAME = "phantomjs";

  private final Map<OperatingSystem, String> phantomjsUrlMap = new HashMap<>();
  {
    phantomjsUrlMap.put(OperatingSystem.MAC, "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.0.0-macosx.zip");
    phantomjsUrlMap.put(OperatingSystem.LINUX_64, "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-linux-x86_64.tar.bz2");
    phantomjsUrlMap.put(OperatingSystem.LINUX_32, "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.8-linux-i686.tar.bz2");
    phantomjsUrlMap.put(OperatingSystem.WINDOWS, "https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.0.0-windows.zip");
  }

  /**
   * Downloads the driver for the given browser.
   * 
   * @param browser browser to obtain binary for
   * @return the file of the binary downloaded
   * @throws IOException if there is a issue with the binary
   */
  public File downloadDriverFor(Browser browser)
      throws IOException
  {
    String osProp = System.getProperty(OS_PROP);

    // property should only be changed for testing purposes
    OperatingSystem os = osProp == null ? OperatingSystem.getCurrentOs() : OperatingSystem.valueOf(osProp);

    switch (browser) {
      case CHROME:
        handleBinary(os, getBinaryName(Browser.CHROME, DEFAULT_EXTRACTED_CHROME_BIN_NAME), resolveUrl(os, browser, chromeUrlMap.get(os)));
        break;

      case IE:
        // only support Windows
        String ieUrl = resolveUrl(os, browser, ieUrlMap.get(os));
        if (ieUrl == null) {
          LOG.warn("IE driver will not be downloaded for the current OS '{}' because it is not supported!", os);
          break;
        }
        handleBinary(os, getBinaryName(Browser.IE, DEFAULT_EXTRACTED_IE_BIN_NAME), ieUrl);
        break;

      case PHANTOMJS:
        handleBinary(os, getBinaryName(Browser.PHANTOMJS, DEFAULT_EXTRACTED_PHANTOMJS_BIN_NAME), resolveUrl(os, browser, phantomjsUrlMap.get(os)));
        break;
      case HTMLUNIT: // fall though
      case FIREFOX:
        LOG.debug("There is no need to download a driver for {}, return", browser);
        break;
      default:
        LOG.debug("Downloading driver for {} is not yet supported!", browser);
        break;
    }

    return null;

  }

  private String getBinaryName(Browser browser, String defaultName)
  {
    return System.getProperty(String.format("test.config.%s.bin.name", browser.name().toLowerCase()), defaultName);
  }

  /**
   * Obtain the download url by using the default URL, or url from the property
   * 
   * @param browser browser to resolve URL
   * @param os operating system to get binary for
   * @param defaultUrl url location of the binary
   * @return the url used to download the binary
   */
  private String resolveUrl(OperatingSystem os, Browser browser, String defaultUrl)
  {
    String url = System.getProperty(String.format("test.config.%s.bin.url.%s", browser.name().toLowerCase(), os.name().toLowerCase()), defaultUrl);
    LOG.info("Use url : {}", url);

    return url;
  }

  private void handleBinary(OperatingSystem os, String fileName, String url)
      throws IOException
  {
    fileName = determineFileExtension(fileName, os);

    if (binaryAlreadyExists(fileName)) {
      LOG.info("{} binary already exists! Skip downloading", fileName);
      return;
    }

    downloadAndUnpack(url);
    moveFileToSeleniumDirAndSetExecutable(PathUtils.getFileInPath(SEAUTO_DOWNLOAD_DIR, fileName));
  }

  private boolean binaryAlreadyExists(String fileName)
  {
    return new File(SEAUTO_BIN_DIR + fileName).exists();
  }

  private boolean moveFileToSeleniumDirAndSetExecutable(File fileToMove)
  {

    File moveTo = new File(SEAUTO_BIN_DIR + fileToMove.getName());
    moveTo.getParentFile().mkdirs();
    fileToMove.setExecutable(true);
    LOG.info("Move binary to {}", moveTo);
    return fileToMove.renameTo(moveTo);
  }

  /**
   * 
   * @param filename
   *          - name of the file to determine if the .exe is needed
   * @param os
   *          - the current operating system
   * @return the file's extension
   */
  private String determineFileExtension(String filename, OperatingSystem os)
  {
    return (os == OperatingSystem.WINDOWS) ? filename + EXE_EXTENSION : filename;
  }

  /**
   * Downloads the file for the given URL, and attemps to unpack it.
   * 
   * Currently unpacking is only supported for .zip and .tar.bz2 files.
   * 
   * @param url url to download
   * @throws IOException
   */
  private void downloadAndUnpack(String url)
      throws IOException
  {

    // download
    File downloadFile = download(url);

    // unpack
    String downloadFileName = downloadFile.getName();
    LOG.info("Extract {}...", downloadFile.getName());
    if (downloadFileName.endsWith(TAR_BZ2_EXTENSION)) {
      extractBzip(downloadFile);
    }
    else
      if (downloadFileName.endsWith(ZIP_EXTENSION)) {
        extractZip(downloadFile);
      }
      else {
        LOG.debug("Could not determine if {} needed to be unpacked", downloadFileName);
      }
  }

  /**
   * Downloads the file for the given string URL
   * 
   * @param url url to download
   * @return the {@link File} of the download
   * @throws IOException
   */
  private File download(String url)
      throws IOException
  {

    URL website = new URL(url);

    Matcher filenameMatcher = FILENAME_PATTERN.matcher(url);

    if (!filenameMatcher.find()) {
      throw new IllegalArgumentException(String.format("Could not determine filename for URL '%s'", url));
    }

    // download to target directory
    File downloadFile = new File(SEAUTO_DOWNLOAD_DIR + filenameMatcher.group());

    LOG.debug("Output filename: {}, Directories created: {}", downloadFile.getAbsolutePath(), downloadFile.getParentFile().mkdirs());

    try (FileOutputStream fileOut = new FileOutputStream(downloadFile)) {
      LOG.info("Download {}...", downloadFile.getName());
      ReadableByteChannel rbc = Channels.newChannel(website.openStream());

      fileOut.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
      LOG.debug("Download complete");
    }

    return downloadFile;
  }

  private void extractBzip(File downloadFile)
      throws IOException
  {
    try (FileInputStream inputSkipTwo = new FileInputStream(downloadFile)) {
      // see javadoc for CBZip2InputStream
      // first two bits need to be skipped
      inputSkipTwo.read();
      inputSkipTwo.read();

      LOG.debug("Extract tar...");
      try (TarInputStream tarIn = new TarInputStream(new CBZip2InputStream(inputSkipTwo))) {
        for (TarEntry entry = tarIn.getNextEntry(); entry != null; entry = tarIn.getNextEntry()) {
          LOG.debug("Extracting {}", entry.getName());

          File extractedFile = new File(downloadFile.getParent() + File.separator + entry.getName());
          extractEntry(extractedFile, entry.isDirectory(), tarIn);
        }
      }
    }
  }

  private void extractZip(File downloadFile)
      throws IOException
  {

    try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(downloadFile))) {
      for (ZipEntry entry = zipIn.getNextEntry(); entry != null; entry = zipIn.getNextEntry()) {
        LOG.debug("Extracting {}", entry.getName());

        File extractedFile = new File(downloadFile.getParent() + File.separator + entry.getName());
        extractEntry(extractedFile, entry.isDirectory(), zipIn);
      }
    }
  }

  private void extractEntry(File file, boolean isDirectory, InputStream inputStream)
      throws IOException
  {

    if (!file.getParentFile().exists()) {
      file.getParentFile().mkdirs();
    }

    if (isDirectory) {
      file.mkdirs();
      return;
    }

    try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
      byte[] bytesIn = new byte[1024];
      int read = 0;

      while ((read = inputStream.read(bytesIn)) != -1) {
        bos.write(bytesIn, 0, read);
      }
    }
  }

}
