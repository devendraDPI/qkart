package qkart;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Checkout {
    WebDriver driver;
    String url = "https://crio-qkart-frontend-qa.vercel.app/checkout";

    public Checkout(WebDriver driver) {
        this.driver = driver;
    }

    public void navigateToCheckout() {
        if (!this.driver.getCurrentUrl().equals(this.url)) {
            this.driver.get(this.url);
        }
    }

    /**
     * Return Boolean denoting the status of adding a new address
     */
    public Boolean addNewAddress(String addressString) {
        Boolean status = false;
        try {
            // Click on the "Add new address" button, enter the addressString in the address text box and click on the
            // "ADD" button to save the address
            WebElement addAddress = driver.findElement(By.xpath("//button[contains(text(), 'Add new address')]"));
            addAddress.click();

            WebElement addressTextArea = driver.findElement(By.xpath("//textarea[contains(@placeholder, 'your complete address')]"));
            addressTextArea.sendKeys(addressString);

            WebElement addButton = driver.findElement(By.xpath("//button[contains(text(), 'Add')]"));
            addButton.click();

            Thread.sleep(3000);

            status = true;
        } catch (Exception e) {
            System.out.println("Exception occurred while entering address: " + e.getMessage());
        }
        return status;
    }

    /**
     * Return Boolean denoting the status of selecting an available address
     */
    public Boolean selectAddress(String addressToSelect) {
        try {
            // Iterate through all the address boxes to find the address box with matching text, addressToSelect and
            // click on it
            List<WebElement> addresses = driver.findElements(By.xpath("//input[contains(@name, 'address')]/parent::span/following-sibling::p"));
            for (WebElement address : addresses) {
                if (address.getText().equals(addressToSelect)) {
                    address.click();
                    Thread.sleep(2000);
                    return true;
                }
            }
            System.out.println("Unable to find the given address");
        } catch (Exception e) {
            System.out.println("Exception Occurred while selecting the given address: " + e.getMessage());
        }
        return false;
    }

    /**
     * Return Boolean denoting the status of place order action
     */
    public Boolean placeOrder() {
        Boolean status = false;
        try {
            // Find the "PLACE ORDER" button and click on it
            WebElement placeOrderButton = driver.findElement(By.xpath("//button[contains(text(), 'PLACE ORDER')]"));
            placeOrderButton.click();
            Thread.sleep(2000);
            status = true;
        } catch (Exception e) {
            System.out.println("Exception while clicking on PLACE ORDER: " + e.getMessage());
        }
        return status;
    }

    /**
     * Return Boolean denoting if the insufficient balance message is displayed
     */
    public Boolean verifyInsufficientBalanceMessage() {
        Boolean status = false;
        try {
            WebElement message = driver.findElement(By.xpath("//div[contains(@class, 'SnackbarItem-message')]"));
            String expectedMessage = "You do not have enough balance in your wallet for this purchase";
            String actualMessage = message.getText();
            if (expectedMessage.equals(actualMessage)) {
                status = true;
            }
        } catch (Exception e) {
            System.out.println("Exception while verifying insufficient balance message: " + e.getMessage());
        }
        return status;
    }
}
