package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static io.appium.java_client.AppiumBy.accessibilityId;
import static io.appium.java_client.AppiumBy.id;

public class AndroidSearchPage implements SearchPage {

    @Override
    public void openSearchAndType(String query) {
        $(accessibilityId("Search Wikipedia")).click();
        $(id("org.wikipedia.alpha:id/search_src_text")).sendKeys(query);
    }

    @Override
    public ElementsCollection results() {
        return $$(id("org.wikipedia.alpha:id/page_list_item_title"));
    }

    @Override
    public SelenideElement resultContaining(String fragment) {
        return $(byXpath("//*[contains(@text,'" + fragment + "')]"));
    }
}
