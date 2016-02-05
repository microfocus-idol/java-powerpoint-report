package com.autonomy.abc.selenium.menu;

import com.autonomy.abc.selenium.util.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OPTopNavBar extends TopNavBar {
    public OPTopNavBar(WebDriver driver) {
        super(driver);
    }

    @Override
    public void logOut() {
        clickCog();
        new WebDriverWait(getDriver(),5).until(ExpectedConditions.visibilityOfElementLocated(By.className("navigation-logout"))).click();
    }

    private void clickCog(){
        findElement(By.className("hp-settings")).click();
        Waits.loadOrFadeWait();
    }

    private void clickDropdown(String page){
       findElement(By.xpath(".//a[contains(.,'"+page+"')]")).click();
    }

    public void goToAboutPage(){
        clickCog();
        clickDropdown("About");
    }

    public void goToSettingsPage(){
        clickCog();
        clickDropdown("Settings");
    }

    public void goToUsersPage(){
        clickCog();
        clickDropdown("Users");
    }

    public void switchPage(TabId tab) {
        clickCog();
        findElement(tab.locator).click();
    }

    public enum TabId {
        ABOUT("About"),
        SETTINGS("Settings"),
        USERS("Users");

        private final By locator;

        TabId(String linkText) {
            locator = By.linkText(linkText);
        }
    }
}
