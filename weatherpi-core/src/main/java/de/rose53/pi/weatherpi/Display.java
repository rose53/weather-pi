package de.rose53.pi.weatherpi;

import java.io.IOException;
import java.util.List;

import de.rose53.pi.weatherpi.display.EBase;
import de.rose53.pi.weatherpi.display.EColon;
import de.rose53.pi.weatherpi.display.EDisplay;

public interface Display {

	void on();
	void off();

	void clear();
	void drawColon(List<EColon> colonList);
	void writeDigitNum(EDisplay display, int num);
	void print(double n, int digits);
	void print(int n, EBase base);
    void writeDisplay() throws IOException;
}
