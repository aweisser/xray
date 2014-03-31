package de.aw.xray.example.test;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.client.XrayEventListener;
import de.aw.xray.client.XrayImpl;
import de.aw.xray.client.Xray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;


/**
 * @author armin.weisser
 */
public class XrayTest {

    private static final String DEV_URL = "http://localhost:8080/xray-example";
    private static final String CHROME_DRIVER = "D:\\server\\chromedriver.exe";

    private String baseUrl = DEV_URL;

    private WebDriver driver;
    private Xray xray;


    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER);
        driver = new ChromeDriver(); // http://www.qaautomation.net/?p=45
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        xray = new XrayImpl("localhost", 4711);
    }

    @Test
    public void registration_GoodCase() throws Exception {
        // register server side event expectations.
        registerServerExpectation("business process event", "Starting business process for Ray");
        registerServerExpectation("business process event", "Finished business process for Ray");
        // registerServerExpectation(".*INFO: Mail sent to x.ray@xray.org.*", "Server-Log");

        // start lisitening for xray events
        xray.connect(); // non-blocking

        // test the AUT with selenium for example
        driver.get(baseUrl + "/hello/Ray");

        /*
        driver.findElement(By.name("name")).sendKeys("Ray X.");
        driver.findElement(By.name("email")).sendKeys("x.ray@xray.org");
        driver.findElement(By.name("register")).click();
        */

        // verify the result on the client.
        assertEquals("Hello Ray. Thanks for your message!", driver.findElement(By.name("result")).getText());

        //xray.verify(); // blocking. Wait for all expactations to come back or run into timeout

    }

    @After
    public void disconnectXrayAgent() throws IOException {
        xray.disconnect();
    }


    private void registerServerExpectation(final String eventType, final Object expectedResult) {
        assertNotNull(expectedResult);
        assertNotNull(eventType);

        xray.register(new XrayEventListener() {

            boolean expectedResultFound = false;

            @Override
            public String getEventType() {
                return eventType;
            }

            @Override
            public long getTimeout() {
                return 10000;
            }

            @Override
            public void handleEvent(XrayEvent xrayEvent) {
                if(expectedResult.equals(xrayEvent.getResult())) {
                    expectedResultFound = true;
                }
            }

            @Override
            public void verify() {
                assertTrue(expectedResultFound);
            }
        });

    }


}
