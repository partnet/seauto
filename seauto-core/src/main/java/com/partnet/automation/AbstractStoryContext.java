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

import java.util.ArrayDeque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Abstract base class for StoryContext implementations. Supports variable
 * storage within the context and {@link AfterScenarioHandler} support
 * 
 * @author fpedroza
 * @since Nov 24, 2014
 */
abstract public class AbstractStoryContext
{

  private final Map<Object, Object> variables = new ConcurrentHashMap<>();

  private final ArrayDeque<AfterScenarioHandler> afterScenarioHandlers = new ArrayDeque<>();

  private static final Object NULL_VALUE_PLACEHOLDER = new Object();

  /**
   * Adds or updates the given key/value as a context variable. Existing
   * key/value are over-written. Ensures if an existing key/value is found that
   * the type of the updated value (class name) is the same as the previous
   * value.
   *
   * @param key Object to use as the key
   * @param value Object as the value, to be retrieved later in the test.
   * 
   * @throws IllegalStateException
   *           if the type of new value conflicts with the existing type
   */
  public void addOrUpdateVariable(Object key, Object value)
  {
    if (value != null) {
      // if a previous value was stored, verify stored type hasn't changed
      Object prevValue = translateNull(variables.get(key));
      if (prevValue != null && !prevValue.getClass().getName().equals(value.getClass().getName())) {
        throw new IllegalStateException(String.format("Cannot change variable type for key (%s) from %s to %s", key, prevValue.getClass().getName(), value.getClass().getName()));
      }
      variables.put(key, value);
    }
    else { // ConcurrentHashMap doesn't allow null values so trick it by using a
           // placeholder
      variables.put(key, NULL_VALUE_PLACEHOLDER);
    }
  }

  /**
   * Remove the variable value (if any) for the given key.
   */
  public void removeVariable(Object key)
  {
    variables.remove(key);
  }

  /**
   * Clear the state of the story context.
   */
  public void clear()
  {
    variables.clear();
  }

  /**
   * Get the variable value for the given key. Only returns null iff a null was
   * stored.
   * 
   * @param key the key of the value to lookup
   * @return the object requested
   * @throws IllegalStateException if a value for the key has not been previously added
   */
  public Object getVariable(Object key)
  {
    Object value = variables.get(key);
    if (value == null) {
      throw new IllegalStateException("missing expected state variable: " + key);
    }
    return translateNull(value);
  }

  /**
   * Get the variable value for the given key; may return null.
   * 
   * @param key the key of the value to lookup
   * @see #getVariable(Object) is preferred unless necessary
   * @return the variable requested.
   */
  public Object getVariableAllowNull(Object key)
  {
    return translateNull(variables.get(key));
  }

  private Object translateNull(Object value)
  {
    // check for placeholder value and translate to null if necessary
    return (value == NULL_VALUE_PLACEHOLDER ? null : value);
  }

  /**
   * Add an action to take when the scenario completes
   */
  public void addAfterScenarioHandler(AfterScenarioHandler afterScenarioHandler)
  {
    afterScenarioHandlers.push(afterScenarioHandler);
  }

  /**
   * Execute all the registered scenario handlers in LIFO order, removing them
   * from the execution stack.
   */
  public void executeAfterScenarioHandlers()
  {
    while (!afterScenarioHandlers.isEmpty()) {
      afterScenarioHandlers.pop().execute(this);
    }
  }

  public interface AfterScenarioHandler
  {

    public abstract void execute(AbstractStoryContext context);
  }

}
