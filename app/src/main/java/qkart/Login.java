package qkart;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

        // Wait for user name to be entered
        Thread.sleep(1000);

        // Find the password text box
        WebElement passwordTextBox = this.driver.findElement(By.id("password"));

        // Enter the password
        passwordTextBox.sendKeys(password);

        // Find the login button
        WebElement loginButton = driver.findElement(By.className("button"));

        // Click the login button
        loginButton.click();

        // Wait for login action to complete
        Thread.sleep(5000);

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
