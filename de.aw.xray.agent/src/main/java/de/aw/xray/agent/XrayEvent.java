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

    @Override
    public boolean equals(Object obj) {
        XrayEvent that = (XrayEvent) obj;
        return that != null && this.time == that.time && this.type == that.type && this.result.equals(that.result);
    }

    @Override
    public int hashCode() {
        return (type + time + String.valueOf(result)).hashCode();
    }
}
