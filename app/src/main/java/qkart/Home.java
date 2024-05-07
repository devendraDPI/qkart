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

    public Boolean logoutUser() throws InterruptedException {
        try {
            // Find and click on the logout button
            WebElement logoutButton = driver.findElement(By.className("MuiButton-text"));
            logoutButton.click();

            // Wait for logout to complete
            Thread.sleep(3000);
            return true;
        } catch (Exception e) {
            // Error while logout
            return false;
        }
    }
}
