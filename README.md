UI, API & DB Project 
Selenium-Cucumber-Junit framework
Built by Maven Implementing Behavior Driven Development.
Feature file is implementation of our User Stories writing Scenarios in Gherkin language implementing Data Driven Testing.
Also using parameterazation to provide data directly in our Scenarios.
In implementation of BDD we have four main packages.
pages: implementing Page Object Model Design Pattern. Also abstract BasePage where we use PageFactory class from Selenium to introduse the elements
of this class to the Driver object so we can use it by instantionating object of this class to the StepDefenition classes,
runners: CukesRunner class, we can run the code with different tags & FailedRunner class to run only failed test.
stepDefenition: Hooks clas with @Before & @After methods coming from the cucumber. Also implementation of our scenarios.
utility: Driver class implementing Singleton Design Pattern. Also implement parallel testing, while still having Singleton.
configuration.properties file
POM XML file
