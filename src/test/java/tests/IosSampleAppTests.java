package tests;

import helpers.Attach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.PageFactory;
import pages.SampleAppPage;

import static com.codeborne.selenide.Condition.disappear;
import static com.codeborne.selenide.Condition.visible;
import static io.qameta.allure.Allure.step;

@Tag("iphone")
class IosSampleAppTests extends TestBase {

    private final SampleAppPage app = PageFactory.sampleAppPage();

    @Test
    void alertButtonShowsAndDismissesAlert() {
        step("Tap 'Alert' on UI Elements screen", () -> {
            app.tapAlertButton();
            Attach.screenshotAs("Tapped Alert");
        });
        step("Native alert appears", () -> {
            app.alertDialog().shouldBe(visible);
            Attach.screenshotAs("Alert visible");
        });
        step("Alert is dismissed after tapping its button", () -> {
            app.dismissAlert();
            app.alertDialog().should(disappear);
            Attach.screenshotAs("Alert dismissed");
        });
    }

    @Test
    void webViewTabSwitchesScreen() {
        step("UI Elements screen is initial", () -> {
            app.navigationBarTitled("UI Elements").shouldBe(visible);
            Attach.screenshotAs("UI Elements visible");
        });
        step("Open 'Web View' tab", () -> {
            app.openTab("Web View");
            Attach.screenshotAs("Tapped Web View tab");
        });
        step("Web View screen is shown", () -> {
            app.webView().shouldBe(visible);
            app.navigationBarTitled("UI Elements").should(disappear);
            Attach.screenshotAs("Web View visible");
        });
    }
}
