package pages;

import com.codeborne.selenide.SelenideElement;

public interface SampleAppPage {

    void tapAlertButton();

    SelenideElement alertDialog();

    void dismissAlert();

    void openTab(String tabName);

    SelenideElement navigationBarTitled(String title);

    SelenideElement webView();
}
