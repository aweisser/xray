package de.aw.xray.example.webapp.controller;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.agent.XrayServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author armin.weisser
 */
@RestController
@RequestMapping("/hello")
public class MainController {

    @Autowired
    XrayServer xrayServer;

    @PostConstruct
    public void initXrayServer() throws Exception {
        xrayServer.start();
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = "text/html")
    public String sayHello(@PathVariable String name) {
        startBusinessProcessFor(name);
        return "<div name='result'>Hello " + name +". Thanks for your message!</div>";
    }

    private void startBusinessProcessFor(String name) {

        xrayServer.pushEvent(new XrayEvent("business process event", "Starting business process for " + name));

        // TODO do business things

        xrayServer.pushEvent(new XrayEvent("business process event", "Finished business process for " + name));

    }

}
