package config;

import org.aeonbits.owner.Config;

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({
        "system:properties",
        "classpath:credentials.properties"
})
public interface BrowserstackConfig extends Config {

    @Key("browserstack.userName")
    String userName();

    @Key("browserstack.accessKey")
    String accessKey();

    @Key("browserstack.hub")
    @DefaultValue("https://hub.browserstack.com/wd/hub")
    String hub();

    @Key("browserstack.appiumVersion")
    String appiumVersion();

    @Key("platform")
    @DefaultValue("android")
    String platform();

    @Key("${platform}.platformName")
    String platformName();

    @Key("${platform}.automationName")
    String automationName();

    @Key("${platform}.app")
    String app();

    @Key("phone")
    @DefaultValue("pixel")
    String phone();

    @Key("device.${phone}.name")
    String deviceName();

    @Key("device.${phone}.osVersion")
    String osVersion();

    @Key("browserstack.projectName")
    @DefaultValue("First Java Project")
    String projectName();

    @Key("browserstack.buildName")
    @DefaultValue("browserstack-build-1")
    String buildName();

    @Key("browserstack.sessionName")
    @DefaultValue("first_test")
    String sessionName();
}
