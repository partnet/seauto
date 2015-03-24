Feature: Verify the correct driver is launched when starting

  @htmlunit
  Scenario: Verify HTMLUnit driver is launched with scenario annotation
    Then the HTMLUnit driver should be running