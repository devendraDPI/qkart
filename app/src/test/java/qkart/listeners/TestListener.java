package qkart.listeners;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import qkart.Base;
import qkart.utils.Utils;


public class TestListener implements ITestListener {
    @Override
    public void onStart(ITestContext context) {
        Utils.logStatus("onStart", context.getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        Utils.logStatus("onFinish", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        Utils.logStatus("onTestStart", result.getName());
        Utils.takeScreenshot(Base.driver, "test_start", result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        Utils.logStatus("onTestSuccess", result.getName());
        Utils.takeScreenshot(Base.driver, "test_success", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        Utils.logStatus("onTestFailure", result.getName());
        Utils.takeScreenshot(Base.driver, "test_failure", result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        Utils.logStatus("onTestSkipped", result.getName());
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        Utils.logStatus("onTestFailedButWithinSuccessPercentage", result.getName());
    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        Utils.logStatus("onTestFailedWithTimeout", result.getName());
    }
}
