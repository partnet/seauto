# SeAuto

Introduction
------
SeAuto is the necessary “merge between” testing automation frameworks and 
Selenium. It is the last piece required to start developing automated 
tests&mdash;the glue between the test framework and Selenium. This solution 
allows good practices to be used from the start&mdash;such as using Page 
Object, Selenium's Page Factory, and an intermediate steps layer to create 
reusable pieces of code. These ideas have been developed, read, and used over 
the past several years.


#### What does this framework provide?

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

This parent project contains the projects for:
* SeAuto core
* JBehave support
* JUnit support
* Cucumber support
* Selenium driver management

The [change log](CHANGELOG.md) summarizes important differences between 
versions. 

Build
------

To build this project, clone the git repository and run:

`mvn clean install`

This will build and test the packages, and install them into the local 
repository.

Email Group
------

Join the SeAuto Google Group to get help and keep up with SeAuto 
news: [SeAuto Group](https://groups.google.com/forum/#!forum/seauto)


Documentation
------
Please see the [documentation](//partnet.github.io/seauto/)


How To Contribute
------
Contributions are welcome. Feel free to [file issues in 
Github,](//github.com/partnet/seauto/issues) or clone the repository, make 
your changes, and send us a pull request.

#### Contribution code formatting
Please follow the example of existing code. As long as spaces are used rather 
than tabs, most other sins can be forgiven. Any questions can be directed to 
the Google user group.




