/* global log, sensordataService */

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
            var $page = $("#controlview");
            $("#controlcanvas").jLCARSControlView();

            $("#controlcanvas").on("daybuttontouch", function(event) {
                handleDayButtonEvent(event);
            });

            $("#controlcanvas").on("weekbuttontouch", function(event) {
                handleWeekButtonEvent(event);
            });

            var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
            $page.on("pageshow", function(event, ui) {
                lcarsControlView.refresh();
            });

            lcarsControlView.refresh();

            log.debug("initControlPage: using websocket at " + "ws://" + location.host + "/websocket/sensorevents");

            var sensorWebSocket = new WebSocket("ws://" + location.host + "/websocket/sensorevents");

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
                    }
                } else if ("HUMIDITY" === sensorevent.type) {
                    if ("INDOOR" === sensorevent.place) {
                        lcarsControlView.updateIndoorHumidity(parseInt(sensorevent.humidity));
                    } else if ("OUTDOOR" === sensorevent.place) {
                        lcarsControlView.updateOutdoorHumidity(parseInt(sensorevent.humidity));
                    }
                } else if ("PRESSURE" === sensorevent.type) {
                    if ("INDOOR" === sensorevent.place) {
                        lcarsControlView.updateIndoorPressure(parseInt(sensorevent.pressure));
                    }
                }
            };

            sensordataService.getTemperature("actual","indoor",
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateIndoorTemperature(data.sensorData[0].value.toFixed(1));
                    }
                });

            sensordataService.getPressure("actual","indoor",
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateIndoorPressure(parseInt(data.sensorData[0].value));
                    }
                });

            sensordataService.getHumidity("actual","indoor",
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateIndoorHumidity(parseInt(data.sensorData[0].value));
                    }
                });

            sensordataService.getTemperature("actual","outdoor",
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateOutdoorTemperature(data.sensorData[0].value.toFixed(1));
                    }
                });

            sensordataService.getHumidity("actual","outdoor",
                function(data){
                    if (data.sensorData.length > 0) {
                        lcarsControlView.updateOutdoorHumidity(parseInt(data.sensorData[0].value));
                    }
                });

            setInterval(function(){ clock(); }, 5000);
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
            return methods[method].apply(this, Array.prototype.slice.call(
                    arguments, 1));
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

var handleDayButtonEvent = function(event) {
    log.debug("handleDayButtonEvent: daybuttontouch");
    var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
    sensordataService.getTemperature("day",function(data){ log.debug("cb" + data.maxValue); });
};


var handleWeekButtonEvent = function(event) {
    log.debug("handleWeekButtonEvent: weekbuttontouch");
    sensordataService.getTemperature("week",function(data){ log.debug(data); });
};

var clock = function() {
    var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
    lcarsControlView.updateClock();
};