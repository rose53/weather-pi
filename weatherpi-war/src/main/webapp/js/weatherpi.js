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

            log.debug("initControlPage: using websocket at " + "ws://" + location.host + "/weatherpi/sensorevents");

            var sensorWebSocket = new WebSocket("ws://" + location.host + "/weatherpi/sensorevents");

            sensorWebSocket.onopen = function() {
            };
            sensorWebSocket.onmessage = function(message) {
                var sensorevent = jQuery.parseJSON(message.data);
                log.debug("onmessage: type = " + sensorevent.type + ", place = " + sensorevent.place);
                if ("TEMPERATURE" === sensorevent.type) {
                    if ("BIRDHOUSE" === sensorevent.place) {
                        lcarsControlView.updateBirdhouseTemperature(sensorevent.temperature.toFixed(1));
                    }
                } else if ("HUMIDITY" === sensorevent.type) {
                    if ("BIRDHOUSE" === sensorevent.place) {
                        lcarsControlView.updateBirdhouseHumidity(parseInt(sensorevent.humidity));
                    }
                } else if ("PRESSURE" === sensorevent.type) {
                    if ("BIRDHOUSE" === sensorevent.place) {
                        lcarsControlView.updateBirdhousePressure(parseInt(sensorevent.pressure));
                    }
                } else if ("WINDSPEED" === sensorevent.type) {
                    if ("ANEMOMETER" === sensorevent.place) {
                        lcarsControlView.updateAnemometerWindspeed(sensorevent.windspeed.toFixed(2));
                    }
                }
            };

            sensordataService.getPressure("actual","birdhouse",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateBirdhousePressure(parseInt(data.sensorData[0].value));
                    }
                });

            sensordataService.getTemperature("actual","anemometer",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateAnemometerWindspeed(parseInt(sensorevent.windspeed.toFixed(2)));
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
                        lcarsControlView.updateAnemometerWindspeed(data.sensorData[0].value.toFixed(2));
                    }
                });
                
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