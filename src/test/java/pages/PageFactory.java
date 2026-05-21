package pages;

import config.BrowserstackConfig;
import org.aeonbits.owner.ConfigFactory;

public final class PageFactory {

    private static final BrowserstackConfig config = ConfigFactory.create(BrowserstackConfig.class);

    private PageFactory() {}

    public static SearchPage searchPage() {
        return "ios".equalsIgnoreCase(config.platform())
                ? new IosSearchPage()
                : new AndroidSearchPage();
    }

    public static SampleAppPage sampleAppPage() {
        if ("ios".equalsIgnoreCase(config.platform())) {
            return new IosSampleAppPage();
        }
        throw new UnsupportedOperationException(
                "BrowserStack-SampleApp.ipa scenarios are iOS-only; current platform="
                        + config.platform());
    }
}
