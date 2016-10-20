#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <WiFiClient.h>
#include <ArduinoJson.h>

const char* ssid         = "xxxxx";
const char* password     = "xxxxx";
const char* mqttServer   = "xxxxx"; 
const char* mqttUser     = "xxxxx";
const char* mqttPassword = "xxxxx";

const String place         = "ANEMOMETER";
const String sensordata    = "sensordata";
const String typeWindspeed = "WINDSPEED";

const unsigned int deepSleepTimeNoConnection = 15 * 60000000; //

const byte             anemometerPin = 2;
const unsigned long    measureInterval = 5000;
volatile unsigned long pulses = 0;

WiFiClient espClient;
PubSubClient client(espClient);

char json[256];

/**************************************************************************/
/*
    Try to connect to the MQTT broker, after 10 tries, we return false
*/
/**************************************************************************/
boolean mqttConnect(void) {
  client.setServer(mqttServer, 1883);

  int  tries = 10;
  while (!client.connected()) {
    tries--;
    if (tries == 0) {
      return false;
    }
    if (!client.connect(place.c_str(), mqttUser, mqttPassword)) {
      delay(5000);
    }
  }
  return true;
}

/**************************************************************************/
/*
    Try to connect to the WIFI
*/
/**************************************************************************/
boolean wifiConnect(void) {
  WiFi.begin(ssid, password);
  // Wait for connection
  int tries = 10;
  boolean retVal = true;
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    tries--;
    if (tries <= 0) {
      Serial.println("going to WIFI sleep");
      ESP.deepSleep(deepSleepTimeNoConnection, WAKE_RF_DEFAULT);
    }
  }
  return retVal;
}


void sendWindspeed(JsonObject& jsonObject, float windspeed) {


  String topic = sensordata + "/" + place + "/" + typeWindspeed;
  topic.toLowerCase();

  jsonObject.set("sensor", "ELTAKO_WS");
  jsonObject.set("type", typeWindspeed);
  jsonObject.set("windspeed", windspeed, 2);

  jsonObject.printTo(json, sizeof(json));
  client.publish(topic.c_str(), json);
  jsonObject.remove("windspeed");
  jsonObject.remove("type");
  jsonObject.remove("sensor");

  delay(250);
}

void pulseCallback() {
  pulses++;
}

float getWindSpeed() {

  attachInterrupt(digitalPinToInterrupt(anemometerPin), pulseCallback, RISING);

  pulses = 0;
  unsigned long start = micros();
  delay(measureInterval); // in milliseconds
  unsigned long diff = micros() - start;
  detachInterrupt(digitalPinToInterrupt(anemometerPin));

  if (diff == 0) {
    return 0.0;
  }
  return (pulses / (diff / (1000.0f * 1000.0f) + 2 )) / 3.0f;
}

void setup() {

  Serial.begin(115200);
   // Connect to WiFi network
  Serial.println("connect to WIFI...");
  wifiConnect();
  
  Serial.println("getting windspeed ...");
  // get actual windspeed
  float actWindspeed = getWindSpeed();
  unsigned int deepSleepTime = 5 * 60000000;
  if (actWindspeed > 0.0) {
    deepSleepTime = 1 * 60000000; // if we have some wind, we do not want to sleep so long
  }
 

  Serial.println("connect to MQTT...");
  if (mqttConnect()) {
    //StaticJsonBuffer<256> jsonBuffer;
    DynamicJsonBuffer jsonBuffer;
    JsonObject& obj = jsonBuffer.createObject();

    obj.set("place", place);

    Serial.println("sending data...");
    // send windspeed
    sendWindspeed(obj, actWindspeed);
  }
  Serial.println("going to sleep");
  ESP.deepSleep(deepSleepTime, WAKE_RF_DEFAULT);
}

void loop() {}
