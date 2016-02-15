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
                    if ("INDOOR" === sensorevent.place) {
                        lcarsControlView.updateIndoorTemperature(sensorevent.temperature.toFixed(1));
                    } else if ("OUTDOOR" === sensorevent.place) {
                        lcarsControlView.updateOutdoorTemperature(sensorevent.temperature.toFixed(1));
                    } else if ("BIRDHOUSE" === sensorevent.place) {
                        lcarsControlView.updateBirdhouseTemperature(sensorevent.temperature.toFixed(1));
                    }
                } else if ("HUMIDITY" === sensorevent.type) {
                    if ("INDOOR" === sensorevent.place) {
                        lcarsControlView.updateIndoorHumidity(parseInt(sensorevent.humidity));
                    } else if ("OUTDOOR" === sensorevent.place) {
                        lcarsControlView.updateOutdoorHumidity(parseInt(sensorevent.humidity));
                    } else if ("BIRDHOUSE" === sensorevent.place) {
                        lcarsControlView.updateBirdhouseHumidity(parseInt(sensorevent.humidity));
                    }
                } else if ("PRESSURE" === sensorevent.type) {
                    if ("INDOOR" === sensorevent.place) {
                        lcarsControlView.updateIndoorPressure(parseInt(sensorevent.pressure));
                    }
                }
            };

            sensordataService.getTemperature("actual","indoor",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateIndoorTemperature(data.sensorData[0].value.toFixed(1));
                    }
                });

            sensordataService.getPressure("actual","indoor",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateIndoorPressure(parseInt(data.sensorData[0].value));
                    }
                });

            sensordataService.getHumidity("actual","indoor",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateIndoorHumidity(parseInt(data.sensorData[0].value));
                    }
                });

            sensordataService.getTemperature("actual","outdoor",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateOutdoorTemperature(data.sensorData[0].value.toFixed(1));
                    }
                });

            sensordataService.getHumidity("actual","outdoor",1,
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateOutdoorHumidity(parseInt(data.sensorData[0].value));
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
    sensordataService.doRestCall(event.sensor,RangeEnum.properties[event.range].queryvalue,event.place,
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
        sensordataService.doRestCall(graphParams.sensor,RangeEnum.properties[graphParams.range].queryvalue,graphParams.place,
        lcarsControlView.getMaxGraphData(),
        function(data){
            if (data.sensorData.length > 0) {
                lcarsControlView.updateGraphData(data,graphParams.sensor,graphParams.range);
            }
        });
    }
};