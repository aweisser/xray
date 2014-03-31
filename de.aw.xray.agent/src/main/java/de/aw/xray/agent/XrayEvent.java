package de.aw.xray.agent;

import java.io.Serializable;

/**
 * @author armin.weisser
 */
public class XrayEvent implements Serializable {
    final String type;
    final Serializable result;
    final Long time;

    public XrayEvent(String type, Serializable result) {
        this.type = type;
        this.result = result;
        this.time = System.currentTimeMillis();
    }

    public String getType() {
        return type;
    }

    public Serializable getResult() {
        return result;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "[" + type + ":" + result + "]@"+time;
    }
}
