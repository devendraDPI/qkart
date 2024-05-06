package qkart;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Home {
    WebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app";

    public Home(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToHome() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    /**
     * verifyLoginButton
     * @return true if login button exists
     */
    public Boolean verifyLoginButton() {
        boolean status = false;
        // TODO: Implement logic to verify existence of Login button
        WebElement loginButton = driver.findElement(By.xpath("//button[text()='Login']"));
        status = loginButton.isDisplayed();
        return status;
    }

    /**
     * clickLoginButton
     * @return true if login button was clicked
     */
    public Boolean clickLoginButton() {
        boolean status = false;
        // TODO: Implement logic to click on Login button
        try {
            WebElement loginButton = driver.findElement(By.xpath("//button[text()='Login']"));
            loginButton.click();
            status = true;
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    /**
     * verifyRegisterButton
     * @return true if register button exists
     */
    public Boolean verifyRegisterButton() {
        boolean status = false;
        // TODO: Implement logic to verify existence of register button
        WebElement registerButton = driver.findElement(By.xpath("//button[text()='Register']"));
        status = registerButton.isDisplayed();
        return status;
    }

    /**
     * clickRegisterButton
     * @return true if register button was clicked
     */
    public Boolean clickRegisterButton() {
        boolean status = false;
        // TODO: Implement logic to click on register button
        try {
            WebElement registerButton = driver.findElement(By.xpath("//button[text()='Register']"));
            registerButton.click();
            status = true;
        } catch (Exception e) {
            status = false;
        }
        return status;
    }
}
