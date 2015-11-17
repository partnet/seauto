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

package com.partnet.config;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.partnet.automation.annotation.StoryScoped;

/**
 * Manages the lifecycle of the {@link SampleTestStoryContext} by setting and removing the context from the current thread.
 * 
 * @author <a href="mailto:rbascom@part.net">rbascom</a>
 */
public final class SampleTestStoryContextProvider
{

  private static final Logger LOG = LoggerFactory.getLogger(SampleTestStoryContextProvider.class);

  private static final ThreadLocal<SampleTestStoryContext> THREAD_LOCAL_CONTEXT = new ThreadLocal<SampleTestStoryContext>();

  private final SampleTestStoryContext context;

  @Inject
  public SampleTestStoryContextProvider(final SampleTestStoryContext context)
  {
    this.context = context;
  }

  public void initialize()
  {
    SampleTestStoryContextProvider.LOG.debug("Initializing scenario context on thread {}", Thread.currentThread());
    SampleTestStoryContextProvider.THREAD_LOCAL_CONTEXT.set(this.context);
  }

  public void end()
  {
    SampleTestStoryContextProvider.LOG.debug("Ending scenario context on thread {}", Thread.currentThread());
    SampleTestStoryContextProvider.THREAD_LOCAL_CONTEXT.remove();
  }

  @Produces
  @StoryScoped
  public SampleTestStoryContext getContext()
  {
    return SampleTestStoryContextProvider.THREAD_LOCAL_CONTEXT.get();
  }
}
