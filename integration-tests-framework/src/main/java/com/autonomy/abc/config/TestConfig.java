package com.autonomy.abc.config;

import com.autonomy.abc.selenium.application.Application;
import com.autonomy.abc.selenium.application.ApplicationType;
import com.autonomy.abc.selenium.control.Resolution;
import com.autonomy.abc.selenium.control.Session;
import com.autonomy.abc.selenium.control.Window;
import com.autonomy.abc.selenium.users.AuthenticationStrategy;
import com.autonomy.abc.selenium.users.NewUser;
import com.autonomy.abc.selenium.users.User;
import com.autonomy.abc.selenium.util.Factory;
import com.autonomy.abc.selenium.util.ParametrizedFactory;
import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

public class TestConfig {
	private final static int DEFAULT_TIMEOUT = 10;

	private final JsonConfig jsonConfig;
	private final int index;
	private final ApplicationType type;
	private final URL url;
	private final Platform platform;
	private final Browser browser;

	TestConfig(final int index, final JsonConfig config) {
		this.jsonConfig = config;
		this.index = index;
		this.type = jsonConfig.getAppType();
		this.url = jsonConfig.getHubUrl();
		this.platform = Platform.WINDOWS;
		this.browser = jsonConfig.getBrowsers().get(index);
	}

	public String getAppUrl(Application<?> application) {
		return jsonConfig.getAppUrl(application.getName().toLowerCase()).toString();
	}

	// TODO: HodApiApplication
	public String getApiUrl() {
		return jsonConfig.getAppUrl("api").toString();
	}

	public User getDefaultUser() {
		return getUser("default");
	}

	public User getUser(String name){
		User user = jsonConfig.getUser(name);
		if (user == null) {
			throw new IllegalStateException("User with name " + name + " not found in config file");
		}
		return user;
	}

	public NewUser getNewUser(String name) {
		NewUser newUser = jsonConfig.getNewUser(name);
		if (newUser == null) {
			throw new IllegalStateException("NewUser with name " + name + " not found in config file");
		}
		return newUser;
	}

	public Browser getBrowser() {
		return browser;
	}

	public Platform getPlatform() {
		return platform;
	}

	public int getIndex() {
		return index;
	}

	public ApplicationType getType() {
		return type;
	}

	URL getHubUrl() {
		return url;
	}

	public Factory<WebDriver> getWebDriverFactory() {
		return new WebDriverFactory(this);
	}

	public ParametrizedFactory<Session, Window> getWindowFactory() {
		return new WindowFactory(this);
	}

	public NewUser generateNewUser() {
		return jsonConfig.generateRandomNewUser();
	}

	public AuthenticationStrategy getAuthenticationStrategy() {
		return jsonConfig.getAuthenticationStrategy(getWebDriverFactory());
	}

	public static List<Object[]> readConfigs(final Collection<ApplicationType> applicationTypes) throws IOException {
		return new TestConfigLoader().readConfigs(applicationTypes);
	}

	public static JsonNode getRawBaseConfig() throws IOException {
		return new TestConfigLoader().getRawBaseConfig();
	}

	@Override
	public String toString() {
		return "parameter-set: [" + getIndex() + "]; browser: " + getBrowser() + "; platform: " + getPlatform() + "; effective config: " + jsonConfig;
	}

	Resolution getResolution() {
		Resolution fromConfig = jsonConfig.getResolution();
		return fromConfig == null ? Resolution.MAXIMIZED : fromConfig;
	}

	public int getTimeout() {
		int timeout = jsonConfig.getTimeout();
		if (timeout >= 0) {
			return timeout;
		}
		return DEFAULT_TIMEOUT;
	}
}
