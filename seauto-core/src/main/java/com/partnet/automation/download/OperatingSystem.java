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

import java.util.Locale;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public enum OperatingSystem
{
  MAC, WINDOWS, LINUX_32, LINUX_64;

  private static OperatingSystem detectedOS;

  public static OperatingSystem getCurrentOs()
  {
    if (detectedOS == null) {

      String operatingSystem = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
      String arch = System.getProperty("os.arch");

      // http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
      if ((operatingSystem.indexOf("mac") >= 0) || (operatingSystem.indexOf("darwin") >= 0)) {
        detectedOS = OperatingSystem.MAC;
      }
      else
        if (operatingSystem.indexOf("win") >= 0) {
          detectedOS = OperatingSystem.WINDOWS;
        }
        else
          if (operatingSystem.indexOf("nux") >= 0) {
            detectedOS = arch.indexOf("64") >= 0 ? OperatingSystem.LINUX_64 : OperatingSystem.LINUX_32;
          }
          else {
            throw new IllegalStateException("Could not determine current operating system!");
          }
    }
    return detectedOS;
  }
}
