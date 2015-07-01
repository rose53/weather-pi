package de.rose53.pi.weatherpi;

import java.io.IOException;

import de.rose53.pi.weatherpi.display.EBase;

public interface Display {

	void print(int n, EBase base);
    void writeDisplay() throws IOException;
}
