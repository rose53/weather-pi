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

            log.debug("initControlPage: using websocket at " + "ws://" + location.host + "/websocket/sensorevent");

            var sensorWebSocket = new WebSocket("ws://munin.local:8080/websocket/sensorevents");

            sensorWebSocket.onopen = function() {
            };
            sensorWebSocket.onmessage = function(message) {
                var sensorevent = jQuery.parseJSON(message.data);
                log.debug("onmessage: type = " + sensorevent.type + ", place = " + sensorevent.place);
                if ("TEMPERATURE" === sensorevent.type) {
                    if ("INDOOR" === sensorevent.place) {
                        lcarsControlView.updateIndoorTemperature(sensorevent.temperature.toFixed(1));
                    }
                } else if ("HUMIDITY" === sensorevent.type) {
                    if ("INDOOR" === sensorevent.place) {
                        lcarsControlView.updateIndoorHumidity(parseInt(sensorevent.humidity));
                    }
                } else if ("PRESSURE" === sensorevent.type) {
                    if ("INDOOR" === sensorevent.place) {
                        lcarsControlView.updateIndoorPressure(parseInt(sensorevent.pressure));
                    }
                }
            };

            //sensordataService.getHello();


            //lcarsControlView.updateIndoorHumidity(parseInt(dht22Service.getHumidity()));
            //lcarsControlView.updateIndoorPressure(parseInt(bmp085Service.getPressure()));
            //lcarsControlView.updateLuminance("FRONT",parseInt(Math.floor(luminanceService.getLuminance("front"))));

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