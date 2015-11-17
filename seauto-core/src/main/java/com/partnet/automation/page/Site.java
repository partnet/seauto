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

package com.partnet.automation.page;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.partnet.automation.HtmlView;

/**
 * Provides functionality that should be accessible anywhere within a given
 * application (with very few, if any, exceptions).
 *
 * @author fpedroza
 * @author bbarker
 * @since Dec 30, 2014
 */
public abstract class Site
    extends HtmlView
{

  private static final Logger LOG = LoggerFactory.getLogger(Site.class);

  private static final Pattern baseUrlPattern = Pattern.compile("((https?://)?(((\\.)?(\\w|-)?)+)(\\:\\d+)?)");

  public Site(WebDriver webDriver)
  {
    super(webDriver);
  }

  abstract protected By getPrimaryWindowSelector();

  abstract protected String getDefaultUrl();

  /**
   * Used after closing dialogs or other popup windows for switching focus to
   * the primary window.
   */
  public void switchToPrimaryWindow()
  {
    switchToOpenWindow(getPrimaryWindowSelector(), null);
  }

  /**
   * Open the home page.
   */
  public void open()
  {
    // final String defaultUrl = getDefaultUrl();
    this.open("http://www.bing.com");
  }

  /**
   * Open/navigate-to a given url.
   * 
   * @param url
   *          - the url to go to
   */
  public void open(final String url)
  {
    LOG.info("Attempting to go to url: {}", url);

    // TODO: Apr 19, 2014 (bbarker) - Remove debug code when bug is found
    /*
     * Used to try and find a getting page bug... List<String> urls = new
     * ArrayList<>(); urls.add("https://dod-emall-dev.csd.disa.mil/acct/");
     * urls.add("https://dod.emall-dev.dla.mil/acct/");
     * urls.add("https://dod.emall-stage.dla.mil/acct/");
     * urls.add("https://emall-dev.part.net/acct/");
     * //urls.add("http://docs.seleniumhq.org/");
     * urls.add("https://teamcity.part.net/");
     * urls.add("https://issues.part.net/");
     * urls.add("https://issues.part.net/confluence");
     * urls.add("https://issues.part.net/timecard");
     * urls.add("http://fredo.part.net:8080/jbehave-data-provider/");
     * urls.add("http://blowfish.part.net/");
     * 
     * Stopwatch stop = new Stopwatch(); for(int i = 0; i < 20; i++) {
     * for(String urlTest : urls) { stop.start(); LOG.debug("nav to {}",
     * urlTest); this.webDriver.get(urlTest); stop.stop();
     * LOG.debug("\ttime: {}", stop.toString()); stop.reset(); } }
     */

    try {
      this.webDriver.navigate().to(url);
    }
    catch (TimeoutException e) {
      LOG.error("hmm.. navigate().to(url) says page never loaded.. refresh!");
      // TODO: Jul 7, 2014 (bbarker) - Check the logs often for the above error
      // statement.
      // I really do not like the idea of refreshing the web browser, but this
      // was one issue that seemed
      // to occur with selenium grid. However, I haven't seen it recently.
      webDriver.navigate().refresh();
      // if the page dosen't actually load, the timeout will be thrown here
      waitForPageToLoad();

      StringBuilder sb = new StringBuilder();
      sb.append("\n*****************************************************************\n").append("*****************************************************************\n").append("There was a timeout exception with the navigate().to(url) method in selenium!!\n")
          .append("*****************************************************************\n").append("*****************************************************************\n");

      LOG.error(sb.toString());
    }
  }

  /**
   * Open/navigate-to a url relative to the current base url.
   * 
   * @param relativeUrl
   *          - relative url to navigate to. Example:
   *          <code>/acct/orders</code>
   */
  public void openRelative(final String relativeUrl)
  {
    String baseUrl = getBaseUrl(this.getCurrentPageUrl());

    this.open(baseUrl + relativeUrl);
  }

  /**
   * Get the title of the current page.
   */
  public String getCurrentPageTitle()
  {
    return webDriver.getTitle();
  }

  public String getCurrentPageUrl()
  {
    return webDriver.getCurrentUrl();
  }

  /**
   * Refreshes the current page. Use sparingly for pulling info after a database
   * update.
   */
  public void refreshPage()
  {
    webDriver.navigate().refresh();
    waitForPageToLoad();
  }

  /**
   * Obtains the base URL of the given url.
   * 
   * @param url
   * @return
   */
  public String getBaseUrl(String url)
  {
    LOG.debug("find base url of '{}'", url);
    Matcher baseUrlMatch = baseUrlPattern.matcher(url);

    if (baseUrlMatch.find()) {
      String baseUrl = baseUrlMatch.group(0);
      LOG.debug("return base url '{}'", baseUrl);

      if (baseUrl.length() > 0) return baseUrlMatch.group(0);
      else throw new IllegalArgumentException(String.format("Could not determine base url for '%s'", url));
    }
    else {
      throw new IllegalArgumentException(String.format("Could not determine base url for '%s'", url));
    }

  }

}
