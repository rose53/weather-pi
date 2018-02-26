/* global log, sensordataService, RangeEnum, forecastService */

(function($) {

    var methods = {
        initControlPage: function(options) {
            var settings = {
                callback: function() {
                }
            };
            if (options) {
                $.extend(settings, options);
            }
            log.trace("initControlPage: init ...");
            preload([
                'images/sunset.png',
                'images/sunrise.png']);

            var $page = $("#controlview");
            $("#controlcanvas").jLCARSControlView();

            $("#controlcanvas").on("graphbuttontouch", function(event) {
                handleGraphButtonEvent(event);
            });

            var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
            $page.on("pageshow", function(event, ui) {
                lcarsControlView.refresh();
            });

            lcarsControlView.refresh();

            connect();

            sensordataService.getPressure("actual","birdhouse",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateBirdhousePressure(parseInt(data.sensorData[0].value));
                    }
                });

            sensordataService.getTemperature("actual","birdhouse",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateBirdhouseTemperature(data.sensorData[0].value.toFixed(1));
                    }
                });

            sensordataService.getHumidity("actual","birdhouse",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateBirdhouseHumidity(parseInt(data.sensorData[0].value));
                    }
                });

            sensordataService.getWindspeed("actual","anemometer",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateAnemometerWindspeed(data.sensorData[0].value.toFixed(1));
                    }
                });
/*
            sensordataService.getTemperature("actual","birdhouse",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateBirdhouseTemperature(data.sensorData[0].value.toFixed(1));
                    }
                });

            sensordataService.getHumidity("actual","birdhouse",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateBirdhouseHumidity(parseInt(data.sensorData[0].value));
                    }
                });
*/
            setInterval(function(){ schedule(); }, 5000);
        },
        initAll: function(options) {
            var settings = {
                callback: function() {
                }
            };
            if (options) {
                $.extend(settings, options);
            }

            $().initApp("initControlPage");

        }
    };

    $.fn.initApp = function(method) {

        // Method calling logic
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        } else if (typeof method === 'object' || !method) {
            return methods.initAll.apply(this, arguments);
        } else {
            $.error('Method ' + method + ' does not exist');
        }
    };

})(jQuery);

$(document).ready(function() {
    $().initApp();
});

function preload(arrayOfImages) {
  $(arrayOfImages).each(function (){
    $('<img/>')[0].src = this;
  });
};

var handleGraphButtonEvent = function(event) {
    log.debug("handleRangeButtonEvent: " + event.sensor);
    log.debug("                        " + RangeEnum.properties[event.range].name);
    log.debug("                        " + event.place);
    var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
    sensordataService.doRestCall(event.sensor,event.name,RangeEnum.properties[event.range].queryvalue,event.place,
        lcarsControlView.getMaxGraphData(),
        function(data){
            if (data.sensorData.length > 0) {
                lcarsControlView.updateGraphData(data,event.sensor,event.range);
            }
        });
};


var lastForecast  = 0;
var lastGraphData = 0;

var schedule = function() {
    var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
    lcarsControlView.updateClock();
    if (lastForecast <= new Date().getTime() - 5 * 60 * 1000 ) {
        lastForecast = new Date().getTime();
        forecastService.daily(
            function(data){
                lcarsControlView.updateDailyForecast(data);
            });
        forecastService.currently(
            function(data){
                lcarsControlView.updateCurrentlyForecast(data);
            });
    }
    if (lastGraphData <= new Date().getTime() - 10 * 60 * 1000 ) {
        lastGraphData = new Date().getTime();
        var graphParams = lcarsControlView.getGraphDataParams();
        sensordataService.doRestCall(graphParams.sensor,graphParams.name,RangeEnum.properties[graphParams.range].queryvalue,graphParams.place,
        lcarsControlView.getMaxGraphData(),
        function(data){
            if (data.sensorData.length > 0) {
                lcarsControlView.updateGraphData(data,graphParams.sensor,graphParams.range);
            }
        });
    }
};

var connect = function () {

      log.debug("connect: using websocket at " + "ws://" + WEATHERPI_HOST + "/weatherpi/sensorevents");
      var ws = new WebSocket("ws://" + WEATHERPI_HOST + "/weatherpi/sensorevents");
      ws.onopen = function() {
      };

      ws.onmessage = function(message) {
          var sensorevent      = jQuery.parseJSON(message.data);
          var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
          log.debug("onmessage: type = " + sensorevent.type + ", place = " + sensorevent.place);
          if ("TEMPERATURE" === sensorevent.type) {
              if ("BIRDHOUSE" === sensorevent.place) {
                  lcarsControlView.updateBirdhouseTemperature(sensorevent.temperature.toFixed(1));
              } else if ("DUSTSENSOR" === sensorevent.place) {
                  lcarsControlView.updateDustsensorTemperature(sensorevent.temperature.toFixed(1));
              }
          } else if ("HUMIDITY" === sensorevent.type) {
              if ("BIRDHOUSE" === sensorevent.place) {
                  lcarsControlView.updateBirdhouseHumidity(parseInt(sensorevent.humidity));
              } else if ("DUSTSENSOR" === sensorevent.place) {
                  lcarsControlView.updateDustsensorHumidity(sensorevent.humidity);
              }
          } else if ("PRESSURE" === sensorevent.type) {
              if ("BIRDHOUSE" === sensorevent.place) {
                  lcarsControlView.updateBirdhousePressure(parseInt(sensorevent.pressure));
              }
          } else if ("WINDSPEED" === sensorevent.type) {
              if ("ANEMOMETER" === sensorevent.place) {
                  lcarsControlView.updateAnemometerWindspeed(sensorevent.windspeed.toFixed(1));
              }
          } else if ("DUST_PM10" === sensorevent.type) {
              if ("DUSTSENSOR" === sensorevent.place) {
                  lcarsControlView.updateDustsensorPm10(parseInt(sensorevent.pm10));
              }
          } else if ("DUST_PM25" === sensorevent.type) {
              if ("DUSTSENSOR" === sensorevent.place) {
                  lcarsControlView.updateDustsensorPm25(parseInt(sensorevent.pm25));
              }
          }
      };

      ws.onclose = function(e) {
          log.debug('Socket is closed. Reconnect will be attempted in 1 second.', e.reason);
          setTimeout(function() {
          connect();
        }, 1000)
      };

      ws.onerror = function(err) {
          log.debug('Socket encountered error: ', err.message, 'Closing socket')
          ws.close()
      };
};
