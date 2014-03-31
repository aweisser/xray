package de.aw.xray.example.webapp.controller;

import de.aw.xray.agent.XrayEvent;
import de.aw.xray.agent.XrayServer;
import de.aw.xray.agent.socket.SocketBasedXrayServerImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author armin.weisser
 */
@RestController
@RequestMapping("/hello")
public class MainController implements InitializingBean {

    @Autowired
    XrayServer xrayServer;

    @Override
    public void afterPropertiesSet() throws Exception {
        xrayServer.start();
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET, produces = "text/html")
    public String sayHello(@PathVariable String name) {
        startBusinessProcessFor(name);
        return "<div name='result'>Hello " + name +". Thanks for your message!</div>";
    }

    private void startBusinessProcessFor(String name) {

        pushXrayEvent("business process event", "Starting business process for " + name);

        // TODO do business things

        pushXrayEvent("business process event", "Finished business process for " + name);

    }

    private void pushXrayEvent(String eventType, String eventMessage) {
        try {
            xrayServer.pushEvent(new XrayEvent(eventType, eventMessage));
        } catch (IOException e) {
            e.printStackTrace();  //TODO handle exception!!!
        }
    }
}
