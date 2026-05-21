package pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static io.appium.java_client.AppiumBy.accessibilityId;

public class IosSampleAppPage implements SampleAppPage {

    @Override
    public void tapAlertButton() {
        $(accessibilityId("Alert")).click();
    }

    @Override
    public SelenideElement alertDialog() {
        return $(byXpath("//XCUIElementTypeAlert"));
    }

    @Override
    public void dismissAlert() {
        $(byXpath("//XCUIElementTypeAlert//XCUIElementTypeButton")).click();
    }

    @Override
    public void openTab(String tabName) {
        $(byXpath("//XCUIElementTypeTabBar//XCUIElementTypeButton[@name='" + tabName + "']")).click();
    }

    @Override
    public SelenideElement navigationBarTitled(String title) {
        return $(byXpath("//XCUIElementTypeNavigationBar[@name='" + title + "']"));
    }

    @Override
    public SelenideElement webView() {
        return $(byXpath("(//XCUIElementTypeWebView)[1]"));
    }
}
