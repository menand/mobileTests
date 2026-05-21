package drivers;

import config.BrowserstackConfig;
import org.openqa.selenium.MutableCapabilities;

import java.util.HashMap;
import java.util.Map;

final class CapabilitiesBuilder {

    private CapabilitiesBuilder() {}

    static MutableCapabilities build(BrowserstackConfig config) {
        MutableCapabilities caps = new MutableCapabilities();

        caps.setCapability("platformName", config.platformName());
        caps.setCapability("appium:automationName", config.automationName());
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

        return caps;
    }
}
