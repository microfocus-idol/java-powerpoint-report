package com.autonomy.abc.selenium.hsod;

import com.autonomy.abc.selenium.application.IsoApplication;
import com.autonomy.abc.selenium.promotions.HsodPromotionService;
import com.autonomy.abc.selenium.users.HsodDeveloperService;
import com.autonomy.abc.selenium.users.HsodUserService;
import com.hp.autonomy.frontend.selenium.control.Window;

public class IsoHsodApplication extends IsoApplication<IsoHsodElementFactory> {
    private IsoHsodElementFactory factory;

    @Override
    public IsoHsodApplication inWindow(final Window window) {
        this.factory = new IsoHsodElementFactory(window.getSession().getDriver());
        return this;
    }

    public IsoHsodElementFactory elementFactory() {
        return factory;
    }

    @Override
    public HsodPromotionService promotionService() {
        return new HsodPromotionService(this);
    }

    @Override
    public HsodUserService userService() {
        return new HsodUserService(this);
    }

    public HsodDeveloperService developerService() {
        return new HsodDeveloperService(this);
    }

    @Override
    public boolean isHosted() {
        return true;
    }
}
