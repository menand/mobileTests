package drivers;

import com.codeborne.selenide.WebDriverProvider;
import config.BrowserstackConfig;
import io.appium.java_client.android.AndroidDriver;
import org.aeonbits.owner.ConfigFactory;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BrowserstackDriver implements WebDriverProvider {

    private static final BrowserstackConfig config = ConfigFactory.create(BrowserstackConfig.class);

    @Override
    public WebDriver createDriver(Capabilities capabilities) {
        MutableCapabilities caps = new MutableCapabilities();

        caps.setCapability("platformName", "android");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:app", config.app());

        Map<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("userName", config.userName());
        bstackOptions.put("accessKey", config.accessKey());
        bstackOptions.put("deviceName", config.deviceName());
        bstackOptions.put("osVersion", config.osVersion());
        bstackOptions.put("appiumVersion", config.appiumVersion());
        bstackOptions.put("projectName", config.projectName());
        bstackOptions.put("buildName", config.buildName());
        bstackOptions.put("sessionName", config.sessionName());
        caps.setCapability("bstack:options", bstackOptions);

        try {
            return new AndroidDriver(new URL(config.hub()), caps);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
