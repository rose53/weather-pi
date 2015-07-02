package de.rose53.pi.weatherpi;

import java.io.IOException;

import de.rose53.pi.weatherpi.display.EBase;
import de.rose53.pi.weatherpi.display.EDisplay;

public interface Display {

	void clear();
	void drawColon(boolean state);
	void writeDigitNum(EDisplay display, int num);
	void print(int n, EBase base);
    void writeDisplay() throws IOException;
}
