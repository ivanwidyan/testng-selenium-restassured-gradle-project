package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;

import java.util.concurrent.TimeUnit;

public class PageObject {
    public WebDriver driver;

    @BeforeClass
    @Parameters({"browser"})
    public void setupDriver(@Optional("chrome") String browser) {
        System.out.println("browser: " + browser);
        if (browser.isEmpty()) {
            browser = "chrome";
        }

        if (browser.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions chromeOptions = new ChromeOptions();

            String test = "--start-maximized#disable-notifications#incognito";
            String[] array = test.split("#");
            chromeOptions.addArguments(array);

            driver = new ChromeDriver(chromeOptions);
        } else if (browser.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            System.out.println("firefox driver is started");
            driver = new FirefoxDriver();
        }
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
    }

    @AfterClass
    public void closeDriver() {
        driver.quit();
    }
}