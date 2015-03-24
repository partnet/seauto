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
 * Tests JBehave's framework.
 * 
 * Note: There is the possibility where {@link DefaultWeldAnnotatedStoryRunner}
 * gets run as well. If running this as a JUnit test, be sure to only invoke
 * this {@link TestJBehave} class. By default, Maven will only execute this
 * class when performing tests
 * 
 * 
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class TestJBehave
    extends DefaultWeldAnnotatedStoryRunner
{

}
