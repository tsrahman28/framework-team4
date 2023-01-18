package base;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import java.net.MalformedURLException;
import java.time.Duration;

public class CommonAPI {
    public WebDriver driver;
    public void getLocalDriver(String browserName){
        if (browserName.equalsIgnoreCase("chrome")){
            driver = new ChromeDriver();
        }else if (browserName.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
        } else if (browserName.equalsIgnoreCase("edge")) {
            driver = new EdgeDriver();
        }
    }


    @Parameters({"useCloudEnv","envName","os","osVersion","browserName","browserVersion","url"})
    @BeforeMethod
    public void setUp(@Optional("false") boolean useCloudEnv, @Optional("browserstack") String envName,
                      @Optional("windows") String os, @Optional("11") String osVersion,
                      @Optional("chrome") String browserName, @Optional("108") String browserVersion,
                      @Optional("https://www.google.com") String url) throws InterruptedException, MalformedURLException {


        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(url);
    }
    @AfterMethod
    public void tearDown(){
        driver.close();
    }

    //generic methods
    public WebDriver getDriver() {
        return driver;
    }

    public String getCurrentTitle() {
        return driver.getTitle();
    }

    public String getTextFromElement(WebElement element) {
        return element.getText();
    }

    public void clickOn(WebElement element) {
        element.click();
    }

    public void type(WebElement element, String text) {
        element.sendKeys(text);
    }
    public void typeAndEnter(WebElement element, String text) {
        element.sendKeys(text, Keys.ENTER);
    }
}
