package de.aw.xray.example.test;

import de.aw.xray.client.impl.XrayImpl;
import de.aw.xray.client.Xray;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static java.util.concurrent.TimeUnit.*;


/**
 * @author armin.weisser
 */
public class XrayTest {

    private static final String DEV_URL = "http://localhost:8080/xray-example";
    private static final String CHROME_DRIVER = new File("de.aw.xray.example.test/src/main/resources/chromedriver.exe").getAbsolutePath();

    private String baseUrl = DEV_URL;

    private WebDriver driver;
    private Xray xray;


    @Before
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_DRIVER);
        driver = new ChromeDriver(); // http://www.qaautomation.net/?p=45
        driver.manage().timeouts().implicitlyWait(30, SECONDS);
        xray = new XrayImpl("localhost", 4711);
    }

    @Test
    public void registration_GoodCase() throws Exception {
        // register server side event expectations.
        xray.lookFor("business process event").andExpect("Starting business process for Ray").within(3, SECONDS);
        xray.lookFor("business process event").andExpect("Finished business process for Ray").within(3, SECONDS);

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

        xray.verify(); // blocking. Wait for all expactations to come back or run into timeout

    }

    @After
    public void disconnectXrayAgent() throws IOException {
        xray.disconnect();
    }

}
