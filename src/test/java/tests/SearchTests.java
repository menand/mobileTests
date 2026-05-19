package tests;

import helpers.Attach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;
import static io.appium.java_client.AppiumBy.*;
import static io.qameta.allure.Allure.step;

class SearchTests extends TestBase {

    @Test
    void successfulSearchTest() {
        step("Type search", () -> {
            $(accessibilityId("Search Wikipedia")).click();
            $(id("org.wikipedia.alpha:id/search_src_text")).sendKeys("Appium");
            Attach.screenshotAs("Type search");
        });
        step("Verify content found", () -> {
            $$(id("org.wikipedia.alpha:id/page_list_item_title"))
                    .shouldHave(sizeGreaterThan(0));
            Attach.screenshotAs("Verify content found");
        });
    }

    @Test
    void searchJavaReturnsIslandAndLanguage() {
        step("Type 'Java' in search", () -> {
            $(accessibilityId("Search Wikipedia")).click();
            $(id("org.wikipedia.alpha:id/search_src_text")).sendKeys("Java");
            Attach.screenshotAs("Type 'Java' in search");
        });
        step("Results contain the island in Indonesia", () -> {
            $(byXpath("//*[contains(@text,'Indonesia')]")).shouldBe(visible);
            Attach.screenshotAs("Results contain the island in Indonesia");
        });
        step("Results contain the programming language", () -> {
            $(byXpath("//*[contains(@text,'rogramming language')]")).shouldBe(visible);
            Attach.screenshotAs("Results contain the programming language");
        });
    }
}
