package web;

import driver.PageObject;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;

public class SearchTest extends PageObject {

    @BeforeClass
    public void setup() {
        System.out.println("open google");
        getDriver().get("https://www.google.com/");
    }

    @Test(priority = 1)
    public void verifyTitle() {
        if (getDriverName().equalsIgnoreCase("browserstack")) {
            if (getDriver().getTitle().equals("Google"))
                getJavascriptExecutor().executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\": \"passed\", \"reason\": \"Title matched!\"}}");
            else
                getJavascriptExecutor().executeScript("browserstack_executor: {\"action\": \"setSessionStatus\", \"arguments\": {\"status\":\"failed\", \"reason\": \"Title not matched\"}}");
        } else {
            Assert.assertEquals(getDriver().getTitle(), "Google", "Title is not matched");
        }
    }

    @Test(priority = 2)
    public void verifyLogo() {
        WebElement logo = getDriver().findElement(By.xpath("//img[@alt='Google']"));
        Assert.assertTrue(logo.isDisplayed(), "Logo is not displayed");
    }

    @Test(
            priority = 3,
            dependsOnMethods = {"verifyTitle", "verifyLogo"},
            alwaysRun = true)
    @Parameters({"keyword"})
    public void search(String keyword) throws Exception {
        WebElement searchBox = getDriver().findElement(By.xpath("//div[@class='SDkEP']/div/input"));
        searchBox.sendKeys(keyword);
        searchBox.sendKeys(Keys.ENTER);

//        Thread.sleep(3000);
    }

    @Test(
            priority = 4,
            dependsOnMethods = "search")
    @Parameters({"keyword"})
    public void checkSearchResult(String keyword) {
        System.out.println("start");

        List<WebElement> searchResult = getDriver().findElements(By.xpath("//div/div/div/a/h3"));
        searchResult.stream().forEach(l -> System.out.println(l.getText()));
        Boolean searchResultVerified = searchResult.stream().allMatch(
                l -> l.getText().toLowerCase().contains(keyword));

        Assert.assertTrue(searchResultVerified, "not all titles contain " + keyword);
    }
}
