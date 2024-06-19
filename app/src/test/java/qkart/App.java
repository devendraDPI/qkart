package qkart;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class App {
    static WebDriver driver;
    public static String lastGeneratedUsername;

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

    @Test
    public void TestCase01() throws InterruptedException {
        Boolean status;

        // Visit the registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Unable to register");

        // Save the last generated username
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Visit the login page and login with the previously registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        Assert.assertTrue(status, "Unable to login");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.logoutUser();
        Assert.assertTrue(status, "Unable to logout");
    }
}
