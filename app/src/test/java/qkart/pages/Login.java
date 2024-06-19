package qkart.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

public class Login {
    WebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/login";

    public Login(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToLoginPage() {
        if (!driver.getCurrentUrl().equals(this.url)) {
            driver.get(this.url);
        }
    }

    public Boolean loginUser(String username, String password) throws InterruptedException {
        // Find the username text box
        WebElement usernameTextBox = this.driver.findElement(By.id("username"));

        // Enter the username
        usernameTextBox.sendKeys(username);

        // Find the password text box
        WebElement passwordTextBox = this.driver.findElement(By.id("password"));

        // Enter the password
        passwordTextBox.sendKeys(password);

        // Find the login button
        WebElement loginButton = driver.findElement(By.className("button"));

        // Click the login button
        loginButton.click();

        // Wait for Login to Complete
        FluentWait<WebDriver> fWait = new FluentWait<WebDriver>(driver)
                                    .withTimeout((Duration.ofSeconds(30)))
                                    .pollingEvery(Duration.ofMillis(250));
        fWait.until(ExpectedConditions.invisibilityOf(loginButton));

        return this.verifyUserLoggedIn(username);
    }

    public Boolean verifyUserLoggedIn(String username) {
        try {
            // Find the username label is present on the top right of the page
            WebElement usernameLabel = this.driver.findElement(By.className("username-text"));
            return usernameLabel.getText().equals(username);
        } catch (Exception e) {
            return false;
        }
    }
}
