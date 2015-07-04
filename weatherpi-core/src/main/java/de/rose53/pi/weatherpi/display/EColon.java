package de.rose53.pi.weatherpi.display;

import java.util.LinkedList;
import java.util.List;

public enum EColon {

	CENTER(0x02),
	LEFT_LOWER(0x04),
	LEFT_UPPER(0x08),
	DECIMAL(0x10);

	private final int bit;

	private EColon(int bit) {
		this.bit = bit;
	}

	public int getBit() {
		return bit;
	}

	public List<EColon> asList() {
		LinkedList<EColon> retVal = new LinkedList<>();
		retVal.add(this);
		return retVal;
	}
}
