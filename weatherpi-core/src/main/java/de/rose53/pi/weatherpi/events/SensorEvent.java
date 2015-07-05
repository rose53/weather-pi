package de.rose53.pi.weatherpi.events;

public class SensorEvent {

	private String sensor;

	public SensorEvent() {
	}

	public SensorEvent(String sensor) {
		this.sensor = sensor;
	}

	public String getSensor() {
		return sensor;
	}
}
