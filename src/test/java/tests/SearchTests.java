package tests;

import helpers.Attach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.PageFactory;
import pages.SearchPage;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static io.qameta.allure.Allure.step;

@Tag("android")
class SearchTests extends TestBase {

    private final SearchPage search = PageFactory.searchPage();

    @Test
    void successfulSearchTest() {
        step("Type search", () -> {
            search.openSearchAndType("Appium");
            Attach.screenshotAs("Type search");
        });
        step("Verify content found", () -> {
            search.results().shouldHave(sizeGreaterThan(0));
            Attach.screenshotAs("Verify content found");
        });
    }

    @Test
    void searchJavaReturnsIslandAndLanguage() {
        step("Type 'Java' in search", () -> {
            search.openSearchAndType("Java");
            Attach.screenshotAs("Type 'Java' in search");
        });
        step("Results contain the island in Indonesia", () -> {
            search.resultContaining("Indonesia").shouldBe(visible);
            Attach.screenshotAs("Results contain the island in Indonesia");
        });
        step("Results contain the programming language", () -> {
            search.resultContaining("rogramming language").shouldBe(visible);
            Attach.screenshotAs("Results contain the programming language");
        });
    }
}
