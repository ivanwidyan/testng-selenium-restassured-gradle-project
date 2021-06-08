import driver.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.util.List;

public class LoginTest extends PageObject {

    @BeforeClass
    public void setup() {
        System.out.println("open facebook");
        driver.get("https://www.facebook.com/");
    }

    @Test(priority = 0)
    public void verifyTitle() {
        Assert.assertTrue(driver.getTitle().contains("Facebook"), "Title is not matched");
    }

    @Test(priority = 1)
    public void verifyLogo() {
        WebElement logo = driver.findElement(By.xpath("//img[@alt='Facebook']"));
        Assert.assertTrue(logo.isDisplayed(), "Logo is not displayed");
    }

    @Test(
            priority = 2,
            dependsOnMethods = {"verifyTitle", "verifyLogo"},
            alwaysRun = true)
    @Parameters({"keyword"})
    public void search(String keyword) {
        WebElement searchBox = driver.findElement(By.xpath("//input[@class='lst tiah']"));
        searchBox.sendKeys(keyword + "\n");
    }

    @Test(
            priority = 3,
            dependsOnMethods = "search")
    @Parameters({"keyword"})
    public void checkSearchResult(String keyword) {
        Boolean verified;
        System.out.println("start");

        // only one result
        /*WebElement searchResult = driver.findElement(By.xpath("//div/div/a/h3/div"));
        Boolean searchResultVerified = searchResult.getText().toLowerCase().contains(keyword);*/

        // all search results
        List<WebElement> searchResult = driver.findElements(By.xpath("//div/div/a/h3/div"));
        searchResult.stream().forEach(l -> System.out.println(l.getText()));
        Boolean searchResultVerified = searchResult.stream().allMatch(
                l -> l.getText().toLowerCase().contains(keyword));

        Assert.assertTrue(searchResultVerified, "not all titles contain " + keyword);
    }
}
