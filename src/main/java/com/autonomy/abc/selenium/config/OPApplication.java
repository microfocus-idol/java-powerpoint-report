package com.autonomy.abc.selenium.config;

import com.autonomy.abc.selenium.menu.SideNavBar;
import com.autonomy.abc.selenium.menu.TopNavBar;
import com.autonomy.abc.selenium.page.ElementFactory;
import com.autonomy.abc.selenium.page.OPAppBody;
import com.autonomy.abc.selenium.page.OPElementFactory;
import com.autonomy.abc.selenium.promotions.OPPromotionService;
import com.autonomy.abc.selenium.users.OPUserService;
import org.openqa.selenium.WebDriver;


public class OPApplication extends Application {
    @Override
    public OPAppBody createAppBody(WebDriver driver) {
        return new OPAppBody(driver);
    }

    @Override
    public OPAppBody createAppBody(WebDriver driver, TopNavBar topNavBar, SideNavBar sideNavBar) {
        return new OPAppBody(driver, topNavBar, sideNavBar);
    }


    @Override
    public OPElementFactory createElementFactory(WebDriver driver) {
        return new OPElementFactory(driver);
    }

    @Override
    public OPPromotionService createPromotionService(ElementFactory elementFactory) {
        return new OPPromotionService(this, elementFactory);
    }

    @Override
    public OPUserService createUserService(ElementFactory elementFactory) {
        return new OPUserService(this, elementFactory);
    }

    @Override
    public UserConfigParser getUserConfigParser() {
        return new OPUserConfigParser();
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.ON_PREM;
    }
}
