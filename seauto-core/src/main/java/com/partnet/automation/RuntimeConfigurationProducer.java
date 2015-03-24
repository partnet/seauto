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

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for providing a {@link RuntimeConfiguration}.
 * 
 * @author fpedroza
 */
public class RuntimeConfigurationProducer
{

  private static final Logger LOG = LoggerFactory.getLogger(RuntimeConfigurationProducer.class);

  private final RuntimeConfiguration config;

  public RuntimeConfigurationProducer()
  {
    LOG.info("RuntimeConfigurationProducer constructor");
    config = RuntimeConfiguration.getInstance();
  }

  @Produces
  @Singleton
  public RuntimeConfiguration createConfiguration()
  {
    LOG.info("RuntimeConfigurationProducer createConfiguration");
    return config;
  }
}
