package de.rose53.pi.weatherpi.componets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import de.rose53.pi.weatherpi.Display;
import de.rose53.pi.weatherpi.display.EDisplay;

public class Clock implements Displayable {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");

	@Override
	public void display(Display display) {
		String s = LocalDateTime.now().format(formatter);

		EDisplay actDisplay = EDisplay.ONE;
		for (int i = 0, j = s.length(); i < j; i++) {
			display.writeDigitNum(actDisplay,Integer.valueOf(s.substring(i, i+1)));
			actDisplay = actDisplay.getNext();
		}
		display.drawColon(true);
	}

}
