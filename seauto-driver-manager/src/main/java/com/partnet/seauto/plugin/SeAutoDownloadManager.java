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

package com.partnet.seauto.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.partnet.automation.Browser;
import com.partnet.automation.download.StandaloneDriverDownloadAssistant;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
@Mojo(name = "download")
public class SeAutoDownloadManager
  extends AbstractMojo
{

  /**
   * List of browsers to obtain drivers for
   */
  @Parameter
  private String[] browserList;

  @Override
  public void execute()
    throws MojoExecutionException, MojoFailureException
  {
    List<Browser> browserDownloadList = new ArrayList<>();

    if (browserList == null) {
      browserDownloadList.add(Browser.CHROME);
      browserDownloadList.add(Browser.PHANTOMJS);
      browserDownloadList.add(Browser.IE);
    }

    else {
      for (String browser : browserList) {
        browserDownloadList.add(Browser.valueOf(browser.toUpperCase()));
      }
    }

    StandaloneDriverDownloadAssistant driverDownloadAssist = new StandaloneDriverDownloadAssistant();
    
    for(Browser browser : browserDownloadList) {
      try {
        driverDownloadAssist.downloadDriverFor(browser);
      }
      catch (IOException e) {
        throw new IllegalArgumentException("There was a issue downloading resources!", e);
      }
    }
  }


}
