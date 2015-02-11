package com.autonomy.abc.config;

import com.autonomy.abc.selenium.menubar.SideNavBar;
import com.autonomy.abc.selenium.menubar.TopNavBar;
import com.autonomy.abc.selenium.page.AppBody;
import com.autonomy.abc.selenium.util.ImplicitWaits;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.*;


@Ignore
@RunWith(Parameterized.class)
public abstract class ABCTestBase {
	private static final Logger LOGGER = LoggerFactory.getLogger(ABCTestBase.class);
	private final static Set<String> USER_BROWSERS;

	public final TestConfig config;
	private final String browser;
	private final Platform platform;
	private WebDriver driver;
	public AppBody body;
	protected SideNavBar navBar;
	protected TopNavBar topNavBar;

	static {
		final String[] allBrowsers = {"firefox", "internet explorer", "chrome"};
		final String browserProperty = System.getProperty("com.autonomy.browsers");

		if (browserProperty == null) {
			USER_BROWSERS = new HashSet<>(Arrays.asList(allBrowsers));
		} else {
			USER_BROWSERS = new HashSet<>();

			for (final String browser : allBrowsers) {
				if (browserProperty.contains(browser)) {
					USER_BROWSERS.add(browser);
				}
			}
		}
	}

	public ABCTestBase(final TestConfig config, final String browser, final Platform platform) {
		this.config = config;
		this.browser = browser;
		this.platform = platform;
	}

	@Parameterized.Parameters
	public static Iterable<Object[]> parameters() throws MalformedURLException {
		final List<Object[]> parameters = new ArrayList<>();


		for (final String browser : USER_BROWSERS) {
			parameters.add(new Object[]{
					new TestConfig(parameters.size()),
					browser,
					Platform.WINDOWS
			});
		}

		return parameters;
	}

	@Before
	public void baseSetUp() throws MalformedURLException {
		LOGGER.info("parameter-set: [" + config.getIndex() + "]; browser: " + browser + "; platform: " + platform);
		driver = config.createWebDriver(browser, platform);
		ImplicitWaits.setImplicitWait(driver);
		driver.get(config.getWebappUrl());
		getDriver().manage().window().maximize();
		abcLogin("richard", "q");
		body = new AppBody(driver);
		navBar = new SideNavBar(driver);
		topNavBar = new TopNavBar(driver);
	}

	@After
	public void baseTearDown() {
		driver.quit();
	}

	public WebDriver getDriver() {
		return driver;
	}

	public TestConfig getConfig() {
		return config;
	}

	public void abcLogin(final String userName, final String password) {
		driver.findElement(By.cssSelector("[name='username']")).clear();
		driver.findElement(By.cssSelector("[name='username']")).sendKeys(userName);
		driver.findElement(By.cssSelector("[name='password']")).clear();
		driver.findElement(By.cssSelector("[name='password']")).sendKeys(password);
		driver.findElement(By.cssSelector("[type='submit']")).click();
		new WebDriverWait(driver, 15).until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".navbar-static-top-blue")));
	}
}

