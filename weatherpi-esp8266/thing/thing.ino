#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <WiFiClient.h>
#include <Wire.h>
#include <ArduinoJson.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_TSL2591.h>
#include <Adafruit_MCP9808.h>
#include <Adafruit_BME280.h>
#include <DHT.h>
#include <Time.h>
#include <RtcDS3231.h>
#include <RingBuffer.h>

#define DHTTYPE DHT22
#define DHTPIN  5

#define WIFI_ERROR_PIN 4
#define READY_PIN 15

#define BUFFER_PAGE_SIZE 20

const char* ssid         = "xxxxxxxx";
const char* password     = "xxxxxxxx";
const char* mqttServer   = "xxxxxxxx";
const char* mqttUser     = "xxxxxxxx";
const char* mqttPassword = "xxxxxxxx";

const String place = "BIRDHOUSE";
const String sensordata = "sensordata";
const String typeTemperature = "TEMPERATURE";
const String typeHumidity    = "HUMIDITY";
const String typeIlluminance = "ILLUMINANCE";
const String typePressure    = "PRESSURE";
const String typeSolarCharge = "SOLAR_CHARGE";

const unsigned int deepSleepTimeNormal       = 2 * 60000000; // 
const unsigned int deepSleepTimeNoConnection = 15 * 60000000; // 

char json[256];

union bufferpage_t {
  float    f[BUFFER_PAGE_SIZE / sizeof(float)];
  time_t   t[BUFFER_PAGE_SIZE / sizeof(time_t)];
  uint8_t  byte[BUFFER_PAGE_SIZE];
};

WiFiClient espClient;
PubSubClient client(espClient);

RtcDS3231        Rtc;

Adafruit_TSL2591 tsl     = Adafruit_TSL2591(4711);
Adafruit_MCP9808 mcp9808 = Adafruit_MCP9808();
DHT              dht(DHTPIN, DHTTYPE, 11);
Adafruit_BME280  bme;
RingBuffer       ringBuffer = RingBuffer();   

time_t getCurrentTime() {
  RtcDateTime now = Rtc.GetDateTime();
  return now.Epoch32Time() - 7200;
}

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
  boolean retVal = true;
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    tries--;
    if (tries <= 0) {
      // got no connection to WiFi, going to sleep
      displayWifiError();
      return false;
      //ESP.deepSleep(deepSleepTimeNoConnection, WAKE_RF_DEFAULT);
    }
  }
  return retVal;
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

bufferpage_t getPage() {
  bufferpage_t page;

  sensors_event_t event;
  tsl.getEvent(&event);
  
  page.t[0] = getCurrentTime();
  page.f[1] = bme.readSealevelPressure(335.0) / 100.0F;
  page.f[2] = event.light;
  page.f[3] = dht.readTemperature();
  page.f[4] = dht.readHumidity();
  return page;
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

void sendMCP9808Temperature(JsonObject& jsonObject, time_t t, float temperature) {
  
  String topic = sensordata + "/" + place + "/" + typeTemperature;
  topic.toLowerCase();
  
  jsonObject.set("sensor", "MCP9808");
  jsonObject.set("type", typeTemperature);
  jsonObject.set("time", t);
  jsonObject.set("temperature", temperature, 2);

  jsonObject.printTo(json, sizeof(json));
  client.publish(topic.c_str(), json);
  jsonObject.remove("temperature");
  jsonObject.remove("time");
  jsonObject.remove("type");
  jsonObject.remove("sensor");
  delay(250);
}


void sendMCP9808Temperature(JsonObject& jsonObject) {
  sendMCP9808Temperature(jsonObject,getCurrentTime(),mcp9808.readTempC());
}

void sendTSL2591Luminosity(JsonObject& jsonObject, time_t t, float light) {
  
  if ((light == 0) |
      (light > 4294966000.0) |
      (light < -4294966000.0))
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
    jsonObject.set("time", t);
    jsonObject.set("illuminance", light, 6); 
    jsonObject.printTo(json, sizeof(json));   
    client.publish(topic.c_str(), json);
    jsonObject.remove("illuminance");
    jsonObject.remove("time");
    jsonObject.remove("type");
    jsonObject.remove("sensor");

  }
  delay(250);
}


void sendTSL2591Luminosity(JsonObject& jsonObject) {

  sensors_event_t event;
  tsl.getEvent(&event);

  sendTSL2591Luminosity(jsonObject, getCurrentTime(),event.light);
}

void sendDHTTemperatureHumidity(JsonObject& jsonObject, time_t t, float temperature, float humidity) {
  
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
    jsonObject.set("time", t);
    
    jsonObject.printTo(json, sizeof(json));
    client.publish(topic.c_str(), json);
    jsonObject.remove("temperature");

    topic = sensordata + "/" + place + "/" + typeHumidity;
    topic.toLowerCase();
    
    jsonObject.set("type", typeHumidity);
    jsonObject.set("humidity", humidity, 2);
    jsonObject.printTo(json, sizeof(json));
    client.publish(topic.c_str(), json);
    jsonObject.remove("humidity");
    jsonObject.remove("time");
    jsonObject.remove("type");
    jsonObject.remove("sensor");
  }
  delay(250);
}

void sendDHTTemperatureHumidity(JsonObject& jsonObject) {
  
  float humidity    = dht.readHumidity();
  float temperature = dht.readTemperature();

  sendDHTTemperatureHumidity(jsonObject, getCurrentTime(), temperature, humidity);
}

void sendBMETemperatureHumidityPressure(JsonObject& jsonObject, time_t t, float temperature, float humidity, float pressure) {
    
  String topic = sensordata + "/" + place + "/" + typeTemperature;
  topic.toLowerCase();
  
  jsonObject.set("sensor", "BME280");
  jsonObject.set("type", typeTemperature);
  jsonObject.set("temperature", temperature, 2);  
  jsonObject.set("time", t);
    
  jsonObject.printTo(json, sizeof(json));
  client.publish(topic.c_str(), json);
  jsonObject.remove("temperature");

  topic = sensordata + "/" + place + "/" + typeHumidity;
  topic.toLowerCase();
    
  jsonObject.set("type", typeHumidity);
  jsonObject.set("humidity", humidity, 2);
  jsonObject.printTo(json, sizeof(json));
  client.publish(topic.c_str(), json);
  jsonObject.remove("humidity");

  topic = sensordata + "/" + place + "/" + typePressure;
  topic.toLowerCase();
    
  jsonObject.set("type", typePressure);
  jsonObject.set("pressure", float_with_n_digits(pressure, 6));
  jsonObject.printTo(json, sizeof(json));
  client.publish(topic.c_str(), json);
  jsonObject.remove("pressure");
    
  jsonObject.remove("time");
  jsonObject.remove("type");
  jsonObject.remove("sensor");
  
  delay(250);
}

void sendBMEPressure(JsonObject& jsonObject, time_t t, float pressure) {
    
  String topic = sensordata + "/" + place + "/" + typePressure;
  topic.toLowerCase();
  
  jsonObject.set("sensor", "BME280");
  jsonObject.set("type", typePressure);
  jsonObject.set("time", t);
  jsonObject.set("pressure", float_with_n_digits(pressure, 6));
    
  jsonObject.printTo(json, sizeof(json));
  client.publish(topic.c_str(), json);
  jsonObject.remove("pressure");
    
  jsonObject.remove("time");
  jsonObject.remove("type");
  jsonObject.remove("sensor");
  
  delay(250);
}

void sendBMETemperatureHumidityPressure(JsonObject& jsonObject) {
  
  float humidity    = bme.readHumidity();
  float temperature = bme.readTemperature();
  float pressure    = bme.readSealevelPressure(335.0) / 100.0F;
  sendBMETemperatureHumidityPressure(jsonObject, getCurrentTime(), temperature, humidity, pressure);
}

void addPageToBufferAndSleep() {

  bufferpage_t page = getPage();
  ringBuffer.write(page.byte,sizeof(page));
  ESP.deepSleep(deepSleepTimeNoConnection, WAKE_RF_DEFAULT);
}


void sendBuffer(JsonObject& jsonObject) {
  bufferpage_t page;
  boolean result;
  while (ringBuffer.read(page.byte,sizeof(page))) {
    // send the buffer via MQTT
    sendBMEPressure(jsonObject,page.t[0],page.f[1]);
    sendTSL2591Luminosity(jsonObject,page.t[0],page.f[2]);
    sendDHTTemperatureHumidity(jsonObject,page.t[0],page.f[3],page.f[4]);
  }      
}

void setup()
{
  Serial.begin(57600);

  // the function to get the time from the RTC
  Serial.println("init clock...");
  Rtc.Begin();

  Serial.println("init ring buffer...");
  ringBuffer.begin();

  Serial.println("init tsl...");
  tsl.begin();
  /* Configure the sensor */
  configureTSL();

  Serial.println("init mcp...");
  mcp9808.begin();

  Serial.println("init dht...");
  dht.begin();

  Serial.println("init bme...");
  bme.begin();
  
  pinMode(WIFI_ERROR_PIN, OUTPUT);

  Serial.println("check and init buffer...");
  // check and init buffer
  boolean initialized = ringBuffer.isInitialized();
  if (!initialized || ringBuffer.getPageSize() != BUFFER_PAGE_SIZE) {
    ringBuffer.format(BUFFER_PAGE_SIZE);
  }
  Serial.println("connect to WIFI...");
  // connect to Wifi
  if (!wifiConnect()) {
    addPageToBufferAndSleep();
  }

  Serial.println("connect to MQTT...");

  if (mqttConnect()) {
    //StaticJsonBuffer<256> jsonBuffer;
    DynamicJsonBuffer jsonBuffer;
    JsonObject& obj = jsonBuffer.createObject();

    obj.set("place", place);

    // check, if we have to send data from the ring buffer

    Serial.println("sending data...");
    // send temperature
    sendMCP9808Temperature(obj);
    // send illumination
    sendTSL2591Luminosity(obj);
    // send DHT data
    sendDHTTemperatureHumidity(obj);
    // send BME data
    sendBMETemperatureHumidityPressure(obj);
    
    sendBuffer(obj);
  } else {
    addPageToBufferAndSleep();
  }
  Serial.println("going to sleep");
  ESP.deepSleep(deepSleepTimeNormal, WAKE_RF_DEFAULT);

}

void loop(){}
