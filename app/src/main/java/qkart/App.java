package qkart;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class App {
    public static String lastGeneratedUsername;

    public static WebDriver createDriver() {
        ChromeDriver driver = new ChromeDriver(); // Launch chrome browser
        driver.manage().window().maximize(); // Maximize browser window
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20)); // Implicitly wait
        return driver;
    }

    public static void logStatus(String testCaseID, String testStep, String testMessage, String testStatus) {
        System.out.println(String.format("%s | %s | %s | %s | %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), testCaseID, testStep, testMessage, testStatus));
    }

    public static Boolean TestCase01(WebDriver driver) throws InterruptedException {
        logStatus("TC001", "Start", "Verify the functionality of Login Button on the home page", "DONE");
        Boolean status;

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        Thread.sleep(2000);

        status = homePage.verifyLoginButton();
        logStatus("TC001", "Step", "Verify login button exists", status ? "PASS" : "FAIL");

        status = homePage.clickLoginButton();
        logStatus("TC001", "Step", "Verify clicked on login button", status ? "PASS" : "FAIL");

        status = driver.getCurrentUrl().endsWith("/login");
        logStatus("TC001", "Step", "Verify that user navigates to login page", status ? "PASS" : "FAIL");

        logStatus("TC001", "End", "Verify functionality of login button on home page", status ? "PASS" : "FAIL");
        return status;
    }

    public static Boolean TestCase02(WebDriver driver) throws InterruptedException {
        logStatus("TC002", "Start", "Verify functionality of register button on home page", "DONE");
        Boolean status;

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        Thread.sleep(2000);

        status = homePage.verifyRegisterButton();
        logStatus("TC002", "Step", "Verify register button exists", status ? "PASS" : "FAIL");

        status = homePage.clickRegisterButton();
        logStatus("TC002", "Step", "Verify clicked on login button", status ? "PASS" : "FAIL");

        status = driver.getCurrentUrl().endsWith("/register");
        logStatus("TC002", "Step", "Verify that user navigates to register page", status ? "PASS" : "FAIL");

        logStatus("TC002", "End", "Verify functionality of register button on home page", status ? "PASS" : "FAIL");
        return status;
    }

    public static void main(String[] args) throws InterruptedException {
        WebDriver driver = createDriver();

        TestCase01(driver);
        TestCase02(driver);

        driver.quit();
    }
}
