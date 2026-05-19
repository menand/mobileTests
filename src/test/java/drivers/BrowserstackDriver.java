package drivers;

import com.codeborne.selenide.WebDriverProvider;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BrowserstackDriver implements WebDriverProvider {
    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        MutableCapabilities caps = new MutableCapabilities();

        caps.setCapability("platformName", "android");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:app", "bs://4a3f9b032b4910480973a5f5bae5456338d2b243");

        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("userName", "andreymenshov_0O13yT");
        bstackOptions.put("accessKey", "huwjXPTFoPfFsBLk7wHG");
        bstackOptions.put("deviceName", "Google Pixel 7");
        bstackOptions.put("osVersion", "13.0");
        bstackOptions.put("appiumVersion", "2.6.0");
        bstackOptions.put("projectName", "First Java Project");
        bstackOptions.put("buildName", "browserstack-build-1");
        bstackOptions.put("sessionName", "first_test");
        caps.setCapability("bstack:options", bstackOptions);

        try {
            return new AndroidDriver(
                    new URL("https://hub.browserstack.com/wd/hub"), caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
