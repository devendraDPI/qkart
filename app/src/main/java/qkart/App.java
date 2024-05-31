package qkart;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class App {
    public static String lastGeneratedUsername;

    public static WebDriver createDriver() {
        WebDriver driver = new ChromeDriver(); // Launch chrome browser
        driver.manage().window().maximize(); // Maximize browser window
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5)); // Implicitly wait
        return driver;
    }

    public static void logStatus(String testCaseID, String testStep, String testMessage, String testStatus) {
        System.out.println(String.format("%s | %s | %s | %s | %s", getDateTime("yyyy-MM-dd HH:mm:ss"), testCaseID, testStep, testMessage, testStatus));
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
        /*
         * 1. Check if the folder "/screenshots" exists, create if it doesn't
         * 2. Generate a unique string using the timestamp
         * 3. Capture screenshot
         * 4. Save the screenshot inside the "/screenshots" folder using the following
         * naming convention: screenshot_<Timestamp>_<ScreenshotType>_<Description>.png
         * eg: screenshot_2022-03-05T06:59:46.015489_StartTestcase_Testcase01.png
         */
        try {
            File screenshotDir = new File(File.separator + "screenshots");

            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            String fileName = String.format("screenshot_%s_%s_%s.png", getDateTime("yyyyMMddHHmmss"), screenshotType, description);

            TakesScreenshot scrShot = ((TakesScreenshot) driver);
            File SrcFile = scrShot.getScreenshotAs(OutputType.FILE);

            File DestFile = new File("screenshots" + File.separator + fileName);
            FileUtils.copyFile(SrcFile, DestFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDateTime(String formatPattern) {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern(formatPattern));
    }

    /**
     * Verify the functionality of login button on the home page
     */
    public static Boolean TestCase01(WebDriver driver) throws InterruptedException {
        logStatus("TC001", "Start", "Verify user registration", "DONE");
        takeScreenshot(driver, "Start", "TC001");

        Boolean status;

        // Visit the registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        if (!status) {
            logStatus("TC001", "Step", "Test case FAIL. User registration FAIL", "FAIL");
            logStatus("TC001", "End", "Verify user registration", "FAIL");
            takeScreenshot(driver, "Fail", "TC001");
            // Return false as the test case fails
            return false;
        } else {
            logStatus("TC001", "Step", "Test case PASS. User registration PASS", "PASS");
        }

        // Save the last generated username
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Visit the login page and login with the previously registered user
        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        logStatus("TC001", "Step", "User Perform Login", status ? "PASS" : "FAIL");
        if (!status) {
            logStatus("TC001", "End", "Verify user Registration", status ? "PASS" : "FAIL");
            takeScreenshot(driver, "Fail", "TC001");
            return false;
        }

        // Visit the home page and log out the logged in user
        Home home = new Home(driver);
        status = home.logoutUser();

        logStatus("TC001", "End", "Verify user Registration", status ? "PASS" : "FAIL");
        takeScreenshot(driver, "End", "TC001");

        return status;
    }

    /**
     * Verify that an existing user is not allowed to re-register on QKart
     */
    public static Boolean TestCase02(WebDriver driver) throws InterruptedException {
        logStatus("TC002", "Start", "Verify user registration with an existing username", "DONE");
        takeScreenshot(driver, "Start", "TC002");

        Boolean status;

        // Visit the registration page and register a new user
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        logStatus("TC002", "Step", "User registration", status ? "PASS" : "FAIL");
        if (!status) {
            logStatus("TC002", "End", "Verify user registration", status ? "PASS" : "FAIL");
            takeScreenshot(driver, "Fail", "TC002");
            return false;
        }

        // Save the last generated username
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Visit the registration page and try to register using the previously registered user's credentials
        registration.navigateToRegisterPage();
        status = registration.registerUser(lastGeneratedUsername, "abc@123", false);

        // If status is true, then registration succeeded, else registration has failed. In this case registration failure means Success
        logStatus("TC002", "End", "Verify user registration", status ? "FAIL" : "PASS");
        takeScreenshot(driver, "End", "TC002");

        return !status;
    }

    /*
     * Verify the functionality of the search text box
     */
    public static Boolean TestCase03(WebDriver driver) throws InterruptedException {
        logStatus("TC003", "Start", "Verify functionality of search box", "DONE");
        takeScreenshot(driver, "Start", "TC003");

        boolean status;

        // Visit the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for the "yonex" product
        status = homePage.searchForProduct("yonex");
        if (!status) {
            logStatus("TC003", "End", "Unable to search for given product", "FAIL");
            takeScreenshot(driver, "Fail", "TC003");
            return false;
        }

        // Fetch the search results
        List<WebElement> searchResults = homePage.getSearchResults();

        // Verify the search results are available
        if (searchResults.size() == 0) {
            logStatus("TC003", "End", "There were no results for the given search string", "FAIL");
            takeScreenshot(driver, "Fail", "TC003");
            return false;
        }

        for (WebElement webElement : searchResults) {
            // Create a SearchResult object from the parent element
            SearchResult resultWebElement = new SearchResult(webElement);

            // Verify that all results contain the searched text
            String elementText = resultWebElement.getTitleOfResult();
            if (!elementText.toUpperCase().contains("YONEX")) {
                logStatus("TC003", "End", "Test Results contains un-expected values: " + elementText, "FAIL");
                takeScreenshot(driver, "Fail", "TC003");
                return false;
            }
        }

        logStatus("TC003", "Step", "Successfully validated the search results", "PASS");

        // SLEEP_STMT_02: Wait for Page to Load
        Thread.sleep(2000);

        // Search for product
        status = homePage.searchForProduct("Gesundheit");
        if (!status) {
            logStatus("TC003", "End", "Unable to search for given product", "FAIL");
            takeScreenshot(driver, "Fail", "TC003");
            return false;
        }

        // Verify no search results are found
        searchResults = homePage.getSearchResults();
        if (searchResults.size() == 0) {
            if (homePage.isNoResultFound()) {
                logStatus("TC003", "Step", "Successfully validated that no products found message is displayed", "PASS");
            }
            logStatus("TC003", "End", "Verified that no search results were found for the given text", "PASS");
        } else {
            logStatus("TC003", "End", "Expected: no results, actual: Results were available", "FAIL");
            takeScreenshot(driver, "Fail", "TC003");
            return false;
        }

        takeScreenshot(driver, "End", "TC003");

        return true;
    }

    /*
     * Verify the presence of size chart and check if the size chart content is as expected
     */
    public static Boolean TestCase04(WebDriver driver) throws InterruptedException {
        logStatus("TC004", "Start", "Verify the presence of size Chart", "DONE");
        takeScreenshot(driver, "Start", "TC004");

        boolean status = false;

        // Visit home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Search for product and get card content element of search results
        status = homePage.searchForProduct("Running Shoes");
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
            if (result.verifySizeChartExists()) {
                logStatus("TC004", "Step", "Successfully validated presence of Size Chart Link", "PASS");

                // Verify if size dropdown exists
                status = result.verifyExistenceOfSizeDropdown(driver);
                logStatus("TC004", "Step", "Validated presence of drop down", status ? "PASS" : "FAIL");

                // Open the size chart
                if (result.openSizeChart()) {
                    // Verify if the size chart contents matches the expected values
                    if (result.validateSizeChartContents(expectedTableHeaders, expectedTableBody, driver)) {
                        logStatus("TC004", "Step", "Successfully validated contents of Size Chart Link", status ? "PASS" : "PASS");
                    } else {
                        logStatus("TC004", "Step", "Failure while validating contents of Size Chart Link", "FAIL");
                        takeScreenshot(driver, "Fail", "TC004");
                    }
                    // Close the size chart modal
                    status = result.closeSizeChart(driver);
                } else {
                    logStatus("TC004", "End", "Failure to open Size Chart", "FAIL");
                    takeScreenshot(driver, "Fail", "TC004");
                    return false;
                }
            } else {
                logStatus("TC004", "End", "Size Chart Link does not exist", "FAIL");
                takeScreenshot(driver, "Fail", "TC004");
                return false;
            }
        }

        logStatus("TC004", "End", "Validated Size Chart Details", "PASS");
        takeScreenshot(driver, "End", "TC004");

        return status;
    }

    /*
     * Verify the complete flow of checking out and placing order for products is working correctly
     */
    public static Boolean TestCase05(WebDriver driver) throws InterruptedException {
        logStatus("TC005", "Start", "Verify Happy Flow of buying products", "DONE");
        takeScreenshot(driver, "Start", "TC005");

        Boolean status;

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        if (!status) {
            logStatus("TC005", "End", "Happy Flow Test Failed", "FAIL");
            takeScreenshot(driver, "Fail", "TC005");
        }

        // Save the username of the newly registered user
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        if (!status) {
            logStatus("TC005", "Step", "User Perform Login Failed", status ? "PASS" : "FAIL");
            logStatus("TC005", "End", "Happy Flow Test Failed", status ? "PASS" : "FAIL");
            takeScreenshot(driver, "Fail", "TC005");
        }

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("Yonex");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

        status = homePage.searchForProduct("Tan");
        homePage.addProductToCart("Tan Leatherette Weekender Duffle");

        // Click on the checkout button
        homePage.clickCheckout();

        // Add a new address on the Checkout page and select it
        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        // Place the order
        checkoutPage.placeOrder();

        // Wait for place order to succeed and navigate to Thanks page
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/thanks"));

        // Check if placing order redirected to the Thanks page
        status = driver.getCurrentUrl().endsWith("/thanks");

        // Go to the home page
        homePage.navigateToHome();

        // Log out the user
        homePage.logoutUser();

        logStatus("TC005", "End", "Happy Flow Test Completed", status ? "PASS" : "FAIL");
        takeScreenshot(driver, "End", "TC005");

        return status;
    }

    /*
     * Verify the quantity of items in cart can be updated
     */
    public static Boolean TestCase06(WebDriver driver) throws InterruptedException {
        logStatus("TC006", "Start", "Verify that cart can be edited", "DONE");
        takeScreenshot(driver, "Start", "TC006");

        Boolean status;

        Home homePage = new Home(driver);
        Register registration = new Register(driver);
        Login login = new Login(driver);

        // Register a new user
        // Go to the Register page
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        if (!status) {
            logStatus("TC006", "End", "Happy Flow Test Failed", "FAIL");
            takeScreenshot(driver, "Fail", "TC006");
        }

        // Save the username of the newly registered user
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Login using the newly registered user
        // Go to the login page
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        if (!status) {
            logStatus("TC006", "Step", "User Perform Login Failed", status ? "PASS" : "FAIL");
            logStatus("TC006", "End", "Happy Flow Test Failed", status ? "PASS" : "FAIL");
            takeScreenshot(driver, "Fail", "TC006");
            return false;
        }

        // Add "Xtend Smart Watch" to cart
        status = homePage.searchForProduct("Xtend");
        homePage.addProductToCart("Xtend Smart Watch");

        // Add "Yarine Floor Lamp" to cart
        status = homePage.searchForProduct("Yarine");
        homePage.addProductToCart("Yarine Floor Lamp");

        // update watch quantity to 2
        homePage.changeProductQuantityInCart("Xtend Smart Watch", 2);

        // update table lamp quantity to 0
        homePage.changeProductQuantityInCart("Yarine Floor Lamp", 0);

        // update watch quantity again to 1
        homePage.changeProductQuantityInCart("Xtend Smart Watch", 1);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();

        // Wait for place order to succeed and navigate to Thanks page
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.urlContains("/thanks"));

        status = driver.getCurrentUrl().endsWith("/thanks");

        homePage.navigateToHome();

        homePage.logoutUser();

        logStatus("TC006", "End", "Verify that cart can be edited", status ? "PASS" : "FAIL");
        takeScreenshot(driver, "End", "TC006");

        return status;
    }

    /**
     * Verify that insufficient balance error is thrown when the wallet balance is not enough
     */
    public static Boolean TestCase07(WebDriver driver) throws InterruptedException {
        logStatus("TC007", "Start", "Verify that insufficient balance error is thrown when the wallet balance is not enough", "DONE");
        takeScreenshot(driver, "Start", "TC007");

        Boolean status;

        Register registration = new Register(driver);
        registration.navigateToRegisterPage();
        status = registration.registerUser("testUser", "abc@123", true);
        if (!status) {
            logStatus("TC007", "Step", "User Perform Registration Failed", status ? "PASS" : "FAIL");
            logStatus("TC007", "End", "Verify that insufficient balance error is thrown when the wallet balance is not enough", status ? "PASS" : "FAIL");
            takeScreenshot(driver, "Fail", "TC007");
            return false;
        }
        lastGeneratedUsername = registration.lastGeneratedUsername;

        Login login = new Login(driver);
        login.navigateToLoginPage();
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        if (!status) {
            logStatus("TC007", "Step", "User Perform Login Failed", status ? "PASS" : "FAIL");
            logStatus("TC007", "End", "Verify that insufficient balance error is thrown when the wallet balance is not enough", status ? "PASS" : "FAIL");
            takeScreenshot(driver, "Fail", "TC007");
            return false;
        }

        Home homePage = new Home(driver);
        homePage.navigateToHome();
        status = homePage.searchForProduct("Stylecon");
        homePage.addProductToCart("Stylecon 9 Seater RHS Sofa Set");

        homePage.changeProductQuantityInCart("Stylecon 9 Seater RHS Sofa Set", 10);

        homePage.clickCheckout();

        Checkout checkoutPage = new Checkout(driver);
        checkoutPage.addNewAddress("Addr line 1 addr Line 2 addr line 3");
        checkoutPage.selectAddress("Addr line 1 addr Line 2 addr line 3");

        checkoutPage.placeOrder();
        Thread.sleep(3000);

        status = checkoutPage.verifyInsufficientBalanceMessage();

        logStatus("TC007", "End", "Verify that insufficient balance error is thrown when the wallet balance is not enough", status ? "PASS" : "FAIL");
        takeScreenshot(driver, "End", "TC007");

        return status;
    }

    /**
     * Verify that a product added to a cart is available when a new tab is added
     */
    public static Boolean TestCase08(WebDriver driver) throws InterruptedException {
        logStatus("TC008", "Start", "Verify that a product added to a cart is available when a new tab is added", "DONE");
        takeScreenshot(driver, "Start", "TC008");

        Boolean status;

        // Go to the Register page
        Register registration = new Register(driver);
        registration.navigateToRegisterPage();

        // Register a new user
        status = registration.registerUser("testUser", "abc@123", true);
        if (!status) {
            logStatus("TC008", "End", "Verify that a product added to a cart is available when a new tab is added", "FAIL");
            takeScreenshot(driver, "Fail", "TC008");
        }

        // Save the username of the newly registered user
        lastGeneratedUsername = registration.lastGeneratedUsername;

        // Go to the login page
        Login login = new Login(driver);
        login.navigateToLoginPage();

        // Login with the newly registered user's credentials
        status = login.loginUser(lastGeneratedUsername, "abc@123");
        if (!status) {
            logStatus("TC008", "Step", "User Perform Login Failed", status ? "PASS" : "FAIL");
            logStatus("TC008", "End", "Verify that a product added to a cart is available when a new tab is added", status ? "PASS" : "FAIL");
            takeScreenshot(driver, "Fail", "TC008");
        }

        // Go to the home page
        Home homePage = new Home(driver);
        homePage.navigateToHome();

        // Find required products by searching and add them to the user's cart
        status = homePage.searchForProduct("Yonex");
        homePage.addProductToCart("YONEX Smash Badminton Racquet");

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

        takeScreenshot(driver, "End", "TestCase08");
        logStatus("TC008", "End", "Verify that a product added to a cart is available when a new tab is added", status ? "PASS" : "FAIL");

        // Close new tab
        driver.close();

        // Switch to main tab
        driver.switchTo().window(windows[0]);

        // Logout
        homePage.logoutUser();

        return status;
    }

    public static Boolean TestCase9(WebDriver driver) throws InterruptedException {
        // TODO: CRIO_TASK_MODULE_SYNCHRONISATION
        Boolean status = false;
        return status;
    }

    public static Boolean TestCase10(WebDriver driver) throws InterruptedException {
        Boolean status = false;
        // TODO: CRIO_TASK_MODULE_SYNCHRONISATION
        return status;
    }

    public static Boolean TestCase11(WebDriver driver) throws InterruptedException {
        Boolean status = false;
        // TODO: CRIO_TASK_MODULE_SYNCHRONISATION
        return status;
    }

    public static void main(String[] args) throws InterruptedException {
        int totalTests = 0;
        int passedTests = 0;
        Boolean status;
        WebDriver driver = createDriver();

        try {
            // Execute TC001
            totalTests += 1;
            status = TestCase01(driver);
            if (status) {
                passedTests += 1;
            }
            System.out.println("");

            // Execute TC002
            totalTests += 1;
            status = TestCase02(driver);
            if (status) {
                passedTests += 1;
            }
            System.out.println("");

            // Execute TC003
            totalTests += 1;
            status = TestCase03(driver);
            if (status) {
                passedTests += 1;
            }
            System.out.println("");

            // Execute TC004
            totalTests += 1;
            status = TestCase04(driver);
            if (status) {
                passedTests += 1;
            }
            System.out.println("");

            // Execute TC005
            totalTests += 1;
            status = TestCase05(driver);
            if (status) {
                passedTests += 1;
            }
            System.out.println("");

            // Execute TC006
            totalTests += 1;
            status = TestCase06(driver);
            if (status) {
                passedTests += 1;
            }
            System.out.println("");

            // Execute TC007
            totalTests += 1;
            status = TestCase07(driver);
            if (status) {
                passedTests += 1;
            }
            System.out.println("");

            // Execute TC008
            totalTests += 1;
            status = TestCase08(driver);
            if (status) {
                passedTests += 1;
            }
            System.out.println("");

            // Execute TC009
            // totalTests += 1;
            // status = TestCase09(driver);
            // if (status) {
            //     passedTests += 1;
            // }
            // System.out.println("");

            // Execute TC010
            // totalTests += 1;
            // status = TestCase10(driver);
            // if (status) {
            //     passedTests += 1;
            // }
            // System.out.println("");

            // Execute TC011
            // totalTests += 1;
            // status = TestCase11(driver);
            // if (status) {
            //     passedTests += 1;
            // }
            // System.out.println("");
        } catch (Exception e) {
            throw e;
        } finally {
            // Quit chrome driver
            driver.quit();
            System.out.println(String.format("%s out of %s test cases passed", Integer.toString(passedTests), Integer.toString(totalTests)));
        }
    }
}
