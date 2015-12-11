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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Email template for creating a email message body that can be compared to a
 * real email. Uses Freemarker to create the message body.
 * 
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public abstract class EmailTemplate
{

  protected Map<String, Object> data = new HashMap<>();

  private String stringOfProcessedTemplate;

  private final String templateFileName;

  /**
   * Create an email template to be used later.
   * 
   * @param templateFileName
   *          name of the Freemarker template file to be used; must exist under
   *          the stories directory.
   * @see PathUtils#getStoriesPath()
   */
  public EmailTemplate(String templateFileName)
  {
    this.templateFileName = Objects.requireNonNull(templateFileName);
  }

  private final void processTemplate()
  {
    Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    Template template;
    String dir = PathUtils.getStoriesPath().toString();
    File fileToUse = PathUtils.getFileInPath(dir, templateFileName);

    // create template
    try {
      cfg.setDirectoryForTemplateLoading(fileToUse.getParentFile());
      template = cfg.getTemplate(fileToUse.getName());
    }
    catch (IOException e) {
      throw new IllegalArgumentException(String.format("Could not locate template file: '%s' in dir '%s'", templateFileName, dir), e);
    }

    Writer writer = new StringWriter();

    try {
      template.process(data, writer);
    }
    catch (TemplateException | IOException e) {
      throw new IllegalStateException(String.format("There was a issue generating the email template '%s'", templateFileName), e);
    }

    // save template
    stringOfProcessedTemplate = writer.toString();
  }

  /**
   * Obtain the string of the processed template
   * @return the processed template
   */
  public final String getProcessedTemplate()
  {
    if (stringOfProcessedTemplate == null) {
      processTemplate();
    }

    return stringOfProcessedTemplate;
  }

}
