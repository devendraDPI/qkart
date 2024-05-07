package qkart;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


public class App {
    public static String lastGeneratedUsername;

    public static WebDriver createDriver() {
        WebDriver driver = new ChromeDriver(); // Launch chrome browser
        driver.manage().window().maximize(); // Maximize browser window
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20)); // Implicitly wait
        return driver;
    }

    public static void logStatus(String testCaseID, String testStep, String testMessage, String testStatus) {
        System.out.println(String.format("%s | %s | %s | %s | %s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), testCaseID, testStep, testMessage, testStatus));
    }

    /**
     * Verify the functionality of login button on the home page
     */
    public static Boolean TestCase01(WebDriver driver) throws InterruptedException {
        logStatus("TC001", "Start", "Verify user registration", "DONE");
        Boolean status;

        // Visit the registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        if (!status) {
            logStatus("TC001", "Step", "Test case FAIL. User registration FAIL", "FAIL");
            logStatus("TC001", "End", "Verify user registration", "FAIL");
            // Return false as the test case fails
            return false;
        } else {
            logStatus("TC001", "End", "Test case PASS. User registration PASS", "PASS");
        }

        // Save the last generated username
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Visit the login page and login with the previously registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        logStatus("TC001", "Step", "User Perform Login", status ? "PASS" : "FAIL");
        if (!status) {
            logStatus("TC001", "End", "Verify user Registration", status ? "PASS" : "FAIL");
            return false;
        }

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.logoutUser();
        logStatus("TC001", "End", "Verify user Registration", status ? "PASS" : "FAIL");

        return status;
    }

    /**
     * Verify that an existing user is not allowed to re-register on QKart
     */
    public static Boolean TestCase02(WebDriver driver) throws InterruptedException {
        logStatus("TC002", "Start", "Verify user registration with an existing username", "DONE");
        Boolean status;

        // Visit the registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        logStatus("TC002", "Step", "User registration", status ? "PASS" : "FAIL");
        if (!status) {
            logStatus("TC002", "End", "Verify user registration", status ? "PASS" : "FAIL");
            return false;
        }

        // Save the last generated username
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Visit the registration page and try to register using the previously registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUsername, "abc@123", false);

        // If status is true, then registration succeeded, else registration has failed. In this case registration failure means Success
        logStatus("TC002", "End", "Verify user registration", status ? "FAIL" : "PASS");
        return !status;
    }

    public static void main(String[] args) throws InterruptedException {
        int totalTests = 0;
        int passedTests = 0;
        Boolean status;
        WebDriver driver = createDriver();

        driver.manage().window().maximize(); // Maximize browser window
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20)); // Implicitly wait

        try {
            // Execute TC001
            totalTests += 1;
            status = TestCase01(driver);
            if (status) {
                passedTests += 1;
            }
            System.out.println("");

            // Execute TC002
            totalTests += 1;
            status = TestCase02(driver);
            if (status) {
                passedTests += 1;
            }
            System.out.println("");
        } catch (Exception e) {
            throw e;
        } finally {
            // Quit chrome driver
            driver.quit();
            System.out.println(String.format("%s out of %s test cases passed", Integer.toString(passedTests), Integer.toString(totalTests)));
        }
    }
}
