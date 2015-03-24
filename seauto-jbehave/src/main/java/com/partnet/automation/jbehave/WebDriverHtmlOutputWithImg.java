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

package com.partnet.automation.jbehave;

/**
 * HtmlOutput that allows WebDriver to take screenshots, and put them into the reports along with placing the 
 * failure UUID into the reports for error tracking
 * @author bbarker
 */
import java.io.PrintStream;
import java.util.Properties;

import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.reporters.FilePrintStreamFactory;
import org.jbehave.core.reporters.HtmlOutput;
import org.jbehave.core.reporters.StoryReporter;
import org.jbehave.core.reporters.StoryReporterBuilder;

public class WebDriverHtmlOutputWithImg
    extends HtmlOutput
{

  public static final org.jbehave.core.reporters.Format WEB_DRIVER_HTML_WITH_IMG = new WebDriverHtmlFormatWithImg();

  public WebDriverHtmlOutputWithImg(PrintStream output)
  {
    super(output);
    changeALine();
  }

  public WebDriverHtmlOutputWithImg(PrintStream output, Properties outputPatterns)
  {
    super(output, outputPatterns);
    changeALine();
  }

  public WebDriverHtmlOutputWithImg(PrintStream output, Keywords keywords)
  {
    super(output, keywords);
    changeALine();
  }

  public WebDriverHtmlOutputWithImg(PrintStream output, Properties outputPatterns, Keywords keywords)
  {
    super(output, outputPatterns, keywords);
    changeALine();
  }

  public WebDriverHtmlOutputWithImg(PrintStream output, Properties outputPatterns, Keywords keywords, boolean reportFailureTrace)
  {
    super(output, outputPatterns, keywords, reportFailureTrace);
    changeALine();
  }

  private void changeALine()
  {
    // If the img path changes, be sure to change it in the
    // StoryLifecycleListener class as well!
    super.overwritePattern("failed", "<div class=\"step failed\">{0} " + "<span class=\"keyword failed\">({1})</span><br/>" + "<span class=\"message failed\"><pre class=\"falure\">{2}</pre></span><br/>" + "<span class=\"message failed\">Failure UUID: {3}</span><br/>"
        + "<a color=\"black\" target=\"jb_scn_shot\" href=\"../screenshots/failed-scenario-{3}.png\">" + "<img src=\"../screenshots/failed-scenario-{3}.png\" alt=\"Screenshot of failed step\" width='20%' />" + "</a>" + "</div>");
  }

  private static class WebDriverHtmlFormatWithImg
      extends org.jbehave.core.reporters.Format
  {

    public WebDriverHtmlFormatWithImg()
    {
      super("HTML");
    }

    @Override
    public StoryReporter createStoryReporter(FilePrintStreamFactory factory, StoryReporterBuilder storyReporterBuilder)
    {
      factory.useConfiguration(storyReporterBuilder.fileConfiguration("html"));
      return new WebDriverHtmlOutputWithImg(factory.createPrintStream(), storyReporterBuilder.keywords()).doReportFailureTrace(storyReporterBuilder.reportFailureTrace()).doCompressFailureTrace(storyReporterBuilder.compressFailureTrace());
    }
  }

}
