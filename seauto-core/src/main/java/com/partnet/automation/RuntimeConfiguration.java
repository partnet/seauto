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

package com.partnet.automation;

import java.io.File;

import javax.enterprise.inject.Alternative;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.partnet.automation.util.SystemPropsUtil;

/**
 * Class responsible for returning sensible defaults to be used.
 * 
 * Should adhere to the following lookup logic to support both config.properties
 * and system properties. Lookup order:
 * <ul>
 * <li>Look for system property specified on command line.
 * <li>Look for property from config.property.
 * <li>Provider default or throw exception if a sensible default can not be
 * provided.
 * </ul>
 * 
 * @author <a href="mailto:rbascom@part.net">rbascom</a>
 */
@Alternative
public class RuntimeConfiguration
{

  private static final Logger LOG = LoggerFactory.getLogger(RuntimeConfiguration.class);

  private static final String STORIES_DIRECTORY_SYSTEM_PROPERTY_NAME = "test.config.story.dir";

  private static final String URL_SYSTEM_PROPERTY_NAME = "test.config.url";

  private static final String FIREFOX_PROFILE_DIRECTORY_SYSTEM_PROPERTY_NAME = "test.config.firefox.profile.directory";

  private static final String FIREFOX_BINARY_PATH_SYSTEM_PROPERTY_NAME = "firefoxBinaryPath";

  private static final String STORY_PATH_INCLUSION_SYSTEM_PROPERTY_NAME = "test.config.story";

  private static final String THREADS_SYSTEM_PROPERTY = "test.config.threads";

  private static final String DRY_RUN_SYSTEM_PROPERTY = "test.config.dryRun";

  private static final String AUTOMATION_CONFIG_FILENAME = "test.config.properties.filename";

  private static final String AUTOMATION_DEFAULT_CONFIG_FILENAME = "config.properties";

  private static final String FIREFOX_TEST_BIN = "firefoxTestBin";

  private static final RuntimeConfiguration instance = new RuntimeConfiguration();

  public static RuntimeConfiguration getInstance()
  {
    return RuntimeConfiguration.instance;
  }

  private RuntimeConfiguration()
  {
    final String configFile = getAutomationConfigFile();
    LOG.info("Attempting to load config file: {}", configFile);
    SystemPropsUtil.loadProperties(configFile);
  }

  public String getUrl()
  {
    return SystemPropsUtil.getRequiredProperty(URL_SYSTEM_PROPERTY_NAME);
  }

  public String getStoriesDirectory()
  {
    StringBuilder sb = new StringBuilder();
    return System.getProperty(STORIES_DIRECTORY_SYSTEM_PROPERTY_NAME, sb.append(System.getProperty("user.dir")).append(File.separator).append("target").append(File.separator).append("test-classes").append(File.separator).toString());
  }

  public String getFirefoxProfileDirectory()
  {
    return getProperty(FIREFOX_PROFILE_DIRECTORY_SYSTEM_PROPERTY_NAME);
  }

  public String getFirefoxBinaryPath()
  {
    String firefoxBinPath = null;

    firefoxBinPath = System.getProperty(FIREFOX_BINARY_PATH_SYSTEM_PROPERTY_NAME);

    if (firefoxBinPath == null) {
      firefoxBinPath = System.getProperty(FIREFOX_TEST_BIN);
    }

    // return whatever we've collected at this point, which may be null
    // if null, the default selenium driver behavior will look for firefox
    // installed on local system
    return firefoxBinPath;
  }

  /**
   * Get the story path inclusion string, to be passed into the
   * StoryFinder().findPaths
   */
  public String getStoryPathInclusion()
  {
    return "**/" + System.getProperty(STORY_PATH_INCLUSION_SYSTEM_PROPERTY_NAME, "*") + ".story";
  }

  public boolean doDryRun()
  {
    return Boolean.getBoolean(DRY_RUN_SYSTEM_PROPERTY);
  }

  public int getThreads()
  {
    return Integer.valueOf(System.getProperty(THREADS_SYSTEM_PROPERTY, "1"));
  }

  private String getAutomationConfigFile()
  {
    return System.getProperty(AUTOMATION_CONFIG_FILENAME, AUTOMATION_DEFAULT_CONFIG_FILENAME);
  }

  private String getProperty(String property)
  {
    return SystemPropsUtil.getRequiredProperty(property);
  }

}
