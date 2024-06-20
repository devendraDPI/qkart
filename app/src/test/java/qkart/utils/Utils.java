package qkart.utils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Utils {
    public static String getDateTime(String formatPattern) {
        LocalDateTime now = LocalDateTime.now();
        return now.format(DateTimeFormatter.ofPattern(formatPattern));
    }

    public static void logStatus(String testCaseID, String testStep, String testMessage) {
        System.out.println(String.format("%s | %s | %s | %s", getDateTime("yyyy-MM-dd HH:mm:ss"), testCaseID, testStep, testMessage));
    }

    public static void logStatus(String testStep, String testMessage) {
        System.out.println(String.format("%s | %s | %s", getDateTime("yyyy-MM-dd HH:mm:ss"), testStep, testMessage));
    }

    public static void takeScreenshot(WebDriver driver, String screenshotType, String description) {
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
}
