package com.autonomy.abc.topnavbar.notifications;

import com.autonomy.abc.config.TestConfig;
import com.autonomy.abc.framework.KnownBug;
import com.autonomy.abc.framework.RelatedTo;
import com.autonomy.abc.selenium.application.HSOApplication;
import com.autonomy.abc.selenium.connections.ConnectionService;
import com.autonomy.abc.selenium.connections.WebConnector;
import com.autonomy.abc.selenium.control.Session;
import com.autonomy.abc.selenium.indexes.Index;
import com.autonomy.abc.selenium.indexes.IndexService;
import com.autonomy.abc.selenium.keywords.KeywordFilter;
import com.autonomy.abc.selenium.keywords.KeywordService;
import com.autonomy.abc.selenium.menu.NavBarTabId;
import com.autonomy.abc.selenium.menu.Notification;
import com.autonomy.abc.selenium.page.HSOElementFactory;
import com.autonomy.abc.selenium.page.admin.HSODevelopersPage;
import com.autonomy.abc.selenium.page.admin.UsersPage;
import com.autonomy.abc.selenium.page.analytics.AnalyticsPage;
import com.autonomy.abc.selenium.page.connections.ConnectionsPage;
import com.autonomy.abc.selenium.page.keywords.KeywordsPage;
import com.autonomy.abc.selenium.page.login.GoogleAuth;
import com.autonomy.abc.selenium.page.promotions.PromotionsPage;
import com.autonomy.abc.selenium.promotions.HSOPromotionService;
import com.autonomy.abc.selenium.promotions.StaticPromotion;
import com.autonomy.abc.selenium.users.*;
import org.junit.Test;
import org.openqa.selenium.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static com.autonomy.abc.framework.ABCAssert.assertThat;
import static com.autonomy.abc.framework.ABCAssert.verifyThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;

public class NotificationsDropDownHostedITCase extends NotificationsDropDownTestBase {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public NotificationsDropDownHostedITCase(final TestConfig config) {
        super(config);
        setInitialUser(config.getUser("index_tests"));
    }

    @Override
    public HSOElementFactory getElementFactory() {
        return (HSOElementFactory) super.getElementFactory();
    }

    @Test
    public void testStaticPromotionNotifications(){
        HSOPromotionService ps = (HSOPromotionService) getApplication().promotionService();

        String docTitle = "TITLE";
        String docContent = "CONTENT";
        String promotionTrigger = "sadness";
        String promotionNotificationText = "Created a new static promotion: Static Promotion for: " + promotionTrigger;

        ps.setUpStaticPromotion(new StaticPromotion(docTitle, docContent, promotionTrigger));
        try {
            getElementFactory().getSearchPage();
            checkForNotification(promotionNotificationText);
        } finally {
            ps.deleteAll();
        }
    }

    @Test
    public void testRemovingStaticPromotionNotifications(){
        HSOPromotionService ps = (HSOPromotionService) getApplication().promotionService();

        String docTitle = "TITLE";
        String docContent = "CONTENT";
        String promotionTrigger = "sadness";
        String promotionNotificationText = "Removed a static promotion";

        StaticPromotion staticPromotion = new StaticPromotion(docTitle, docContent, promotionTrigger);

        ps.setUpStaticPromotion(staticPromotion);
        ps.delete(staticPromotion);

        checkForNotification(promotionNotificationText);
    }

    @Test
    public void testCreateIndexNotifications() {
        Index index = new Index("danye west");
        IndexService indexService = ((HSOApplication) getApplication()).indexService();

        try {
            indexService.setUpIndex(index);
            checkForNotificationNoWait("Created a new index: " + index.getName());
        } finally {
            indexService.deleteIndex(index);
        }
    }

    @Test
    public void testConnectorsCreationNotifications(){
        String connectorName = "lc";

        String creatingNotification = "Creating a new connection: " + connectorName;
        String createdNotification = "Created a new connection: " + connectorName;
        String startedNotification = "Connection " + connectorName + " started";
        String finishedNotification = "Connection "+ connectorName + " has finished running";

        WebConnector connector = new WebConnector("http://loscampesinos.com/", connectorName).withDuration(60);

        ConnectionService cs = ((HSOApplication) getApplication()).connectionService();
        try {
            cs.setUpConnection(connector); //Notifications are dealt with within here, so need to wait for them

            getElementFactory().getConnectionsPage();

            getElementFactory().getTopNavBar().notificationsDropdown();
            notifications = getElementFactory().getTopNavBar().getNotifications();

            assertThat(notifications.notificationNumber(1).getText(), is(finishedNotification));
            assertThat(notifications.notificationNumber(2).getText(), is(startedNotification));
            assertThat(notifications.notificationNumber(3).getText(), is(createdNotification));
            assertThat(notifications.notificationNumber(4).getText(), is(creatingNotification));
        } finally {
            cs.deleteConnection(connector, true);
        }
    }

    @Test
    @KnownBug("CSA-2043")
    public void testConnectorsDeletionNotifications() {
        String connectorName = "deathcabyoucutie";
        WebConnector connector = new WebConnector("http://deathcabforcutie.com/", connectorName).withDuration(60);

        String deletingNotification = "Deleting connection " + connectorName;
        String successfulNotification = "Connection " + connectorName + " successfully removed";

        ConnectionService cs = ((HSOApplication) getApplication()).connectionService();
        cs.setUpConnection(connector);

        cs.deleteConnection(connector, true);        //Because of the WebDriverWait within no need to wait for the notifications

        getElementFactory().getTopNavBar().notificationsDropdown();
        notifications = getElementFactory().getTopNavBar().getNotifications();

        assertThat(notifications.notificationNumber(1).getText(), is(successfulNotification));
        assertThat(notifications.notificationNumber(2).getText(), is(deletingNotification));
    }

    // TODO: this is a mess
    @Test
    @KnownBug({"CSA-1698", "CSA-1687"})
    public void testUsernameShowsInNotifications() throws Exception {
        getElementFactory().getSideNavBar().switchPage(NavBarTabId.DEVELOPERS);

        HSODevelopersPage hsoDevelopersPage = getElementFactory().getDevsPage();
        User dev = new User(null, hsoDevelopersPage.getUsernames().get(0));

        String devUsername = "Brendon Urie";

        hsoDevelopersPage.editUsernameLink(dev).click();
        hsoDevelopersPage.editUsernameInput(dev).setAndSubmit(devUsername);

        KeywordService keywordService = getApplication().keywordService();
        UserService userService = getApplication().userService();
        Session session = null;

        SignupEmailHandler emailHandler = new GmailSignupEmailHandler((GoogleAuth) config.getUser("google").getAuthProvider());

        try {
            keywordService.addSynonymGroup("My", "Good", "Friend", "Jeff");

            getElementFactory().getTopNavBar().notificationsDropdown();
            for (Notification notification : getElementFactory().getTopNavBar().getNotifications().getAllNotifications()) {
                verifyThat(notification.getUsername(), is(devUsername));
            }

            User user = userService.createNewUser(config.getNewUser("drake"), Role.ADMIN);

            try {
                user.authenticate(config.getWebDriverFactory(), emailHandler);
            } catch (TimeoutException e) {
                /* User has likely already been authenticated recently, attempt to continue */
            }

            session = getSessionRegistry().startSession(config.getWebappUrl());
            HSOElementFactory secondFactory = new HSOApplication().inWindow(session.getActiveWindow()).elementFactory();

            loginTo(secondFactory.getLoginPage(), session.getDriver(), user);

            secondFactory.getPromotionsPage();

            keywordService.addSynonymGroup("Messi", "Campbell");

            verifyThat(getElementFactory().getTopNavBar().getNotifications().getNotification(1).getUsername(), is(user.getUsername()));
            secondFactory.getTopNavBar().notificationsDropdown();
            verifyThat(secondFactory.getTopNavBar().getNotifications().getNotification(1).getUsername(), is(user.getUsername()));

            keywordService.addSynonymGroup("Joel", "Lionel");

            verifyThat(getElementFactory().getTopNavBar().getNotifications().getNotification(1).getUsername(), is(devUsername));
            verifyThat(secondFactory.getTopNavBar().getNotifications().getNotification(1).getUsername(), is(devUsername));

        } finally {
            if (session != null) {
                getSessionRegistry().endSession(session);
            }

            userService.deleteOtherUsers();
            keywordService.deleteAll(KeywordFilter.ALL);

            emailHandler.markAllEmailAsRead(getDriver());
        }
    }

    @Test
    @RelatedTo("CSA-1586")
    @KnownBug("CSA-1542")
    public void testNotificationsPersistOverPages(){
        KeywordService keywordService = getApplication().keywordService();
        keywordService.deleteAll(KeywordFilter.ALL);

        keywordService.addSynonymGroup("Pop", "Punk");
        keywordService.addBlacklistTerms("Shola", "Ameobi");
        keywordService.deleteAll(KeywordFilter.ALL);

        getElementFactory().getTopNavBar().notificationsDropdown();
        List<Notification> notifications = getElementFactory().getTopNavBar().getNotifications().getAllNotifications();

        for (Class<?> page : Arrays.asList(
                PromotionsPage.class,
                AnalyticsPage.class,
                KeywordsPage.class,
                ConnectionsPage.class,
                UsersPage.class
        )) {
            navigateAndVerifyNotifications(page, notifications);
        }
    }

    private void navigateAndVerifyNotifications(Class<?> page, List<Notification> notifications) {
        getElementFactory().switchTo(page);
        logger.info("on page " + page);

        getElementFactory().getTopNavBar().openNotifications();

        verifyThat(getElementFactory().getTopNavBar().getNotifications().getAllNotifications(), contains(notifications.toArray()));
    }
}
