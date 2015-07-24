//  How to access GPIO registers from C-code on the Raspberry-Pi
//  Example program
//  15-January-2012
//  Dom and Gert
//


// Access from ARM Running Linux

#define BCM2708_PERI_BASE        0x20000000
#define GPIO_BASE                (BCM2708_PERI_BASE + 0x200000) /* GPIO controller */


#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <wiringPi.h>

#define MAXTIMINGS 100

//#define DEBUG

#define DHT11 11
#define DHT22 22
#define AM2302 2302

int readDHT(int type, int pin);

int main(int argc, char **argv)
{
	wiringPiSetup();

	if (argc != 3) {
        printf("usage: %s [11|22|2302] GPIOpin#\n", argv[0]);
        printf("example: %s 2302 4 - Read from an AM2302 connected to GPIO #4\n", argv[0]);
        return 2;
	}
	int type = 0;
	if (strcmp(argv[1], "11") == 0) type = DHT11;
	if (strcmp(argv[1], "22") == 0) type = DHT22;
	if (strcmp(argv[1], "2302") == 0) type = AM2302;
	if (type == 0) {
        printf("Select 11, 22, 2302 as type!\n");
        return 3;
	}
 
	int dhtpin = atoi(argv[2]);

	if (dhtpin <= 0) {
        printf("Please select a valid GPIO pin #\n");
        return 3;
	}

	readDHT(type, dhtpin);
	return 0;

} // main


int bits[250], data[100];
int bitidx = 0;

int readDHT(int type, int pin) {
	int counter = 0;
	int laststate = HIGH;
	int j=0;

	// Set GPIO pin to output
	pinMode(pin, OUTPUT);


	digitalWrite(pin, HIGH);
	delay(500);  // 500 ms
	digitalWrite(pin, LOW);
	delay(20);

	pinMode(pin, INPUT);

	data[0] = data[1] = data[2] = data[3] = data[4] = 0;

	// wait for pin to drop?
	while (digitalRead(pin) > 0) {
		delayMicroseconds(1);
	}

	// read data!
	for (int i=0; i< MAXTIMINGS; i++) {
		counter = 0;
		while ( digitalRead(pin) == laststate) {
            counter++;
			if (counter == 1000) 
                  break;
		}
		laststate = digitalRead(pin);
		if (counter == 1000) break;
   
		bits[bitidx++] = counter;

		if ((i>3) && (i%2 == 0)) {
			// shove each bit into the storage bytes
			data[j/8] <<= 1;
			if (counter > 200)
				data[j/8] |= 1;
			j++;
		}
	}

	if ((j >= 39) && (data[4] == ((data[0] + data[1] + data[2] + data[3]) & 0xFF)) ) {
		if (type == DHT11)
            printf("%d,%d\n", data[2], data[0]);
		if (type == DHT22) {
            float f, h;
            h = data[0] * 256 + data[1];
            h /= 10;

            f = (data[2] & 0x7F)* 256 + data[3];
			f /= 10.0;
			if (data[2] & 0x80) {
				f *= -1;
			}		 
            printf("%f,%f\n", f, h);
		}
		return 1;
	}
	return 0;
}
