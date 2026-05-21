package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.appium.java_client.AppiumBy.accessibilityId;

public class IosSearchPage implements SearchPage {

    @Override
    public void openSearchAndType(String query) {
        $(accessibilityId("Search Wikipedia")).click();
        $(byXpath("//XCUIElementTypeSearchField")).sendKeys(query);
    }

    @Override
    public ElementsCollection results() {
        return $$(byXpath("//XCUIElementTypeCell"));
    }

    @Override
    public SelenideElement resultContaining(String fragment) {
        return $(byXpath(
                "//*[contains(@label,'" + fragment + "')"
                        + " or contains(@value,'" + fragment + "')"
                        + " or contains(@name,'" + fragment + "')]"));
    }
}
