package pages;

import base.CommonAPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

    public class HomePage extends CommonAPI {

        Logger LOG = LogManager.getLogger(pages.HomePage.class.getName());

        public HomePage(WebDriver driver){
            PageFactory.initElements(driver, this);
        }
        //object
        @FindBy(css = "#twotabsearchtextbox")
        WebElement searchField;

        @FindBy(css = "#nav-search-submit-button")
        WebElement searchButton;

        @FindBy(xpath = "//span[@id='nav-link-accountList-nav-line-1']")
        WebElement floatingMenu;

        @FindBy(xpath = "(//span[text()='Sign in'])[1]")
        WebElement loginButton;

        @FindBy(css = "#searchDropdownBox")
        WebElement menuDropdown;

        //reusable steps
        public void typeItemToSearch(String item){
            type(searchField, item);
            LOG.info("item name type success");
        }
        public void clickOnSearchButton(){
            clickOn(searchButton);
            LOG.info("click search success");
        }
        public void searchItem(String item){
            typeAndEnter(searchField, item);
            LOG.info("item name type and enter success");
        }
        public void selectOptionFromMenuDropdown(String option){
            selectOptionFromDropdown(menuDropdown, option);
            LOG.info(option+" option selected from the dropdown");
        }
        public void hoverOverFloatingMenu(WebDriver driver){
            hoverOver(driver, floatingMenu);
            LOG.info("hover over menu success");
        }
        public void clickOnLoginButton(){
            clickOn(loginButton);
            LOG.info("click on login button success");
        }
    }


