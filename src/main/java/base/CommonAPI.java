package base;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utility.Utility;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
public class CommonAPI {

    String username = Utility.decode(Utility.getProperties().getProperty("browserstack.username"));
    String password = Utility.decode(Utility.getProperties().getProperty("browserstack.password"));
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

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        driver.get(url);
    }
    @AfterMethod
    public void tearDown(){
        driver.close();
    }
}
