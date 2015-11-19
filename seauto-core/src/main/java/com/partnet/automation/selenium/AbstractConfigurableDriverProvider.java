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

package com.partnet.automation.selenium;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.partnet.automation.Browser;
import com.partnet.automation.RuntimeConfiguration;
import com.partnet.automation.download.OperatingSystem;
import com.partnet.automation.download.StandaloneDriverDownloadAssistant;

/**
 * Delegating implementation that provides {@link WebDriver} instances specified
 * by various means.
 */
public abstract class AbstractConfigurableDriverProvider
    implements DriverProvider
{

  // too bad we can't inject the runConfig into abstract classes...
  protected RuntimeConfiguration runConfig = RuntimeConfiguration.getInstance();

  private ThreadLocal<WebDriver> delegate = new ThreadLocal<WebDriver>();

  private static final Logger LOG = LoggerFactory.getLogger(AbstractConfigurableDriverProvider.class);

  // TODO: Feb 4, 2015 (bbarker) - Fix proxy setting here
  protected final String USE_PROXY_BY_DEFAULT = "test.config.default.useProxy";
  protected final String BROWSER_SYSTEM_PROPERTY_NAME = "test.config.browser";
  protected final String SELENIUM_REMOTE_URL = "test.config.selenium.url";
  protected final String REMOTE_WEBDRIVER_RETRY_ATTEMPTS = "remote.webdriver.retry.attempts";
  protected final String REMOTE_WEBDRIVER_RETRY_PAUSE_MILLIS = "remote.webdriver.retry.pause.millis";
  protected final String PAGE_LOAD_TIMEOUT_SECONDS = "test.config.page.load.timeout";
  protected final String PHANTOM_JS_BIN_PROP = "test.config.driver.phantomjs.bin";
  protected final String CHROME_DRIVER_BIN_PROP = "test.config.driver.chrome.bin";
  protected final String IE_DRIVER_BIN_PROP = "test.config.driver.ie.bin";
  protected final String ALLOW_SCREENSHOTS = "test.config.driver.screenshots.allow";
  protected final String DRIVER_BIN_PATH_APPEND = ".path";
  protected final String WINDOWS_APPEND = ".windows";
  protected final String MAC_APPEND = ".mac";
  protected final String ANDROID_APP_PKG = "test.config.android.app.package";
  protected final String ANDROID_APP_WAIT_PKG = "test.config.android.app.wait.package";
  protected final String ANDROID_PLATFORM_NAME = "test.config.android.platform.name";
  protected final String ANDROID_PLATFORM_VERSION = "test.config.android.platform.version";
  protected final String ANDROID_SELENDROID_PORT = "test.config.android.selendroid.port";
  protected final String ANDROID_DEVICE_NAME = "test.config.android.device.name";
  protected final String ANDROID_APK_PATH = "test.config.android.apk.path";
  protected final String ANDROID_BROWSER_NAME = "android";


  /**
   * Get a {@link WebDriver} for the specified {@link Browser}
   * 
   * For a quick start, use
   * {@link AbstractConfigurableDriverProvider#getDefaultWebDriver(Browser)}
   * 
   * @param browser desired browser for test
   *                
   * @return handle to {@link WebDriver}
   */
  protected abstract WebDriver getWebDriver(Browser browser);

  /**
   * This is the default implementation of getting a web driver.
   * 
   * Use this as a quick start guide for
   * {@link AbstractConfigurableDriverProvider#getWebDriver(Browser)}
   * 
   * @param browser browser to obtain
   * @return instance of the specific WebDriver
   */
  protected WebDriver getDefaultWebDriver(Browser browser)
  {
    Objects.requireNonNull(browser, "browser cannot be null");

    WebDriver driver;
    boolean useRemoteBrowser = (getRemoteSeleniumUrlString() != null);

    LOG.debug("Initialize: '{}' using remote browser: '{}'", browser, useRemoteBrowser);
    switch (browser) {
      case IE:
        driver = useRemoteBrowser ? getRemoteInternetExplorerWebDriver() : getInternetExplorerWebDriver();
        break;

      case CHROME:
        driver = useRemoteBrowser ? getRemoteChromeWebDriver() : getChromeWebDriver();
        break;

      case FIREFOX:

        driver = useRemoteBrowser ? getRemoteFirefoxWebDriver() : getFirefoxWebDriver();
        break;

      case PHANTOMJS:
        driver = useRemoteBrowser ? getRemotePhantomJsWebDriver() : getPhantomJsWebDriver();
        break;

      case HTMLUNIT:
        if (useRemoteBrowser) {

          // Don't throw here, in case running with a Grid.
          LOG.info("Running HTMUNIT on the grid is NOT supported! If you want to take the time to create " + "a HTTP connection to the grid, you might as well just use PhantomJs. ");
        }

        driver = getHtmlUnitWebDriver();
        break;

      case ANDROID:
        driver = getRemoteAndroidWebDriver();
        break;

      default:
        throw new IllegalArgumentException(String.format("Unsupported browser: '%s'", browser.name()));
    }

    LOG.debug("Using WebDriver implementation of type : {}", driver.getClass());

    if(!browser.isAndroid()) {
      driver.manage().timeouts().pageLoadTimeout(Integer.getInteger(PAGE_LOAD_TIMEOUT_SECONDS, 120), TimeUnit.SECONDS);
    }

    return driver;
  }

  /**
   * Sets the given web driver to the current thread
   *
   * @param driver {@link WebDriver} to set for current thread
   */
  protected void set(WebDriver driver)
  {
    this.delegate.set(driver);
  }

  @Override
  public WebDriver get()
  {
    WebDriver driver = this.delegate.get();

    if (driver == null) {
      LOG.warn("driver on this thread has not been launched!");
    }
    return driver;
  }

  @Override
  public void end()
  {
    // suggestion to close web driver before quitting to prevent socket lock on
    // 7054
    // https://code.google.com/p/selenium/issues/detail?id=7272
    // https://code.google.com/p/selenium/issues/detail?id=4790
    WebDriver driver = this.get();
    if (driver != null) {
      LOG.debug("Stopping driver");

      //closing android browser is not supported
      if(!Browser.getBrowser(driver).isAndroid()) {
        driver.close();
      }

      this.get().quit();
      this.set(null);
    }
    else {
      LOG.debug("No driver to stop");
    }
  }

  /**
   * @see #launch(Browser)
   */
  @Override
  public void launch()
  {
    launch(null);
  }

  /**
   * Initialize this {@link DriverProvider} for the current running thread. If
   * no value (null) is specified, the default browser is used.
   * 
   * @param browser
   *          - The browser to use, unless a higher priority browser as defined
   *          by {@link #getBrowserFromProperty()} is set.
   * 
   * @see #getDefaultBrowser()
   */
  public void launch(Browser browser)
  {
    // system property value takes priority
    Browser browserFromProp = getBrowserFromProperty();

    if (browserFromProp != null) {
      LOG.debug("Browser from property in use: '{}'", browserFromProp.name());
    }
    else {
      if (browser == null) {
        browserFromProp = getDefaultBrowser();
        LOG.debug("Default browser in use: '{}'", browserFromProp.name());
      }
      else {
        browserFromProp = browser;
        LOG.debug("Using specified browser: '{}'", browserFromProp.name());
      }
    }

    Objects.requireNonNull(browserFromProp, "browser to use cannot be null");

    end();
    this.set(getWebDriver(browserFromProp));
  }

  /**
   * Captures the html of the webdriver, and writes it to the specified path.
   * <p>
   * There is also the option of replacing all of the relative paths with a
   * given base url of the site so the page can be rendered when opened with a
   * browser.
   * 
   * @param htmlPath
   *          - path and filename of where to save the html file to.
   * @param baseUrl
   *          - replace relative path of html with this base url. If it is null
   *          or blank, it will skip replacing the relative paths.
   */
  public void saveHtml(String htmlPath, String baseUrl)
  {
    if (StringUtils.isNotBlank(baseUrl) && !baseUrl.endsWith("/")) {
      baseUrl += "/";
    }

    LOG.debug("Write html to: {}", htmlPath);

    String pageSource = this.get().getPageSource();

    if (StringUtils.isNotBlank(baseUrl)) {
      pageSource = pageSource.replaceAll("=(\\s)?\"/", "=\"" + baseUrl);
    }

    try {
      // write to file, replacing relative path with something that it will find
      // and render
      FileUtils.write(new File(htmlPath), pageSource, "iso-8859-1");
    }
    catch (IOException e) {
      LOG.error("Error writing html to '{}'!", htmlPath, e);
    }
  }

  /**
   * Screenshooter for HTMLUnit. It saves the html source to disk following the
   * same pattern as the screenshot path. The HTMLUnit session is transfered to
   * PhantomJs, which takes the screenshot, and is destroyed. The original
   * driver is not destroyed
   * 
   * Note: Javascript events, current page changes, etc.. are not saved and are
   * not captured in the screenshots taken.
   * 
   * @param path
   *          - where to save the file. This assumes a png file will be
   *          generated
   * @param baseUrl
   *          - used to transfer the cookies to the phantomjs driver properly.
   * 
   * @see #getPhantomJsWebDriver()
   */
  public void saveScreenshotForHtmlUnit(String path, String baseUrl)
  {
    final WebDriver driver = this.get();

    if (!(driver instanceof HtmlUnitDriver)) {
      LOG.warn("Wrong driver called screenshooter for HTMLUnit driver, default to regular screenshooter");
      this.saveScreenshotAs(path);
      return;
    }

    PhantomJSDriver phantomJs = (PhantomJSDriver) getPhantomJsWebDriver();

    try {
      phantomJs.get(baseUrl);
      String url = driver.getCurrentUrl();
      LOG.debug("Url: {}", url);

      for (Cookie cookie : driver.manage().getCookies()) {
        LOG.debug("Cookie: {}", cookie.toString());
        phantomJs.manage().addCookie(cookie);
      }

      phantomJs.get(url);

      // set current thread to phantomjs, and take screenshot in the default way
      this.set(phantomJs);
      LOG.debug("HTML Screenshot taken: {}", this.saveScreenshotAs(path));
    }
    finally {
      // set back original driver for this thread
      this.set(driver);

      phantomJs.close();
      phantomJs.quit();
    }
  }

  /**
   * Takes screenshot of the current driver. Screeshot will be skipped if
   * test.config.driver.screenshots.allow is set to false.
   * 
   * @param path location where the screenshot should be saved at
   * @return true if succeeded, false otherwise
   */
  @Override
  public boolean saveScreenshotAs(String path)
  {

    if(!Boolean.valueOf(System.getProperty(ALLOW_SCREENSHOTS, "true"))) {
      LOG.debug("Skipping screenshot");
      return false;
    }

    WebDriver driver = this.get();

    if (driver instanceof HtmlUnitDriver) {
      LOG.info("Additional work needs to be done to take screenshots with HTMLUnit driver, skipping. " + "Email the SeAuto user list for more information.");
      return false;
    }

    if (driver instanceof TakesScreenshot) {
      File dstFile = new File(path);
      File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
      try {
        FileUtils.copyFile(srcFile, dstFile);
      }
      catch (IOException e) {
        throw new RuntimeException("Can't save screenshot!", e);
      }
      return true;
    }
    return false;

  }

  /**
   * This returns the value stored in the browser property, not the browser for
   * the current thread.
   * 
   * @return Browser, or null if a browser is not defined
   * @see #BROWSER_SYSTEM_PROPERTY_NAME
   */
  protected Browser getBrowserFromProperty()
  {
    String browser = System.getProperty(BROWSER_SYSTEM_PROPERTY_NAME);

    return browser == null ? null : Browser.valueOfByName(browser.toUpperCase());
  }

  /**
   * Used to indicate whether or not a proxy should be used by default.
   * 
   * @see #USE_PROXY_BY_DEFAULT
   * @return true if using proxy, false otherwise
   */
  protected boolean useProxy()
  {
    boolean useProxy = true;
    String useProxyValue = System.getProperty(USE_PROXY_BY_DEFAULT);
    if (useProxyValue != null) {
      useProxy = Boolean.getBoolean(USE_PROXY_BY_DEFAULT);
      LOG.info("by default, useProxy: {}", useProxy);
    }
    return useProxy;
  }

  /**
   * Get the browser to use by default; HTMLUNIT is the optimistic default.
   * @return the default browser to use, if one is not defined.
   */
  protected Browser getDefaultBrowser()
  {
    return Browser.HTMLUNIT;
  }

  /**
   * Get the value of the remote URL to connect to.
   *
   * @return null if the system property {@link #SELENIUM_REMOTE_URL} is blank, otherwise returns the {@link URL}
   * @see #SELENIUM_REMOTE_URL
   * @throws IllegalArgumentException if the {@link #SELENIUM_REMOTE_URL} is malformed.
   */
  protected URL getRemoteSeleniumUrl()
  {
    URL url;
    String stringUrl = getRemoteSeleniumUrlString();
    try {
      url = new URL(stringUrl);
    } catch (MalformedURLException e) {
      LOG.error("invalid url: {}", stringUrl, e);
      throw new IllegalStateException(String.format("The url '%s' is malformed!", stringUrl), e);
    }
    return url;
  }

  /**
   * Returns the string property for {@link #SELENIUM_REMOTE_URL}
   * @return the url in string form, or null if it does not exist.
   */
  protected String getRemoteSeleniumUrlString() {
    return StringUtils.trimToNull(System.getProperty(SELENIUM_REMOTE_URL));
  }

  /**
   * Helper method to launch remote web driver.
   *
   * At times there are issues with starting the remote web driver. For firefox,
   * problems with locking port 7054 can arise.
   *
   * See the Selenium <a
   * href="https://code.google.com/p/selenium/issues/detail?id=4790">bug</a> for
   * more info
   *
   * A special case needs to be performed for the Android browser, since it can not be cast to {@link RemoteWebDriver}
   *
   * @param capabilities web driver capabilities.
   * @return {@link WebDriver} instance of the remote web driver
   *
   */
  protected WebDriver initRemoteWebDriver(DesiredCapabilities capabilities)
  {
    URL remoteUrl = getRemoteSeleniumUrl();
    LOG.debug("Remote Selenium URL: {}", remoteUrl.toString());
    WebDriver driver = null;
    boolean isAndroid = false;
    int tries = 1;

    if(capabilities.getCapability(CapabilityType.BROWSER_NAME).equals(ANDROID_BROWSER_NAME)) {
      isAndroid = true;
    }

    while (driver == null) {
      LOG.debug("Try {} {}", tries, capabilities.toString());

      try {

        if(isAndroid) {
          driver = new AndroidDriver(remoteUrl, capabilities);
        }
        else {
          driver = new RemoteWebDriver(remoteUrl, capabilities);
        }

      } catch (WebDriverException e) {
        LOG.error("Remote WebDriver was unable to start! " + e.getMessage(), e);

        if (tries >= Integer.getInteger(REMOTE_WEBDRIVER_RETRY_ATTEMPTS, 10)) {
          throw e;
        }

        sleep(Integer.getInteger(REMOTE_WEBDRIVER_RETRY_PAUSE_MILLIS, 5000) * tries);
        tries++;
        driver = null;
      }
    }

    if(!isAndroid) {
      // allow screenshots to be taken
      driver = new Augmenter().augment(driver);
    }

    // Allow files from the host to be uploaded to a remote browser
    ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());

    return driver;
  }


  /**
   * Simple Thread sleep method that catches InterruptException.
   * @param millis time (in milliseconds) to sleep
   */
  private void sleep(int millis)
  {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      LOG.error("Exception occurred sleeping", e);
    }
  }

  private boolean doesFileExist(final String path)
  {
    final File file = new File(path);
    return file.exists();
  }

  /**
   * Basic method to check if a system property exists
   * @param systemProperty the system property to check
   * @return boolean if system property exists - true if it does, false otherwise.
   */
  private boolean isSysPropAvailable(final String systemProperty) {
    return System.getProperty(systemProperty) != null;
  }

  /*
   * Default WebDriver implementations. These can be overridden by the base
   * class if needed
   */

  /**
   * Default implementation of getting a local Internet Explorer web driver.
   * 
   * @return the WebDriver
   */
  protected WebDriver getInternetExplorerWebDriver()
  {
    // Note: running ie9 32 bit driver is faster then 64 bit driver. Download
    // the preferred IEDriverServer.exe from selenium's website
    LOG.debug("Start IE");

    // Note: this driver is only used when running the tests locally in IE
    String ieDriverPath = getOsSpecificBinaryPathFromProp(IE_DRIVER_BIN_PROP, "IEDriverServer");

    if (StringUtils.isNotBlank(ieDriverPath) && doesFileExist(ieDriverPath)) {
      LOG.info("Use specified IE Driver path: {}", ieDriverPath);
      System.setProperty("webdriver.ie.driver", ieDriverPath);
    }
    else {
      LOG.debug("Use default IEDriverServer.exe location");
    }

    return new InternetExplorerDriver(getInternetExplorerCapabilities());
  }

  /**
   * Default implementation of getting a remote WebDriver instance
   * 
   * @return - the remote IE WebDriver
   */
  protected WebDriver getRemoteInternetExplorerWebDriver()
  {
    return initRemoteWebDriver(getInternetExplorerCapabilities());
  }

  /**
   * Best found default capabilities for Internet Explorer
   * 
   * @return {@link DesiredCapabilities}
   */
  protected DesiredCapabilities getInternetExplorerCapabilities()
  {
    final DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();

    // get past certificate security warning pages
    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

    // setup native events and window focus
    // http://jimevansmusic.blogspot.com/2012/06/whats-wrong-with-internet-explorer.html
    capabilities.setCapability(InternetExplorerDriver.NATIVE_EVENTS, true);
    capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);

    // don't accept alerts automatically
    capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, false);

    return capabilities;
  }

  /**
   * Default implementation of getting a local HTMLUnit Driver
   * 
   * @return {@link WebDriver} for HTMLUnit
   */
  protected WebDriver getHtmlUnitWebDriver()
  {
    // Set to firefox 24 to emulate a friendly javascript engine
    HtmlUnitDriver driver = new HtmlUnitDriver(BrowserVersion.FIREFOX_38);
    driver.setJavascriptEnabled(true);
    return driver;
  }

  /**
   * Default implementation throws UnsupportedOperationException
   * @return WebDriver instance
   */
  protected WebDriver getPhantomJsWebDriver()
  {
    String pathToBin = getOsSpecificBinaryPathFromProp(PHANTOM_JS_BIN_PROP, "phantomjs");

    DesiredCapabilities capabilities = getPhantomJsCapabilities();
    capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, pathToBin);

    return new PhantomJSDriver(capabilities);

  }

  /**
   * Helper method to get the correct driver binary for the given operating
   * system.
   * 
   * @param binProp system property that needs to be OS specific
   * @param def default property
   * @return binary property for the current OS
   */
  protected String getOsSpecificBinaryProperty(String binProp, String def)
  {
    OperatingSystem os = OperatingSystem.getCurrentOs();

    switch (os) {
      case MAC:
        binProp += MAC_APPEND;
        break;
      case WINDOWS:
        binProp += WINDOWS_APPEND;
        def += StandaloneDriverDownloadAssistant.EXE_EXTENSION;
        break;

      // default case
      case LINUX_32: // fall though
      case LINUX_64: // fall though
      default:
        break;
    }

    return System.getProperty(binProp, def);
  }

  /**
   * Helper method to locate the correct binary for the given driver.
   * 
   * 
   * @param binProp property to pull the binary name from
   * @param def default property
   * @return string containing the entire path and binary name
   */
  protected String getOsSpecificBinaryPathFromProp(String binProp, String def)
  {
    // append .path to the end of the prop, to see if a path is defined
    String path = System.getProperty(binProp + DRIVER_BIN_PATH_APPEND, StandaloneDriverDownloadAssistant.SEAUTO_BIN_DIR);

    String fileName = getOsSpecificBinaryProperty(binProp, def);
    File driverBin = new File(path + fileName);

    if (!driverBin.exists()) throw new IllegalStateException(String.format("The driver binary does not exist! Expected path to bin: %s", driverBin.getPath()));

    return path + fileName;
  }

  protected WebDriver getRemotePhantomJsWebDriver()
  {
    return initRemoteWebDriver(getPhantomJsCapabilities());
  }

  protected DesiredCapabilities getPhantomJsCapabilities()
  {
    DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
    return capabilities;
  }

  /**
   * Obtains the default firefox web driver.
   * 
   * @return {@link WebDriver} for the Firefox instance
   */
  protected WebDriver getFirefoxWebDriver()
  {

    final FirefoxBinary fb;
    String fireboxBinPath = runConfig.getFirefoxBinaryPath();

    if (fireboxBinPath != null) {
      LOG.info("Using Firefox binary: {}", fireboxBinPath);
      fb = new FirefoxBinary(new File(fireboxBinPath));
    }
    else {
      LOG.info("Use system default for the Firefox binary");
      fb = new FirefoxBinary();
    }

    final DesiredCapabilities capabilities = getFirefoxCapabilities();
    capabilities.setCapability(FirefoxDriver.BINARY, fb);

    return new FirefoxDriver(capabilities);
  }

  /**
   * Default remote firefox instance
   * @return {@link WebDriver} for the remote Firefox instance
   */
  protected WebDriver getRemoteFirefoxWebDriver()
  {
    final DesiredCapabilities capabilities = getFirefoxCapabilities();
    return initRemoteWebDriver(capabilities);
  }

  /**
   * Default firefox capabilities
   * @return capabilities for the Firefox driver.
   */
  protected DesiredCapabilities getFirefoxCapabilities()
  {
    return DesiredCapabilities.firefox();
  }

  protected WebDriver getChromeWebDriver()
  {
    String pathToDriverBin = getOsSpecificBinaryPathFromProp(CHROME_DRIVER_BIN_PROP, "chromedriver");

    System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, pathToDriverBin);
    DesiredCapabilities capabilities = DesiredCapabilities.chrome();

    return new ChromeDriver(capabilities);
  }

  protected WebDriver getRemoteChromeWebDriver()
  {
    // TODO Auto-generated method stub
    throw new IllegalStateException("Getting remote chrome driver is not implemented yet!");
  }

  /**
   * Configuration to get a handle to the Android driver
   * @return initialized instance of the {@link AndroidDriver}
   */
  private WebDriver getRemoteAndroidWebDriver() {

    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(CapabilityType.BROWSER_NAME, ANDROID_BROWSER_NAME);
    capabilities.setCapability("deviceName", System.getProperty(ANDROID_DEVICE_NAME, "Android"));
    capabilities.setCapability("platformName", System.getProperty(ANDROID_PLATFORM_NAME, "Android"));
    capabilities.setCapability("app", System.getProperty(ANDROID_APK_PATH));

    if (isSysPropAvailable(ANDROID_PLATFORM_VERSION)) {
      capabilities.setCapability("platformVersion", System.getProperty(ANDROID_PLATFORM_VERSION, "5.0"));
    }

    if (isSysPropAvailable(ANDROID_APP_PKG)) {
      capabilities.setCapability("appPackage", System.getProperty(ANDROID_APP_PKG));
    }

    if (isSysPropAvailable(ANDROID_APP_WAIT_PKG)) {
      capabilities.setCapability("appWaitPackage", System.getProperty(ANDROID_APP_WAIT_PKG));
    }

    RemoteWebDriver remoteDriver = (RemoteWebDriver) initRemoteWebDriver(capabilities);
    AndroidDriver driver = (AndroidDriver) remoteDriver;



    return driver;
  }

}
