#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <WiFiClient.h>
#include <Wire.h>
#include <ArduinoJson.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_TSL2591.h>
#include <Adafruit_MCP9808.h>
#include <DHT.h>

#define DHTTYPE DHT22
#define DHTPIN  5

#define WIFI_ERROR_PIN 4
#define READY_PIN 15

const char* ssid     = "xxx";
const char* password = "xxx";
const char* mqttServer = "xxx";
const char* mqttUser = "xxx";
const char* mqttPassword = "xxx";

const String place = "BIRDHOUSE";
const String sensordata = "sensordata";
const String typeTemperature = "TEMPERATURE";
const String typeHumidity    = "HUMIDITY";
const String typeIlluminance = "ILLUMINANCE";
const String typeSolarCharge = "SOLAR_CHARGE";

const unsigned int deepSleepTime = 2 * 60000000;

char json[256];

WiFiClient espClient;
PubSubClient client(espClient);

Adafruit_TSL2591 tsl     = Adafruit_TSL2591(4711);
Adafruit_MCP9808 mcp9808 = Adafruit_MCP9808();
DHT dht(DHTPIN, DHTTYPE, 11);


void displayWifiError() {
  for (int i = 0; i < 10; i++) {
      digitalWrite(WIFI_ERROR_PIN,HIGH);
      delay(500);  
      digitalWrite(WIFI_ERROR_PIN,LOW);
      delay(500);  
  }
}
/**************************************************************************/
/*
    Try to connect to the WIFI, after 10 tries, we do a deep sleep
*/
/**************************************************************************/
boolean wifiConnect(void) {
  WiFi.begin(ssid, password);
  // Wait for connection
  int tries = 10;
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    tries--;
    if (tries <= 0) {
      // got no connection to WiFi, going to sleep
      displayWifiError();
      ESP.deepSleep(deepSleepTime, WAKE_RF_DEFAULT);
    }
  }
}

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
    Configures the gain and integration time for the TSL2591
*/
/**************************************************************************/
void configureTSL(void)
{
  // You can change the gain on the fly, to adapt to brighter/dimmer light situations
  //tsl.setGain(TSL2591_GAIN_LOW);    // 1x gain (bright light)
  tsl.setGain(TSL2591_GAIN_MED);      // 25x gain
  //tsl.setGain(TSL2591_GAIN_HIGH);   // 428x gain

  // Changing the integration time gives you a longer time over which to sense light
  // longer timelines are slower, but are good in very low light situtations!
  tsl.setTiming(TSL2591_INTEGRATIONTIME_100MS);  // shortest integration time (bright light)
  //tsl.setTiming(TSL2591_INTEGRATIONTIME_200MS);
  //tsl.setTiming(TSL2591_INTEGRATIONTIME_300MS);
  //tsl.setTiming(TSL2591_INTEGRATIONTIME_400MS);
  //tsl.setTiming(TSL2591_INTEGRATIONTIME_500MS);
  //tsl.setTiming(TSL2591_INTEGRATIONTIME_600MS);  // longest integration time (dim light)

}

void sendMCP9808Temperature(JsonObject& jsonObject) {
  
  String topic = sensordata + "/" + place + "/" + typeTemperature;
  topic.toLowerCase();
  
  jsonObject.set("sensor", "MCP9808");
  jsonObject.set("type", typeTemperature);
  jsonObject.set("accuracy", 0.25, 2);
  jsonObject.set("temperature", mcp9808.readTempC(), 2);

  jsonObject.printTo(json, sizeof(json));
  client.publish(topic.c_str(), json);
  jsonObject.remove("temperature");
  jsonObject.remove("accuracy");
  jsonObject.remove("type");
  jsonObject.remove("sensor");
  delay(250);
}

void sendTSL2591Luminosity(JsonObject& jsonObject) {

  sensors_event_t event;
  tsl.getEvent(&event);
  if ((event.light == 0) |
      (event.light > 4294966000.0) |
      (event.light < -4294966000.0))
  {
    /* If event.light = 0 lux the sensor is probably saturated */
    /* and no reliable data could be generated! */
    /* if event.light is +/- 4294967040 there was a float over/underflow */
    jsonObject["error"] = "TSL2591 read error (Invalid data (adjust gain or timing))";
    jsonObject.printTo(json, sizeof(json));
    client.publish("sensordata/test/status", json);
    jsonObject.remove("error");
  }
  else
  {
    String topic = sensordata + "/" + place + "/" + typeIlluminance;
    topic.toLowerCase();
    
    jsonObject.set("sensor", "TSL2591");
    jsonObject.set("type", typeIlluminance);
    jsonObject.set("accuracy", 0.0, 2);
    jsonObject.set("illuminance", event.light, 6); 
    jsonObject.printTo(json, sizeof(json));   
    client.publish(topic.c_str(), json);
    jsonObject.remove("illuminance");
    jsonObject.remove("accuracy");
    jsonObject.remove("type");
    jsonObject.remove("sensor");

  }
  delay(250);
}

void sendDHTTemperatureHumidity(JsonObject& jsonObject) {
  
  float humidity    = dht.readHumidity();
  float temperature = dht.readTemperature();

  if (isnan(humidity) || isnan(temperature)) {
    jsonObject["error"] = "DHT22 read error";
    jsonObject.printTo(json, sizeof(json));
    client.publish("sensordata/test/status", json);
    jsonObject.remove("error");
  } else {

    String topic = sensordata + "/" + place + "/" + typeTemperature;
    topic.toLowerCase();
  
    jsonObject.set("sensor", "DHT22");
    jsonObject.set("type", typeTemperature);
    jsonObject.set("temperature", temperature, 2);  
    jsonObject.set("accuracy", 0.5, 2);
    
    jsonObject.printTo(json, sizeof(json));
    client.publish(topic.c_str(), json);
    jsonObject.remove("temperature");

    topic = sensordata + "/" + place + "/" + typeHumidity;
    topic.toLowerCase();
    
    jsonObject.set("type", typeHumidity);
    jsonObject.set("accuracy", 0.0, 2);
    jsonObject.set("humidity", humidity, 2);
    jsonObject.printTo(json, sizeof(json));
    client.publish(topic.c_str(), json);
    jsonObject.remove("humidity");
    jsonObject.remove("accuracy");
    jsonObject.remove("type");
    jsonObject.remove("sensor");
  }
  delay(250);
}

void setup()
{
  Serial.begin(115200);

  tsl.begin();
  /* Configure the sensor */
  configureTSL();

  mcp9808.begin();

  dht.begin();

  pinMode(WIFI_ERROR_PIN, OUTPUT);
  //pinMode(READY_PIN, INPUT);
  // connect to Wifi
  wifiConnect();

  if (mqttConnect()) {
    //StaticJsonBuffer<256> jsonBuffer;
    DynamicJsonBuffer jsonBuffer;
    JsonObject& obj = jsonBuffer.createObject();

    obj.set("place", place);
    // send temperature
    sendMCP9808Temperature(obj);
    // send illumination
    sendTSL2591Luminosity(obj);
    // send DHT data
    sendDHTTemperatureHumidity(obj);
    // send chrger info
    //sendSolarChargeStatus(obj);
  }
  Serial.println("going to sleep");
  ESP.deepSleep(deepSleepTime, WAKE_RF_DEFAULT);
}

void advancedRead(void)
{
  // More advanced data read example. Read 32 bits with top 16 bits IR, bottom 16 bits full spectrum
  // That way you can do whatever math and comparisons you want!
  uint32_t lum = tsl.getFullLuminosity();
  uint16_t ir, full;
  ir = lum >> 16;
  full = lum & 0xFFFF;
  Serial.print("[ "); Serial.print(millis()); Serial.print(" ms ] ");
  Serial.print("IR: "); Serial.print(ir);  Serial.print("  ");
  Serial.print("Full: "); Serial.print(full); Serial.print("  ");
  Serial.print("Visible: "); Serial.print(full - ir); Serial.print("  ");
  Serial.print("Lux: "); Serial.println(tsl.calculateLux(full, ir));
}


void loop(){}
