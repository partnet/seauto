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

import java.text.SimpleDateFormat;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jbehave.core.annotations.weld.WeldConfiguration;
import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.StoryControls;
import org.jbehave.core.i18n.LocalizedKeywords;
import org.jbehave.core.io.LoadFromClasspath;
import org.jbehave.core.model.ExamplesTableFactory;
import org.jbehave.core.parsers.RegexPrefixCapturingPatternParser;
import org.jbehave.core.parsers.RegexStoryParser;
import org.jbehave.core.reporters.ConsoleOutput;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.steps.MarkUnmatchedStepsAsPending;
import org.jbehave.core.steps.ParameterConverters;
import org.jbehave.core.steps.ParameterConverters.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.partnet.automation.RuntimeConfiguration;

/**
 * Provides an instance of {@link Configuration}.
 * 
 * @author <a href="mailto:rbascom@part.net">rbascom</a>
 */
public final class DefaultConfigurationProducer
{

  static final Logger LOG = LoggerFactory.getLogger(DefaultConfigurationProducer.class);

  @Inject
  private RuntimeConfiguration runConfig;

  @Produces
  @Singleton
  @ConfigurationAlternative
  @WeldConfiguration
  public Configuration getConfiguration()
  {
    LOG.info("ConfigurationProducer.getConfiguration()");

    Keywords keywords = new LocalizedKeywords();

    ParameterConverters converters = new ParameterConverters().addConverters(new DateConverter(new SimpleDateFormat("yyyy-MM-dd")), new StoryParameterEnumConverter());

    return new MostUsefulConfiguration().useStoryControls(new StoryControls().doDryRun(runConfig.doDryRun()).doSkipScenariosAfterFailure(false))

    .useStepPatternParser(new RegexPrefixCapturingPatternParser()).useStoryLoader(new LoadFromClasspath(this.getClass().getClassLoader()))

    .useKeywords(keywords).useStepCollector(new MarkUnmatchedStepsAsPending(keywords)).useStoryParser(new RegexStoryParser(keywords, new ExamplesTableFactory(keywords, new LoadFromClasspath(this.getClass()), converters))).useDefaultStoryReporter(new ConsoleOutput(keywords))

    .useStoryReporterBuilder(new StoryReporterBuilder().withFormats(Format.CONSOLE, Format.TXT, Format.STATS, WebDriverHtmlOutputWithImg.WEB_DRIVER_HTML_WITH_IMG).withFailureTrace(true).withReporters(new LoggingStoryReporter()).withKeywords(keywords)).useParameterConverters(converters);
  }
}
