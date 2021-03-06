# Xray - the testing paradigm with super powers

Ttrying to hunt down a bug with end2end testing?

As a developer you'd probably do something like this:
* start your webapp
* set a breakpoint on the server side code
* tail -f the server log
* open the app in a browser window
* click your testcase
* watch client, server log and breakpoints to see what's going on.

## With Xray you can do any kind of end2end test and verify server side expactations at the same time.

```Java
    @org.junit.Test
    public void xrayRegistrationProcess() {
    
        // 1. register server side expectation.
        registerServerExpectation("Registration mail body", "Hello Ray, welcome!");
        registerServerExpectation("Server log", "Mail sent to x.ray@xray.org");
    
        // 2. start xray the AUT
        xray.connect();

        // 3. test the AUT with selenium for example
        selenium.findElement(By.name("name")).sendKeys("Ray X.");
        selenium.findElement(By.name("email")).sendKeys("x.ray@xray.org");
        selenium.findElement(By.name("register")).click();

        // 4. verify the result on the client.
        assertEquals("Ray, check your mail!", selenium.findElement(By.name("msg")).getText());

        // 5. verify the server side expectations
        xray.verify();
    }
```
