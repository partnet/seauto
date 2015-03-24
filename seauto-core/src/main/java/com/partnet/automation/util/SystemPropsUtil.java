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

package com.partnet.automation.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropsUtil
{

  private static final Logger log = LoggerFactory.getLogger(SystemPropsUtil.class);

  private SystemPropsUtil()
  {
    // No instance creation.
  }

  /**
   * Load properties from the given configuration file name.
   * <p/>
   * Given error message is displayed if file not found.
   * <p/>
   * 
   * @param configFileName
   *          - File name of properties file.
   * @throws RuntimeException
   *           - If the file has a error while reading the properties file
   */
  public static void loadProperties(String configFileName)
  {
    Properties cmdlineProps = System.getProperties();
    Properties fileProps = new Properties();

    // load property file
    InputStream inputStream = SystemPropsUtil.class.getResourceAsStream(configFileName);
    if (inputStream == null) {
      // force use of classloader to look for properties file
      inputStream = SystemPropsUtil.class.getClassLoader().getResourceAsStream(configFileName);
      if (inputStream == null) {
        log.warn(String.format("Failed to locate and load properties file: %s", configFileName));
        return;
      }
    }
    else {
      log.info("Properties file ({}) exists", configFileName);
    }

    // load fileProps with config.properties
    try {
      fileProps.load(inputStream);
    }
    catch (IOException e) {
      throw new RuntimeException(String.format("Failure occurred while reading properties file: %s", configFileName), e);
    }

    // Override any fileProp with cmdlineProps
    fileProps.putAll(cmdlineProps);
    System.setProperties(fileProps);
  }

  /**
   * Look for a required system property.
   * 
   * @param propertyName
   *          name of the property to get
   * @return value of the given property
   * @throws IllegalStateException
   *           if the property isn't defined or has an empty/blank value
   */
  public static String getRequiredProperty(final String propertyName)
  {
    String propertyValue = System.getProperty(propertyName);
    if (StringUtils.trimToNull(propertyValue) == null) {
      throw new IllegalStateException(String.format("Missing required property %s", propertyName));
    }
    return propertyValue;
  }

}
