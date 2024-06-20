package qkart;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class App extends Base {
    public static String lastGeneratedUsername;

    @Test(
        description = "Verify that a new user can register and login to qkart",
        priority = 1
    )
    @Parameters({"TC01_Username", "TC01_Password"})
    public void TestCase01(@Optional("testUser") String username, @Optional("abc@123") String password) throws InterruptedException {
        Boolean status;

        // Visit the registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser(username, password, true);
        Assert.assertTrue(status, "Unable to register");

        // Save the last generated username
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Visit the login page and login with the previously registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.loginUser(lastGeneratedUsername, password);
        Assert.assertTrue(status, "Unable to login");

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.logoutUser();
        Assert.assertTrue(status, "Unable to logout");
    }

    @Test(
        description = "Verify that an existing user is not allowed to re-register on qkart",
        priority = 2
    )
    public void TestCase02() throws InterruptedException {
        Boolean status;

        // Visit the registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Unable to register");

        // Save the last generated username
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Visit the registration page and try to register using the previously registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUsername, "abc@123", false);

        Assert.assertFalse(status, "Able to re-register");
    }

    @Test(
        description = "Verify the functionality of search text box",
        priority = 3
    )
    public void TestCase03() throws InterruptedException {
        boolean status;
        SoftAssert sa = new SoftAssert();

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct("yonex");
        sa.assertTrue(status, String.format("Unable to search for '%s' product", "yonex"));

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        sa.assertTrue(searchResults.size() != 0, String.format("There were no results for '%s'", "yonex"));

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultWebElement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultWebElement.getTitleOfResult();
            sa.assertTrue(elementText.toLowerCase().contains("yonex"), String.format("Results contains un-expected values '%s'", elementText));
        }

        // SLEEP_STMT_02: Wait for Page to Load
        Thread.sleep(2000);

        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        sa.assertTrue(status, String.format("Unable to search for '%s' product", "Gesundheit"));

        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        sa.assertTrue(searchResults.size() == 0, "No search results were found for the given text");
        sa.assertTrue(homePage.isNoResultFound(), "No products found message is displayed");

        sa.assertAll();
    }

    @Test(
        description = "Verify the existence of size chart for certain items and validate contents of size chart",
        priority = 4
    )
    public void TestCase04() throws InterruptedException {
        boolean status = false;
        SoftAssert sa = new SoftAssert();

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct("Running Shoes");
        Assert.assertTrue(status, String.format("Unable to search for '%s' product", "Running Shoes"));

        List<WebElement> searchResults = homePage.getSearchResults();

        // Create expected values
        List<String> expectedTableHeaders = Arrays.asList("Size", "UK/INDIA", "EU", "HEEL TO TOE");
        List<List<String>> expectedTableBody = Arrays.asList(
            Arrays.asList("6", "6", "40", "9.8"),
            Arrays.asList("7", "7", "41", "10.2"),
            Arrays.asList("8", "8", "42", "10.6"),
            Arrays.asList("9", "9", "43", "11"),
            Arrays.asList("10", "10", "44", "11.5"),
            Arrays.asList("11", "11", "45", "12.2"),
            Arrays.asList("12", "12", "46", "12.6")
        );

        // Verify size chart presence and content matching for each search result
        for (WebElement webElement : searchResults) {
            SearchResult result = new SearchResult(webElement);
            // Verify if the size chart exists for the search result
            sa.assertTrue(result.verifySizeChartExists(), "Size chart does not exist");

            // Verify if size dropdown exists
            status = result.verifyExistenceOfSizeDropdown(driver);
            sa.assertTrue(status, "Dropdown not present");

            // Open the size chart
            status = result.openSizeChart();
            sa.assertTrue(status, "Unable to open size chart");

            // Verify if the size chart contents matches the expected values
            sa.assertTrue(result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver), "Unable validate contents of size chart");

            // Close the size chart modal
            status = result.closeSizeChart(driver);
            sa.assertTrue(status, "Unable to close size chart");
        }

        sa.assertAll();
    }

    @Test(
        description = "Verify that a new user can add multiple products in to the cart and checkout",
        priority = 5
    )
    @Parameters({"TC05_ProductToSearch1", "TC05_ProductToSearch2", "TC05_AddressDetails"})
    public void TestCase05(String product1, String product2, String address) throws InterruptedException {
        Boolean status;
        SoftAssert sa = new SoftAssert();

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Unable to register");

        // Save the username of the newly registered user
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        Assert.assertTrue(status, "Unable to login");

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct(product1);
        sa.assertTrue(status, String.format("Unable to search for '%s' product", product1));

        status = homePage.addProductToCart(product1);
        sa.assertTrue(status, String.format("Unable to add '%s' product to cart", product1));

        status = homePage.searchForProduct(product2);
        sa.assertTrue(status, String.format("Unable to search for '%s' product", product2));

        status = homePage.addProductToCart(product2);
        sa.assertTrue(status, String.format("Unable to add '%s' product to cart", product2));

        // Click on the checkout button
        status = homePage.clickCheckout();
        Assert.assertTrue(status, "Unable to click on checkout");

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);

        status = checkoutPage.addNewAddress(address);
        Assert.assertTrue(status, "Unable to add new address");

        status = checkoutPage.selectAddress(address);
        Assert.assertTrue(status, "Unable to select address");

        // Place the order
        status = checkoutPage.placeOrder();
        Assert.assertTrue(status, "Unable to place order");

        // Wait for place order to succeed and navigate to Thanks page
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/thanks"));

        // Check if placing order redirected to the Thanks page
        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue(status, "After placing the order, the page does not redirect to the Thanks page");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        status = homePage.logoutUser();
        Assert.assertTrue(status, "Unable to logout");

        sa.assertAll();
    }

    @Test(
        description = "Verify that the contents of the cart can be edited",
        priority = 6
    )
    @Parameters({"TC06_ProductToSearch1", "TC06_ProductToSearch2"})
    public void TestCase06(String product1, String product2) throws InterruptedException {
        Boolean status;
        SoftAssert sa = new SoftAssert();

        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        // Register a new user
        // Go to the Register page
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Unable to register");

        // Save the username of the newly registered user
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Login using the newly registered user
        // Go to the login page
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        Assert.assertTrue(status, "Unable to login");

        // Add "Xtend Smart Watch" to cart
        status = homePage.searchForProduct(product1);
        sa.assertTrue(status, String.format("Unable to search for '%s' product", product1));

        status = homePage.addProductToCart(product1);
        sa.assertTrue(status, String.format("Unable to add '%s' product to cart", product1));

        // Add "Yarine Floor Lamp" to cart
        status = homePage.searchForProduct(product2);
        sa.assertTrue(status, String.format("Unable to search for '%s' product", product2));

        status = homePage.addProductToCart(product2);
        sa.assertTrue(status, String.format("Unable to add '%s' product to cart", product2));

        // update watch quantity to 2
        status = homePage.changeProductQuantityInCart(product1, 2);
        sa.assertTrue(status, String.format("Unable to change '%s' quantity to '%d'", product1, 2));

        // update table lamp quantity to 0
        status = homePage.changeProductQuantityInCart(product2, 0);
        sa.assertTrue(status, String.format("Unable to change '%s' quantity to '%d'", product2, 0));

        // update watch quantity again to 1
        status = homePage.changeProductQuantityInCart(product1, 1);
        sa.assertTrue(status, String.format("Unable to change '%s' quantity to '%d'", product1, 1));

        status = homePage.clickCheckout();
        Assert.assertTrue(status, "Unable to click on checkout");

        Checkout checkoutPage = new Checkout(driver);

        status = checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        Assert.assertTrue(status, "Unable to add new address");

        status = checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
        Assert.assertTrue(status, "Unable to select address");

        status = checkoutPage.placeOrder();
        Assert.assertTrue(status, "Unable to place order");

        // Wait for place order to succeed and navigate to Thanks page
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/thanks"));

        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue(status, "After placing the order, the page does not redirect to the Thanks page");

        homePage.navigateToHome();

        status = homePage.logoutUser();
        Assert.assertTrue(status, "Unable to logout");

        sa.assertAll();
    }

    @Test(
        description = "Verify that insufficient balance error is thrown when the wallet balance is not enough",
        priority = 7
    )
    @Parameters({"TC07_ProductToSearch", "TC07_Qty"})
    public void TestCase07(String product, Integer qty) throws InterruptedException {
        Boolean status;
        SoftAssert sa = new SoftAssert();

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Unable to register");

        lastGeneratedUsername = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        Assert.assertTrue(status, "Unable to login");

        Home homePage = new Home(driver);
        homePage.navigateToHome();

        status = homePage.searchForProduct(product);
        sa.assertTrue(status, String.format("Unable to search for '%s' product", product));

        status = homePage.addProductToCart(product);
        sa.assertTrue(status, String.format("Unable to add '%s' product to cart", product));

        status = homePage.changeProductQuantityInCart(product, qty);
        sa.assertTrue(status, String.format("Unable to change quantity"));

        status = homePage.clickCheckout();
        Assert.assertTrue(status, "Unable to click on checkout");

        Checkout checkoutPage = new Checkout(driver);

        status = checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        Assert.assertTrue(status, "Unable to add new address");

        status = checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
        Assert.assertTrue(status, "Unable to select address");

        status = checkoutPage.placeOrder();
        Assert.assertTrue(status, "Unable to place order");

        status = checkoutPage.verifyInsufficientBalanceMessage();
        Assert.assertTrue(status, "The error 'Insufficient balance' is not thrown when the wallet balance is insufficient");

        sa.assertAll();
    }

    @Test(
        description = "Verify that a product added to a cart is available when a new tab is added",
        priority = 8
    )
    public void TestCase08() throws InterruptedException {
        Boolean status;
        SoftAssert sa = new SoftAssert();

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Unable to register");

        // Save the username of the newly registered user
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        Assert.assertTrue(status, "Unable to login");

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("Yonex");
        sa.assertTrue(status, String.format("Unable to search for '%s' product", "Yonex"));

        status = homePage.addProductToCart("YONEX Smash Badminton Racquet");
        sa.assertTrue(status, String.format("Unable to add '%s' product to cart", "YONEX Smash Badminton Racquet"));

        // Open a new tab
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.open();");

        Set<String> setWindows = driver.getWindowHandles();
        String[] windows = setWindows.toArray(new String[setWindows.size()]);

        // Switch to new tab
        driver.switchTo().window(windows[1]);

        // Navigate to the home page
        homePage.navigateToHome();

        // Check the contents of the cart
        List<String> expectedProductsNameInCart = new ArrayList<>();
        expectedProductsNameInCart.add("YONEX Smash Badminton Racquet");

        status = homePage.verifyCartContents(expectedProductsNameInCart);
        Assert.assertTrue(status, "Product added is not present in the cart");

        // Close new tab
        driver.close();

        // Switch to main tab
        driver.switchTo().window(windows[0]);

        // Logout
        status = homePage.logoutUser();
        Assert.assertTrue(status, "Unable to logout");

        sa.assertAll();
    }

    @Test(
        description = "Verify that privacy policy and about us links are working fine",
        priority = 9
    )
    public void TestCase09() throws InterruptedException {
        boolean status = false;
        SoftAssert sa = new SoftAssert();

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Go to privacy policy
        WebElement privacyPolicy = driver.findElement(By.xpath("//a[contains(text(), 'Privacy policy')]"));
        privacyPolicy.click();
        Thread.sleep(2000); // No wait method to wait for tab to be open

        // Verify that the url of the current tab does not change
        String currentTabUrl = driver.getCurrentUrl();
        status = currentTabUrl.equals("https://crio-qkart-frontend-qa.vercel.app/");
        sa.assertTrue(status, "The url of the current tab changed");

        Set<String> setWindows = driver.getWindowHandles();
        String[] windows = setWindows.toArray(new String[setWindows.size()]);

        // Switch to first tab
        driver.switchTo().window(windows[1]);

        // Verify if the privacy policy content is available on the newly opened tab
        WebElement privacyPolicyHeading = driver.findElement(By.xpath("//h2[contains(text(), 'Privacy Policy')]"));
        status = privacyPolicyHeading.isDisplayed();
        sa.assertTrue(status, "Privacy policy content is not available on the newly opened tab");

        // Switch to main tab
        driver.switchTo().window(windows[0]);

        // Go to terms of service
        WebElement termsOfService = driver.findElement(By.xpath("//a[contains(text(), 'Terms of Service')]"));
        termsOfService.click();
        Thread.sleep(2000); // No wait method to wait for tab to be open

        // Verify that the url of the current tab does not change
        currentTabUrl = driver.getCurrentUrl();
        status = currentTabUrl.equals("https://crio-qkart-frontend-qa.vercel.app/");
        sa.assertTrue(status, "The url of the current tab changed");

        setWindows = driver.getWindowHandles();
        windows = setWindows.toArray(new String[setWindows.size()]);

        // Switch to second tab
        driver.switchTo().window(windows[2]);

        // Verify if the terms of service content is available on the newly opened tab
        WebElement termsOfServiceHeading = driver.findElement(By.xpath("//h2[contains(text(), 'Terms of Service')]"));
        status = termsOfServiceHeading.isDisplayed();
        sa.assertTrue(status, "Terms of Service content is not available on the newly opened tab");

        // Close second tab
        driver.close();

        // Switch to first tab
        driver.switchTo().window(windows[1]);

        // Close first tab
        driver.close();

        //Switch to main tab
        driver.switchTo().window(windows[0]);

        sa.assertAll();
    }

    @Test(
        description = "Verify that the contact us dialog works fine",
        priority = 10
    )
    public void TestCase10() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Click on the "Contact us" link
        WebElement contactUs = driver.findElement(By.xpath("//p[contains(text(), 'Contact us')]"));
        contactUs.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'card-block')]")));

        // Enter the name
        WebElement name = driver.findElement(By.xpath("//input[contains(@placeholder, 'Name')]"));
        name.sendKeys("crio user");

        // Enter the email
        WebElement email = driver.findElement(By.xpath("//input[contains(@placeholder, 'Email')]"));
        email.sendKeys("criouser@gmail.com");

        // Enter the message
        WebElement message = driver.findElement(By.xpath("//input[contains(@placeholder, 'Message')]"));
        message.sendKeys("Testing the contact us page");

        // Click on the "Contact Now" button
        WebElement contactNowBtn = driver.findElement(By.xpath("//button[contains(text(), 'Contact Now')]"));
        contactNowBtn.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath("//div[contains(@class, 'card-block')]")));

        Assert.assertTrue(true, "Contact us option is not working correctly");
    }

    @Test(
        description = "Verify that the advertisement Links on the qkart page are clickable",
        priority = 11
    )
    public void TestCase11() throws InterruptedException {
        Boolean status;
        SoftAssert sa = new SoftAssert();

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        Assert.assertTrue(status, "Unable to register");

        // Save the username of the newly registered user
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        Assert.assertTrue(status, "Unable to login");

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("Yonex");
        sa.assertTrue(status, String.format("Unable to search for '%s' product", "Yonex"));

        homePage.addProductToCart("YONEX Smash Badminton Racquet");
        sa.assertTrue(status, String.format("Unable to add '%s' product to cart", "YONEX Smash Badminton Racquet"));

        // Click on the checkout button
        status = homePage.clickCheckout();
        Assert.assertTrue(status, "Unable to click on checkout");

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);

        status = checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        Assert.assertTrue(status, "Unable to add new address");

        status = checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");
        Assert.assertTrue(status, "Unable to select address");

        // Place the order
        status = checkoutPage.placeOrder();
        Assert.assertTrue(status, "Unable to place order");

        // Wait for place order to succeed and navigate to Thanks page
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/thanks"));

        // Check if placing order redirected to the Thanks page
        status = driver.getCurrentUrl().endsWith("/thanks");
        Assert.assertTrue(status, "After placing the order, the page does not redirect to the Thanks page");

        List<WebElement> ads = driver.findElements(By.xpath("//iframe"));
        int actualSize = ads.size();
        int expectedSize = 3;
        Assert.assertTrue(actualSize==expectedSize, String.format("%s Advertisements are not available", expectedSize));

        for (int i=1; i<expectedSize; i++) {
            WebElement adv = driver.findElement(By.xpath("(//iframe)["+ i +"]"));
            driver.switchTo().frame(adv);
            WebElement buyNowButton = driver.findElement(By.xpath("//button[contains(text(), 'Buy Now')]"));
            buyNowButton.click();
            wait.until(ExpectedConditions.urlContains("/checkout"));
            status = driver.getCurrentUrl().contains("/checkout");
            sa.assertTrue(status, "After clicking the 'Buy Now' button, the page does not redirect to the checkout page");

            driver.navigate().back();
            driver.switchTo().parentFrame();
        }

        sa.assertAll();
    }
}
