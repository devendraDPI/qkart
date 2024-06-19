package qkart.pages;

import java.sql.Timestamp;
import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Register {
    WebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/register";
    public String lastGeneratedUsername;

    public Register(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToRegisterPage() {
        if (!driver.getCurrentUrl().equals(this.url)) {
            driver.get(this.url);
        }
    }

    public Boolean registerUser(String username, String password, Boolean makeUsernameDynamic) throws InterruptedException {
        // Find the username text box
        WebElement usernameTextBox = this.driver.findElement(By.id("username"));

        // Get time stamp for generating a unique username
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String testDataUsername;

        if (makeUsernameDynamic) {
            // Concatenate the timestamp to string to form unique timestamp
            testDataUsername = username + "_" + String.valueOf(timestamp.getTime());
        } else {
            testDataUsername = username;
        }

        // Type the generated username in the username field
        usernameTextBox.sendKeys(testDataUsername);

        // Find the password text box
        WebElement passwordTextBox = this.driver.findElement(By.id("password"));
        String testDataPassword = password;

        // Enter the password value
        passwordTextBox.sendKeys(testDataPassword);

        // Find the confirm password text box
        WebElement confirmPasswordTextBox = this.driver.findElement(By.id("confirmPassword"));

        // Enter the confirm password value
        confirmPasswordTextBox.sendKeys(testDataPassword);

        // Find the register now button
        WebElement registerNowButton = this.driver.findElement(By.className("button"));

        // Click the register now button
        registerNowButton.click();

        // Wait for new user to get created in the backend
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.or(
            ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@id, 'notistack')]")),
            ExpectedConditions.urlContains("/login")
        ));

        this.lastGeneratedUsername = testDataUsername;

        return this.driver.getCurrentUrl().endsWith("/login");
    }
}
