package qkart;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class Base {
    static WebDriver driver;

    @BeforeSuite
    public static void createDriver() {
        driver = new ChromeDriver(); // Launch chrome browser
        driver.manage().window().maximize(); // Maximize browser window
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Implicitly wait
    }

    @AfterSuite
    public static void quitDriver() {
        driver.quit();
    }
}
