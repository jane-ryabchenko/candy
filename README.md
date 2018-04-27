# Candy

**Visual Regression Testing Framework**

## Rationale

The purpose of this framework is allow developers to detect unwanted/unintentional changes in web application UI and inform developer of such changes during test phase of build process.

It allows to minimize time required to find, report, fix and verify visual regression because it excludes interaction with QA engineer from this process.

  

## How it works

Candy Framework (just “Candy” below) requires Java, JUnit and Selenium WebDriver to be used on the project.

Engineers can write new Candy tests from scratch, using JUnit, Selenium WebDriver and Candy, or modify existing Selenium tests to add visual regression verification by making low code changes to invoke Candy where needed.

Candy uses straight forward idea of screenshot comparison. Engineer configures test to take screenshot of a page or page region and compares actual screenshot with expected screenshot.

Candy calculates difference for every pair of actual/expected screenshots, saves each actual screenshot into the folder specified in configuration and creates test report for every test with paths to screenshots, diff percentage, etc.

Candy implies approach opposite to fail fast. It collects information about every failed/succeed comparison within test method and let's test method to keep taking remaining screenshots.

At the end of test invocation, if there were failed comparisons during invocation, Candy will throw MultipleFailureException with failed comparison info and appropriate stack trace to allow engineer to find exact point in test’s code where screenshot comparison has failed.

  

## How to start

In order to use Candy for visual regression verification for existing Selenium test, engineer needs to perform 5 simple steps:

1.  Add Candy Framework Maven dependency to the pom.xml 
```
    <dependency>
	    <groupId>org.candy</groupId>
	    <artifactId>candy-framework</artifactId>
	    <version>0.97</version>
    </dependency>  
```

2.  Add ScreenshotCaptor member variable to the test class to use it within test methods to capture and compare screenshots and pass WebDriver instance to its constructor.  
```
    private ScreenshotCaptor captor = new ScreenshotCaptor(webDriver);  
```    
    
3.  Add CandyTestWatcher member variable annotated with @Rule and pass previously created instance of ScreenshotCaptor to its constructor.
```
@Rule
public CandyTestWatcher watcher = new CandyTestWatcher(captor);  
```  

4.  Configure path to folder with origin (expected) screenshots and path to folder for actual screenshots using static methods of GlobalContext class (this should be done before the first invocation of capture(...) methods of a ScreenshotCaptor class either in test constructor, or method annotated with @Before.
```
GlobalContext.setOriginFolder(<path_to_codebase/origin_folder>);
GlobalContext.setActualFolder(<path_to_build/actual_folder>);
```

5.  Call captor.screenshot(<screenshot_name>) method to capture screenshot in required moment and compare it with corresponding origin screenshot.
    

## Implementation

#### Why do we need @Rule CandyTestWatcher ?

CandyTestWatcher class extends JUnit TestWatcher class and overrides Statement apply(Statement base, Description description) method.

Method apply(...) for each JUnit test method and controls test Statement evaluation.

CandyTestWatcher uses this method to obtain the name of the test method that is going to be executed and provides this name to the ScreenshotCaptor (purpose of this action will be explained later).

Also it uses this method to get list of screenshot comparisons from the ScreenshotCaptor and throw MultipleFailureException with all failed comparisons (if there are any failed comparisons) after test Statement evaluation.

#### Where to store origin and actual screenshots?

Usually origin screenshots folder is stored as a test resource folder in the codebase within Version Control System (just “VCS” below) used on the project, while folder for actual screenshots placed in temporary build/target folder.  
  
#### How Candy compares screenshots?

By default Candy uses straight image comparator which compares images pixel by pixel within maximum width and height of both images. Result of such comparison is difference ratio calculated by formula
```
diff = numberOfMistmatchedPixels / (max(widht1, width2) * max(height1, height2)
```
 Also it can be configured to use color comparator which compares pixels respectively to their LAB color distance. Result of such comparison is difference ratio calculated by formula
```
diff = sqrt(sum(lab_delta ^ 2)) / sqrt(sum(255 ^ 2)
```

#### What to do with test results?

In case of intentional change of the page or page fragment, engineer needs to update corresponding origin screenshot to reflect updates.

This can be done by replacing origin screenshot with actual screenshot and consequent commit of the change together with updated origin screenshot with selected VCS.

Otherwise, to understand what exactly has changed engineer needs to either use a third party image comparison tool, or Candy Server.

#### What is Candy Server for?

Candy Server is a web server application that provides REST-endpoints to upload test results and store them as a test report with list of comparisons.

Every test report has it’s unique ID (basically it’s UUID) and can be used as a part of URL to see report details on the web page. Such URL also can be used to share report with other team members.

Comparison report page provides tools to display screenshot diffs in various modes, such as side by side, overlay, blink, etc. Optionally user can turn on diff color selection, to highlight all mismatched pixels.

Candy Server is a Spring Web application which uses Spring MVC REST-controllers to receive comparison report data from team members environments and to render the comparison report page.

It also uses Spring JPA and Hibernate to work with database to store and obtain comparison report data.

Candy Server instance can be shared within one project, or across the whole department or company.

#### How the results are uploaded to the server?

Candy Maven Plugin can be used to upload test results to the Candy Server.

Plugin execution should be configured for post-integration-test Maven phase, to allow upload generated screenshots and comparison reports XML files.

After execution of integration test plugin sends POST request with JSON payload containing total number of comparisons within report.

It receives response from Candy Server with unique ID string in its body.

Then plugin uploads each comparison data iteratively using POST query with multipart/mixed payload. Payload consists of three parts:

1.  JSON with report ID received after initial request and comparison data such as origin screenshot name, actual screenshot name, diff percentage, etc.;
    
2.  Actual screenshot PNG image;
    
3.  Original screenshot PNG image.
    
Candy Server keeps tracking number of uploaded comparison records and compares it with total/expected number of comparisons received in initial request. After upload of last comparison Server marks report as complete.
  
Find example of the plugin configuration below.
```
<build>
<plugins>
...
<plugin>
<groupId>org.candy</groupId>
<artifactId>candy-maven-plugin</artifactId>
<version>0.97</version>
<dependencies>
<dependency>
<groupId>org.candy</groupId>
<artifactId>candy-server-shared</artifactId>
<version>0.97</version>
</dependency>
</dependencies>
<configuration>
<testReportFolder>target/candy-actual</testReportFolder>
<uploadURL>CANDY_SERVER_URL</uploadURL>
</configuration>
<executions>
<execution>
<phase>post-integration-test</phase>
<goals>
<goal>cndy-maven-plugin</goal>
</goals>
</execution>
</executions>
</plugin>
...
<plugins>
<build>
```
