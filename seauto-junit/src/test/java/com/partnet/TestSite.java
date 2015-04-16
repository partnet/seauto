package com.partnet;

import javax.inject.Inject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.partnet.automation.selenium.DriverProvider;
import com.partnet.config.SampleTestStoryContext;
import com.partnet.junit.SeAuto;
import com.partnet.junit.annotations.browser.HTMLUnit;

/**
 * @author <a href="mailto:bbarker@part.net">bbarker</a>
 */
@RunWith(SeAuto.class)
public class TestSite
{

  @Inject
  SampleTestStoryContext context;
  
  @Rule
  public ExpectedException expectedEx = ExpectedException.none();
  
  private static final String URL_SYS_PROP_NAME = "test.config.url";
  private static final String TEST_URL = "http://url-to-no-where-234242/";
  
  @After
  public void tearDown()
  {
    System.clearProperty(URL_SYS_PROP_NAME);
  }
  
  @Test
  @HTMLUnit
  public void test_ensureSiteOpenPullsFromTestConfigUrlProperty()
  {
    System.setProperty(URL_SYS_PROP_NAME, TEST_URL);
    context.site().open();
    Assert.assertEquals("test.config.url property isn't working correctly", 
        TEST_URL, context.site().getCurrentPageUrl());
  }
  
  @Test
  @HTMLUnit
  public void test_ensureTestConfigUrlIsRequired()
  {
    expectedEx.expect(IllegalStateException.class);
    expectedEx.expectMessage("Missing required property test.config.url");
    context.site().open();
  }

}
