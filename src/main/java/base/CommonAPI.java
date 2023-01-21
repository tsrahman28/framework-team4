package base;

import com.relevantcodes.extentreports.LogStatus;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import reporting.ExtentManager;
import reporting.ExtentTestManager;
import utility.Utility;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;

public class CommonAPI {
    Logger LOG = LogManager.getLogger(CommonAPI.class.getName());

    String takeScreenshot = Utility.getProperties().getProperty("take.screenshot", "false");
    String maximizeBrowser = Utility.getProperties().getProperty("browser.maximize", "true");
    String implicitWait = Utility.getProperties().getProperty("implicit.wait", "10");
    String username = Utility.decode(Utility.getProperties().getProperty("browserstack.username"));
    String password = Utility.decode(Utility.getProperties().getProperty("browserstack.password"));
    public WebDriver driver;

    //report setup from line 37 to 94
    public static com.relevantcodes.extentreports.ExtentReports extent;

    @BeforeSuite
    public void extentSetup(ITestContext context) {
        ExtentManager.setOutputDirectory(context);
        extent = ExtentManager.getInstance();
    }

    @BeforeMethod
    public void startExtent(Method method) {
        String className = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName().toLowerCase();
        ExtentTestManager.startTest(method.getName());
        ExtentTestManager.getTest().assignCategory(className);
    }
    protected String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    @AfterMethod
    public void afterEachTestMethod(ITestResult result) {
        ExtentTestManager.getTest().getTest().setStartedTime(getTime(result.getStartMillis()));
        ExtentTestManager.getTest().getTest().setEndedTime(getTime(result.getEndMillis()));

        for (String group : result.getMethod().getGroups()) {
            ExtentTestManager.getTest().assignCategory(group);
        }

        if (result.getStatus() == 1) {
            ExtentTestManager.getTest().log(LogStatus.PASS, "Test Passed");
        } else if (result.getStatus() == 2) {
            ExtentTestManager.getTest().log(LogStatus.FAIL, getStackTrace(result.getThrowable()));
        } else if (result.getStatus() == 3) {
            ExtentTestManager.getTest().log(LogStatus.SKIP, "Test Skipped");
        }
        ExtentTestManager.endTest();
        extent.flush();
        if (takeScreenshot.equalsIgnoreCase("true")){
            if (result.getStatus() == ITestResult.FAILURE) {
                takeScreenshot(result.getName());
            }
        }
        driver.quit();
    }
    @AfterSuite
    public void generateReport() {
        extent.close();
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }
    public void getLocalDriver(String browserName){
        if (browserName.equalsIgnoreCase("chrome")){
            driver = new ChromeDriver();
        }else if (browserName.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
        } else if (browserName.equalsIgnoreCase("edge")) {
            driver = new EdgeDriver();
        }
    }
    public void getCloudDriver(String envName, String os, String osVersion, String browser, String browserVersion, String username, String password) throws MalformedURLException {
        DesiredCapabilities cap = new DesiredCapabilities();
        cap.setCapability("os", os);
        cap.setCapability("os_version", osVersion);
        cap.setCapability("browser", browser);
        cap.setCapability("browser_version", browserVersion);
        if (envName.equalsIgnoreCase("browserstack")){
            cap.setCapability("resolution", "1024x768");
            driver = new RemoteWebDriver(new URL("http://"+username+":"+password+"@hub-cloud.browserstack.com:80/wd/hub"),cap);
        } else if (envName.equalsIgnoreCase("saucelabs")) {
            driver = new RemoteWebDriver(new URL("http://"+username+":"+password+"@ondemand.saucelabs.com:80/wd.hub"),cap);
        }

    }

    @Parameters({"useCloudEnv","envName","os","osVersion","browserName","browserVersion","url"})
    @BeforeMethod
    public void setUp(@Optional("false") boolean useCloudEnv, @Optional("browserstack") String envName,
                      @Optional("windows") String os, @Optional("11") String osVersion,
                      @Optional("chrome") String browserName, @Optional("108") String browserVersion,
                      @Optional("https://www.google.com") String url) throws InterruptedException, MalformedURLException {
        if (useCloudEnv){
            getCloudDriver(envName, os,osVersion,browserName,browserVersion, username, password);
        }else {
            getLocalDriver(browserName);
        }
        if (maximizeBrowser.equalsIgnoreCase("true")){
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.parseInt(implicitWait)));
        }
        driver.manage().window().maximize();
        driver.get(url);
    }
    //    @AfterMethod
//    public void tearDown(){
//        driver.close();
//    }
    //generic methods
    public WebDriver getDriver() {
        return driver;
    }
    public String getCurrentTitle(){
        return driver.getTitle();
    }
    public String getTextFromElement(WebElement element){
        return element.getText();
    }
    public void clickOn(WebElement element){
        element.click();
    }
    public void type(WebElement element, String text){
        element.sendKeys(text);
    }
    public void typeAndEnter(WebElement element, String text){
        element.sendKeys(text, Keys.ENTER);
    }
    public void selectOptionFromDropdown(WebElement dropdown, String option){
        Select select = new Select(dropdown);
        try {
            select.selectByVisibleText(option);
        }catch (Exception e){
            select.selectByValue(option);
        }
    }
    public void hoverOver(WebDriver driver, WebElement element){
        Actions actions = new Actions(driver);
        actions.moveToElement(element).build().perform();
    }
    public void waitForElementToBeVisible(WebDriver driver, int duration, WebElement element){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(duration));
        wait.until(ExpectedConditions.visibilityOf(element));
    }
    public void clickWithActions(WebDriver driver, WebElement element){
        Actions actions = new Actions(driver);
        actions.moveToElement(element).click().build().perform();
    }
    public void clickWithJavascript(WebElement element){
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("arguments[0].click();", element);
    }
    public void captureScreenshot() {
        File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(file,new File("screenshots"+File.separator+"screenshot.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void takeScreenshot(String screenshotName){
        DateFormat df = new SimpleDateFormat("MMddyyyyHHmma");
        Date date = new Date();
        df.format(date);

        File file = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(file, new File(Utility.path + File.separator +"screenshots"+ File.separator + screenshotName+" "+df.format(date)+".jpeg"));
            LOG.info("Screenshot captured");
        } catch (Exception e) {
            LOG.info("Exception while taking screenshot "+e.getMessage());
        }
    }
}
