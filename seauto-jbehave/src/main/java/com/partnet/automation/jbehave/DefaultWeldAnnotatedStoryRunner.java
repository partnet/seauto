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
import java.util.Objects;

import org.jbehave.core.InjectableEmbedder;
import org.jbehave.core.annotations.Configure;
import org.jbehave.core.annotations.UsingEmbedder;
import org.jbehave.core.annotations.weld.UsingWeld;
import org.jbehave.core.embedder.Embedder;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.weld.WeldAnnotatedEmbedderRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.partnet.automation.RuntimeConfiguration;

/**
 * Entry point for the automation tests.
 * 
 * @author <a href="mailto:rbascom@part.net">rbascom</a>
 */
@RunWith(WeldAnnotatedEmbedderRunner.class)
@Configure()
@UsingEmbedder(embedder = Embedder.class, verboseFailures = true, generateViewAfterStories = true, ignoreFailureInStories = true, ignoreFailureInView = false, metaFilters = {
    "-skip", "-manual"
}, storyTimeoutInSecs = Long.MAX_VALUE)
@UsingWeld
public class DefaultWeldAnnotatedStoryRunner
    extends InjectableEmbedder
{

  private final static Logger LOG = LoggerFactory.getLogger(DefaultWeldAnnotatedStoryRunner.class);

  // TODO: Nov 18, 2014 (fpedroza) - try to get rid of this
  private final RuntimeConfiguration runConfig = RuntimeConfiguration.getInstance();

  @Override
  @Test
  public void run()
  {
    final String storiesDirectory = runConfig.getStoriesDirectory();
    final String storyPathInclusion = runConfig.getStoryPathInclusion();

    this.injectedEmbedder().runStoriesAsPaths(this.getStoryPaths(storiesDirectory, storyPathInclusion));
  }

  /**
   * Allows access to the {@link EmbedderControls}
   */
  @Override
  public Embedder injectedEmbedder()
  {
    Embedder embedder = super.injectedEmbedder();

    // set thread count. The maven plugin for setting threads does not appear to
    // be working.
    // embedder.embedderControls().useThreads(runConfig.getThreads());
    embedder.embedderControls().useThreads(1);

    return embedder;
  }

  /**
   * Recursively searches for files in the given <tt>storiesDirectory</tt>,
   * using the given <tt>storyPathInclusion</tt>.
   * 
   * @param storiesDirectory
   *          the directory to recursively search. Cannot be <tt>null</tt>.
   * @param storyPathInclusion
   *          the inclusion regex expression. Cannot be <tt>null</tt>.
   * 
   * @return the story paths
   */
  protected List<String> getStoryPaths(final String storiesDirectory, final String storyPathInclusion)
  {
    Objects.requireNonNull(storiesDirectory, "storiesDirectory cannot be null.");
    Objects.requireNonNull(storyPathInclusion, "storyPathInclusion cannot be null.");
    System.out.println("Looking for stories in: " + storiesDirectory);
    LOG.debug("Looking for stories in: {}", storiesDirectory);
    LOG.debug("Using story path inclusion: {}", storyPathInclusion);

    final List<String> storyPaths = new StoryFinder().findPaths(storiesDirectory, storyPathInclusion, "**/partial_*,**/givenstory_*");

    LOG.info("Found {} stories: {}", storyPaths.size(), storyPaths);

    return storyPaths;
  }

}
