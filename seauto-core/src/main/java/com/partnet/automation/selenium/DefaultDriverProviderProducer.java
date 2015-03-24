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

package com.partnet.automation.selenium;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for providing a {@link WebDriverProvider}.
 *
 * @author <a href="mailto:rbascom@part.net">Ryan Bascom</a>
 */
@Alternative
public final class DefaultDriverProviderProducer
{

  private final static Logger LOG = LoggerFactory.getLogger(DefaultDriverProviderProducer.class);

  @Produces
  @Singleton
  public DriverProvider getDriverProvider()
  {
    LOG.info("DefaultDriverProviderProducer getDriverProvider");
    return new DefaultDriverProvider();
  }

}
