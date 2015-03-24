# SeAuto

Introduction
------
SeAuto is the necessary “merge between” testing automation frameworks and selenium. It is the last piece required to start developing automated tests - the glue between the test framework and Selenium. This solution allows good practices to be used from the start - such as using Page Object, Selenium's Page Factory, and an intermediate steps layer to create reusable pieces of code. These ideas have been developed, read, and used over the past several years.


#### So what does this framework provide?

* Run against any of the major browsers: Firefox, Internet Explorer, Chrome, PhanomJs, and HTMLUnit
* Run on Linux, Windows, and possibly Mac
* Run tests on a pre-existing Selenium Grid by simply defining a URL
* Change a single property to multi-thread tests with assurance tests will be thread safe
* Clean, clear distinction between the roles of Page Object and intermediate Step Objects.
* Quickly and easily add Page and Step Objects
* Useful helper methods for WebDriver that work across all of the browsers, such as accepting a javascript alert (even for the headless browsers!)
* Easily integrate it as part of your existing CI server
* Support for a unique browser per test to utilize resources more efficiently
* Drop-in, ready to start creating tests

Information
------

This parent project contains the proejcts for:
* SeAuto-Core
* SeAuto-JBehave
* SeAuto-JUnit

The [change log](CHANGELOG.md) summarizes important differences between versions. 


Build
------

To build this project, clone the repo locally, and run:

`mvn clean install`

This will build and test the project, and install them into the local repository

Email Group
------
**Note, this is NOT a internal group. Others outside of partnet can see this**

[SeAuto Group](https://groups.google.com/forum/#!forum/seauto)


Documentation
------
Please see the [documentation](http://mercury.part.net/WebContent/#/getStarted)


How To Contribute
------
Contributions are more then welcome. Feel free to create new issues in Github, clone the repository, and send a pull request.

#### Code formatting
Use two spaces instead of a tab. Examples of method blocks, switch statements, etc.. can be found in the code base. If there is every a question, feel free to email the user group.




