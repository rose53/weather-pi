package de.rose53.pi.weatherpi.events;

import javax.json.JsonObjectBuilder;

import de.rose53.pi.weatherpi.common.ESensorPlace;
import de.rose53.pi.weatherpi.common.ESensorType;

public class WinddirectionEvent extends SensorEvent {

    private double winddirection;

    public WinddirectionEvent() {
        super(ESensorType.WINDDIRECTION);
    }

    public WinddirectionEvent(ESensorPlace place, String sensor, double winddirection) {
        super(ESensorType.WINDDIRECTION,place,sensor);
        this.winddirection = winddirection;
    }

    public double getWinddirection() {
        return winddirection;
    }

    @Override
    public double getValue() {
        return getWinddirection();
    }

    @Override
    protected void addtoJsonObject(JsonObjectBuilder objectBuilder) {
        super.addtoJsonObject(objectBuilder);
        objectBuilder.add("winddirection", getWinddirection());
    }
}
