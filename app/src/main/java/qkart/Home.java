package qkart;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

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

            // Wait for Logout to complete
            FluentWait<WebDriver> fWait = new FluentWait<WebDriver>(driver)
                                    .withTimeout((Duration.ofSeconds(30)))
                                    .pollingEvery(Duration.ofMillis(250));
            fWait.until(ExpectedConditions.invisibilityOf(logoutButton));
            return true;
        } catch (Exception e) {
            // Error while logout
            return false;
        }
    }

    /**
     * Returns Boolean if searching for the given product name occurs without any errors
     */
    public Boolean searchForProduct(String product) {
        try {
            // Clear the contents of the search box and enter the product name in the search box
            WebElement searchBox = driver.findElement(By.xpath("(//input[contains(@name, 'search')])[1]"));
            searchBox.clear();
            searchBox.sendKeys(product);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//h4[contains(text(), 'No products found')]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'card-actions')]/preceding-sibling::div"))
            ));
            return true;
        } catch (Exception e) {
            System.out.println("Error while searching for a product: " + e.getMessage());
            return false;
        }
    }

    /**
     * Returns Array of Web Elements that are search results and return the same
     */
    public List<WebElement> getSearchResults() {
        List<WebElement> searchResults = new ArrayList<WebElement>() {};
        try {
            // Find all WebElements corresponding to the card content section of each of search results
            searchResults = driver.findElements(By.xpath("//div[contains(@class, 'card-actions')]/preceding-sibling::div"));
        } catch (Exception e) {
            System.out.println("There were no search results: " + e.getMessage());
        }
        return searchResults;
    }

    /**
     * Returns Boolean based on if the "No products found" text is displayed
     */
    public Boolean isNoResultFound() {
        Boolean status = false;
        try {
            // Check the presence of "No products found" text in the web page.
            // Assign status = true if the element is "displayed" else set status = false
            WebElement resultText = driver.findElement(By.xpath("//h4[contains(text(), 'No products found')]"));
            status = resultText.isDisplayed();
        } catch (Exception e) {}
        return status;
    }

    /**
     * Return Boolean if add product to cart is successful
     */
    public Boolean addProductToCart(String productName) {
        try {
            // Iterate through each product on the page to find the WebElement corresponding to the matching
            // productName Click on the "ADD TO CART" button for that element Return true if these operations succeeds
            List<WebElement> products = driver.findElements(By.xpath("//div[contains(@class, 'card-actions')]/preceding-sibling::div/p[1]"));
            List<WebElement> addToCartButton = driver.findElements(By.xpath("//button[contains(text(), 'Add to cart')]"));
            for (int i=0; i<products.size(); i++) {
                if (productName.equals(products.get(i).getText())) {
                    addToCartButton.get(i).click();
                    Thread.sleep(2000);
                    return true;
                }
            }
            System.out.println("Unable to find the given product");
            return false;
        } catch (Exception e) {
            System.out.println("Exception while performing add to cart: " + e.getMessage());
            return false;
        }
    }

    /**
     * Return Boolean denoting the status of clicking on the checkout button
     */
    public Boolean clickCheckout() {
        Boolean status = false;
        try {
            // Find and click on the the checkout button
            WebElement checkoutButton = driver.findElement(By.xpath("//button[contains(text(), 'Checkout')]"));
            checkoutButton.click();
            Thread.sleep(2000);
            status = true;
        } catch (Exception e) {
            System.out.println("Exception while clicking on Checkout: " + e.getMessage());
        }
        return status;
    }

    /**
     * Return Boolean denoting the status of change quantity of product in cart operation
     */
    public Boolean changeProductQuantityInCart(String productName, int quantity) {
        try {
            // Find the item on the cart with the matching productName Increment or decrement the quantity of the
            // matching product until the current quantity is reached (Note: Keep a look out when then input quantity
            // is 0, here we need to remove the item completely from the cart)
            WebElement product = driver.findElement(By.xpath("//div[contains(text(), '"+ productName +"')]/parent::div"));
            WebElement decrementQtyButton = product.findElement(By.xpath(".//button[1]"));
            WebElement incrementQtyButton = product.findElement(By.xpath(".//button[2]"));
            WebElement currentQtyElement = product.findElement(By.xpath(".//div[contains(@data-testid, 'item-qty')]"));
            int currentQty = Integer.parseInt(currentQtyElement.getText());
            int difference = Math.abs(currentQty - quantity);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

            for (int i=0; i<difference; i++) {
                if (currentQty < quantity) {
                    incrementQtyButton.click();
                    wait.until(ExpectedConditions.textToBePresentInElement(currentQtyElement, String.valueOf(currentQty+1)));
                    currentQty++;
                } else {
                    decrementQtyButton.click();
                    wait.until(ExpectedConditions.textToBePresentInElement(currentQtyElement, String.valueOf(currentQty-1)));
                    currentQty--;
                }
            }

            return true;
        } catch (Exception e) {
            if (quantity == 0) {
                return true;
            }
            System.out.println("Exception occurred when updating cart: " + e.getMessage());
            return false;
        }
    }

    /**
     * Return Boolean denoting if the cart contains items as expected
     */
    public Boolean verifyCartContents(List<String> expectedCartContents) {
        try {
            WebElement cartParent = driver.findElement(By.className("cart"));
            List<WebElement> cartContents = cartParent.findElements(By.className("css-zgtx0t"));

            ArrayList<String> actualCartContents = new ArrayList<String>() {
            };
            for (WebElement cartItem : cartContents) {
                actualCartContents.add(cartItem.findElement(By.className("css-1gjj37g")).getText().split("\n")[0]);
            }

            for (String expected : expectedCartContents) {
                if (!actualCartContents.contains(expected)) {
                    return false;
                }
            }

            return true;
        } catch (Exception e) {
            System.out.println("Exception while verifying cart contents: " + e.getMessage());
            return false;
        }
    }
}
