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

import java.util.List;
import java.util.Map;

import org.jbehave.core.failures.UUIDExceptionWrapper;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.model.GivenStories;
import org.jbehave.core.model.Lifecycle;
import org.jbehave.core.model.Meta;
import org.jbehave.core.model.Narrative;
import org.jbehave.core.model.OutcomesTable;
import org.jbehave.core.model.OutcomesTable.Outcome;
import org.jbehave.core.model.Scenario;
import org.jbehave.core.model.Story;
import org.jbehave.core.model.StoryDuration;
import org.jbehave.core.reporters.StoryReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * StoryReporter that logs various info for various story events.
 * 
 * @author fpedroza
 * @since May 14, 2014
 */
public class LoggingStoryReporter
    implements StoryReporter
{

  private static final Logger LOG = LoggerFactory.getLogger(LoggingStoryReporter.class);

  // TODO: Jan 13, 2015 (bbarker) - Need to wait until
  // https://jira.codehaus.org/browse/JBEHAVE-1053 is merged
  // private static final Logger LOG_RESTARTED_STORY =
  // LoggerFactory.getLogger(LoggingStoryReporter.class.getName() + ".restart");

  public LoggingStoryReporter()
  {}

  @Override
  public void storyNotAllowed(Story story, String filter)
  {
    // TODO: May 15, 2014 (fpedroza) - evaluate usefulness
    LOG.info("LoggingStoryReporter.storyNotAllowed() - {} {}", story, filter);
  }

  @Override
  public void storyCancelled(Story story, StoryDuration storyDuration)
  {
    // TODO: May 15, 2014 (fpedroza) - evaluate usefulness
    LOG.info("LoggingStoryReporter.storyCancelled() - {} {}", story, storyDuration);
  }

  @Override
  public void beforeStory(Story story, boolean givenStory)
  {
    // name includes full path so try to extract just the last component (i.e.
    // the story filename)
    // and put that into the MDC
    String name = story.getName();
    String shortName = name;

    int idx = name.lastIndexOf('/') + 1;
    if (idx > 0) {
      shortName = name.substring(idx);
    }

    MDC.put("story", shortName);
    LOG.info("LoggingStoryReporter.beforeStory() - (path: {})", story.getPath());
  }

  private String toString(Meta meta)
  {
    StringBuilder buf = new StringBuilder();
    if (!meta.isEmpty()) {
      for (String name : meta.getPropertyNames()) {
        buf.append(String.format("  @%s: %s \n", name, meta.getProperty(name)));
      }
    }
    return buf.toString();
  }

  @Override
  public void afterStory(boolean givenStory)
  {
    MDC.clear();
  }

  @Override
  public void narrative(Narrative narrative)
  {
    // don't care about this
  }

  @Override
  public void lifecyle(Lifecycle lifecycle)
  {
    if (!lifecycle.isEmpty()) {
      StringBuilder msg = new StringBuilder();

      List<String> beforeSteps = lifecycle.getBeforeSteps();
      if (!beforeSteps.isEmpty()) {
        msg.append("\nbeforeSteps:").append(beforeSteps);
      }

      List<String> afterSteps = lifecycle.getAfterSteps();
      if (!afterSteps.isEmpty()) {
        msg.append("\nafterSteps:").append(afterSteps);
      }

      LOG.info("LoggingStoryReporter.lifecyle() - {}", msg);
    }
  }

  @Override
  public void scenarioNotAllowed(Scenario scenario, String filter)
  {
    // don't care about this
  }

  @Override
  public void beforeScenario(String scenarioTitle)
  {
    LOG.debug("Scenario: {}", scenarioTitle);
  }

  @Override
  public void scenarioMeta(Meta meta)
  {
    if (!meta.isEmpty()) {
      LOG.debug("Scenario Meta: \n{}", toString(meta));
    }
  }

  @Override
  public void afterScenario()
  {
    // don't care about this
  }

  @Override
  public void givenStories(GivenStories givenStories)
  {
    // TODO: May 15, 2014 (fpedroza) - evaluate usefulness
    LOG.info("LoggingStoryReporter.givenStories() - {}", givenStories);
  }

  @Override
  public void givenStories(List<String> storyPaths)
  {
    // TODO: May 15, 2014 (fpedroza) - evaluate usefulness
    LOG.info("LoggingStoryReporter.givenStories() - {}", storyPaths);
  }

  @Override
  public void beforeExamples(List<String> steps, ExamplesTable table)
  {
    // don't care about this
  }

  @Override
  public void example(Map<String, String> tableRow)
  {
    LOG.debug("example - {}", tableRow);
  }

  @Override
  public void afterExamples()
  {
    // don't care about this
  }

  @Override
  public void beforeStep(String step)
  {
    LOG.debug("before Step: {}", step);
  }

  @Override
  public void successful(String step)
  {
    // don't care about this
  }

  @Override
  public void ignorable(String step)
  {
    LOG.debug("Step: {} (IGNORED)", step);
  }

  @Override
  public void pending(String step)
  {
    LOG.debug("Step: {} (PENDING)", step);
  }

  @Override
  public void notPerformed(String step)
  {
    LOG.debug("Step: {} (NOT PERFORMED)", step);
  }

  @Override
  public void failed(String step, Throwable cause)
  {
    StringBuilder msg = new StringBuilder();
    msg.append(String.format("Step: %s (FAILED)", step));

    if (cause instanceof UUIDExceptionWrapper) {
      msg.append(" - Failure UUID: ").append(((UUIDExceptionWrapper) cause).getUUID());
    }

    LOG.error(msg.toString(), cause);
  }

  @Override
  public void failedOutcomes(String step, OutcomesTable table)
  {
    failed(step, table.failureCause());

    StringBuilder message = new StringBuilder();

    message.append("(Failed Outcomes) ").append(step);
    for (Outcome<?> out : table.getFailedOutcomes()) {
      message.append(out.getDescription());
    }
    LOG.error(message.toString());
  }

  @Override
  public void restarted(String step, Throwable cause)
  {
    // TODO: May 15, 2014 (fpedroza) - evaluate usefulness
    LOG.info("LoggingStoryReporter.restarted() - {}", step, cause);

  }

  // TODO: Jan 13, 2015 (bbarker) - Need to wait until
  // https://jira.codehaus.org/browse/JBEHAVE-1053 is merged

  /*
   * @Override public void restartedStory(Story story, Throwable cause) {
   * StringBuilder restartMsg = new StringBuilder();
   * 
   * restartMsg.append("Restarted story: ").append(story.getName()).append("\n")
   * .append(story.getDescription().asString());
   * 
   * LOG_RESTARTED_STORY.warn(restartMsg.toString(), cause);
   * 
   * }
   */

  @Override
  public void dryRun()
  {
    LOG.debug("DRY RUN");
  }

  @Override
  public void pendingMethods(List<String> methods)
  {
    // don't care about this
  }

}
