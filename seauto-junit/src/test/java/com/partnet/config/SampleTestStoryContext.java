package com.partnet.config;

import javax.inject.Inject;

import com.partnet.automation.AbstractStoryContext;
import com.partnet.automation.page.Page;
import com.partnet.automation.page.PageProvider;
import com.partnet.page.DummySite;
import com.partnet.page.DummySiteProvider;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
public class SampleTestStoryContext
  extends AbstractStoryContext
{
  
  PageProvider pageProvider;
  DummySiteProvider siteProvider;
  
  
  @Inject
  SampleTestStoryContext(final PageProvider pageProvider, final DummySiteProvider siteProvider)
  {
    this.pageProvider = pageProvider;
    this.siteProvider = siteProvider;
  }
  
  public DummySite site()
  {
    return siteProvider.createSite();
  }
  
  /**
   * Get a reference to a specified {@link Page} instance.
   * Shortcut/alternative to using the {@link #pages()} method just to invoke {@link PageProvider#get(Class)}.
   * 
   * @param clazz  type of {@link Page} 
   * @return  a {@link Page} instance of the requested type
   */
  public <T extends Page> T getPage(Class<T> clazz)
  {
    return this.pageProvider.get(clazz);
  }


}
