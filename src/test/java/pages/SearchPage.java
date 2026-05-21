package pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

public interface SearchPage {

    void openSearchAndType(String query);

    ElementsCollection results();

    SelenideElement resultContaining(String fragment);
}
