package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class PageObject {

    private String driverName;
    private WebDriver driver;
    private JavascriptExecutor javascriptExecutor;

    // Browserstack
    private static final String BROWSERSTACK_USERNAME = System.getenv("BROWSERSTACK_USERNAME");
    public static final String BROWSERSTACK_AUTOMATE_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
    public static final String BROWSERSTACK_URL = "https://" + BROWSERSTACK_USERNAME + ":" + BROWSERSTACK_AUTOMATE_KEY + "@hub-cloud.browserstack.com/wd/hub";

    @BeforeClass
    @Parameters({"driver",
            "remoteBrowser", "remoteBrowserVersion", "remoteOs",
            "remoteDevice", "remoteRealMobile",
            "remoteOsVersion", "remoteBuild", "remoteName"})
    protected void setupDriver(
            @Optional("chrome") String driver,
            // remote driver web
            @Optional("chrome") String remoteBrowser,
            @Optional("latest") String remoteBrowserVersion,
            @Optional("Windows") String remoteOs,
            // remote driver mobile web
            @Optional("Google Pixel 5") String remoteDevice,
            @Optional("false") String remoteRealMobile,
            // remote driver
            @Optional("10") String remoteOsVersion,
            @Optional("browserstack-build-1") String remoteBuild,
            @Optional("Thread 1") String remoteName) {
        System.out.println("browser: " + driver);
        if (driver.isEmpty()) {
            driver = "chrome";
        }

        setDriverName(driver);

        if (driver.equalsIgnoreCase("chrome")) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions chromeOptions = new ChromeOptions();

            String test = "--start-maximized#disable-notifications#incognito";
            String[] array = test.split("#");
            chromeOptions.addArguments(array);

            this.driver = new ChromeDriver(chromeOptions);
        } else if (driver.equalsIgnoreCase("firefox")) {
            WebDriverManager.firefoxdriver().setup();
            System.out.println("firefox driver is started");
            this.driver = new FirefoxDriver();
        } else if (driver.equalsIgnoreCase("browserstack")) {
            Hashtable<String, String> capsHashtable = new Hashtable<String, String>();

            if (Boolean.parseBoolean(remoteRealMobile)) {
                // Browser stack mobile web capabilities
                capsHashtable.put("device", remoteDevice);
                capsHashtable.put("real_mobile", remoteRealMobile);
            } else {
                // Browser stack web capabilities
                capsHashtable.put("browser", remoteBrowser); // chrome, firefox, safari
                capsHashtable.put("browser_version", remoteBrowserVersion); // latest, latest-beta
                capsHashtable.put("os", remoteOs); // Windows, OS X
            }

            capsHashtable.put("os_version", remoteOsVersion); // Windows = 10, OS X = Big Sur, Google Pixel 5 = 11.0
            capsHashtable.put("build", remoteBuild);
            capsHashtable.put("name", remoteName);

            capsHashtable.put("browserstack.debug", "true");  // for enabling visual logs
            capsHashtable.put("browserstack.console", "info");  // to enable console logs at the info level. You can also use other log levels here
            capsHashtable.put("browserstack.networkLogs", "true");  // to enable network logs to be logged

            DesiredCapabilities caps = new DesiredCapabilities();
            String key;
            Set<String> keys = capsHashtable.keySet();
            Iterator<String> itr = keys.iterator();
            while (itr.hasNext()) {
                key = itr.next();
                caps.setCapability(key, capsHashtable.get(key));
            }

            try {
                System.out.println(BROWSERSTACK_URL);
                this.driver = new RemoteWebDriver(new URL(BROWSERSTACK_URL), caps);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        }
        this.driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        javascriptExecutor = (JavascriptExecutor) this.driver;
    }

    @AfterClass
    protected void closeDriver() {
        driver.quit();
    }

    protected String getDriverName() {
        return driverName;
    }

    protected void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    protected WebDriver getDriver() {
        return driver;
    }

    protected void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    protected JavascriptExecutor getJavascriptExecutor() {
        return javascriptExecutor;
    }

    protected void setJavascriptExecutor(JavascriptExecutor javascriptExecutor) {
        this.javascriptExecutor = javascriptExecutor;
    }
}