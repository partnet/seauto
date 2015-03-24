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

package org.openqa.selenium.browserlaunchers;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.Proxy;

/**
 * workaround to allow selenium 2.44+ and also use default phanotmjs
 * implementation until the phantomjs people release a fix
 * https://github.com/detro/ghostdriver/issues/397 
 */
public class Proxies
{

  public static Proxy extractProxy(Capabilities capabilities)
  {
    return Proxy.extractFrom(capabilities);
  }
}
