package qkart;

import org.openqa.selenium.WebDriver;

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

        return status;
    }

    /**
     * clickLoginButton
     * @return true if login button was clicked
     */
    public Boolean clickLoginButton() {
        boolean status = false;
        // TODO: Implement logic to click on Login button

        return status;
    }

    /**
     * verifyRegisterButton
     * @return true if register button exists
     */
    public Boolean verifyRegisterButton() {
        boolean status = false;
        // TODO: Implement logic to verify existence of register Bb
        return status;
    }

    /**
     * clickRegisterButton
     * @return true if register button was clicked
     */
    public Boolean clickRegisterButton() {
        boolean status = false;
        // TODO: Implement logic to click on register button

        return status;
    }
}
