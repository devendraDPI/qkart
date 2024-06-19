package qkart.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class SearchResult {
    WebElement parentWebElement;

    public SearchResult(WebElement searchResultWebElement) {
        this.parentWebElement = searchResultWebElement;
    }

    /**
     * Return title of the parentWebElement denoting the card content section of a search result
     */
    public String getTitleOfResult() {
        String titleOfSearchResult = "";
        // Find the element containing the title (product name) of the search result and assign the extract title text
        // to titleOfSearchResult
        WebElement title = parentWebElement.findElement(By.xpath("./p"));
        titleOfSearchResult = title.getText();
        return titleOfSearchResult;
    }

    /**
     * Return Boolean denoting if the open size chart operation was successful
     */
    public Boolean openSizeChart() {
        try {
            // Find the link of size chart in the parentWebElement and click on it
            WebElement sizeChart = parentWebElement.findElement(By.xpath(".//button[contains(text(), 'Size chart')]"));
            sizeChart.click();
            Thread.sleep(2000);
            return true;
        } catch (Exception e) {
            System.out.println("Exception while opening Size chart: " + e.getMessage());
            return false;
        }
    }

    /**
     * Return Boolean denoting if the close size chart operation was successful
     */
    public Boolean closeSizeChart(WebDriver driver) {
        try {
            Thread.sleep(2000);
            Actions action = new Actions(driver);
            // Clicking on "ESC" key closes the size chart modal
            action.sendKeys(Keys.ESCAPE);
            action.perform();
            Thread.sleep(2000);
            return true;
        } catch (Exception e) {
            System.out.println("Exception while closing the size chart: " + e.getMessage());
            return false;
        }
    }

    /**
     * Return Boolean based on if the size chart exists
     */
    public Boolean verifySizeChartExists() {
        Boolean status = false;
        try {
            // Check if the size chart element exists. If it exists, check if the text of the element is "SIZE CHART".
            // If the text "SIZE CHART" matches for the element, set status = true , else set to false
            WebElement sizeChart = parentWebElement.findElement(By.xpath(".//button[contains(text(), 'Size chart')]"));
            status = sizeChart.isDisplayed() && sizeChart.getText().toLowerCase().equals("size chart");
        } catch (Exception e) {}
        return status;
    }

    /**
     * Return Boolean if the table headers and body of the size chart matches the expected values
     */
    public Boolean validateSizeChartContents(List<String> expectedTableHeaders, List<List<String>> expectedTableBody, WebDriver driver) {
        Boolean status = true;
        try {
            /*
             * Locate the table element when the size chart modal is open
             * Validate that the contents of expectedTableHeaders is present as the table header in the same order
             * Validate that the contents of expectedTableBody are present in the table body in the same order
             */
            for (int i=0; i<expectedTableHeaders.size(); i++) {
                String expectedHeader = expectedTableHeaders.get(i);
                int col = i+1;
                String actualHeader = driver.findElement(By.xpath("//table/thead/tr/th["+ col +"]")).getText();
                if (!expectedHeader.equals(actualHeader)) {
                    status = false;
                    break;
                }
            }

            for (int i=0; i<expectedTableBody.size(); i++) {
                List<String> rowData = expectedTableBody.get(i);
                for (int j=0; j<rowData.size(); j++) {
                    String expectedBodyValue = rowData.get(j);
                    int row = i+1;
                    int column = j+1;
                    String actualBodyValue = driver.findElement(By.xpath("//table/tbody/tr["+ row +"]/td["+ column +"]")).getText();
                    if (!expectedBodyValue.equals(actualBodyValue)) {
                        status = false;
                        break;
                    }
                }
            }
            return status;
        } catch (Exception e) {
            System.out.println("Error while validating chart contents");
            return false;
        }
    }

    /**
     * Return Boolean based on if the Size drop down exists
     */
    public Boolean verifyExistenceOfSizeDropdown(WebDriver driver) {
        Boolean status = false;
        try {
            // If the size dropdown exists and is displayed return true, else return false
            WebElement sizeDropDown = driver.findElement(By.xpath("//select[@name='age']"));
            status = sizeDropDown.isDisplayed();
        } catch (Exception e) {}
        return status;
    }
}
