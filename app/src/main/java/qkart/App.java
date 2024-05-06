package qkart;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Initializing Test");

        ChromeDriver driver = new ChromeDriver(); // Launch chrome browser
        driver.manage().window().maximize(); // Maximize browser window
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20)); // Implicitly wait

        // TC001
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC001 | Start Test: Verify the functionality of Login Button on the home page");
        driver.get("https://crio-qkart-frontend-qa.vercel.app/");
        Thread.sleep(2000);

        try {
            // Find the Login button
            WebElement loginButton = driver.findElement(By.xpath("//button[normalize-space()='Login']"));

            // Check if the Login Button is displayed
            if (loginButton.isDisplayed()) {
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC001 | Step: Login Button is displayed in the home page: PASS");
            } else {
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC001 | Step: Login Button is not displayed in the home page: FAIL");
            }

            // Click on the login Button
            loginButton.click();
            Thread.sleep(5000);

            String currentUrl = driver.getCurrentUrl();

            // Check if the user is redirected to the login page
            if (currentUrl.endsWith("/login")) {
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC001 | Step: On clicking the login button, user is navigated to login page: PASS");
            } else {
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC001 | Step: On clicking the login button, user is not navigated to login page: FAIL");
                throw new Exception("TC001: FAIL, When the login button is clicked, page does not re-direct to login page");
            }
        } catch (Exception e) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC001 | End Test: Verify the functionality of Login Button on the home page: FAIL");
            throw new Exception("TC001: FAIL");
        }
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC001 | End Test: Verify the functionality of Login Button on the home page: PASS");

        // TC002
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC002 | Start Test: Verify the functionality of Register Button on the home page");

        // Navigate to the home page of QKART
        driver.get("https://crio-qkart-frontend-qa.vercel.app/");
        Thread.sleep(2000);

        try {
            // Find the Register button
            WebElement registerButton = driver.findElement(By.xpath("//button[normalize-space()='Register']"));

            // Check if the Register Button is displayed
            if (registerButton.isDisplayed()) {
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC002 | Step: Register Button is displayed in the home page: PASS");
            } else {
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC002 | Step: Register Button is not displayed in the home page: FAIL");
            }

            // Click on the register Button
            registerButton.click();
            Thread.sleep(5000);

            String currentUrl = driver.getCurrentUrl();

            // Check if the user is redirected to the registration page
            if (currentUrl.endsWith("/register")) {
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC002 | Step: On clicking the register button, user is navigated to registration page: PASS");
            } else {
                System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC002 | Step: On clicking the register button, user is not navigated to registration page: FAIL");
                throw new Exception("TC002: FAIL, When the register button is clicked, page does not re-direct to register page");
            }

        } catch (Exception e) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC002 | End Test: Verify the functionality of register Button on the home page: FAIL");
            throw new Exception("TC002: FAIL");
        }

        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " | TC002 | End Test: Verify the functionality of register Button on the home page: PASS");

        driver.quit();
    }
}
