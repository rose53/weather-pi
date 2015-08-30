/* global log */

(function($) {

    // var log = log4javascript.getLogger('init');

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

            $("#controlcanvas").on("scanbuttontouch", function(event) {
                handleScanButtonEvent(event);
            });

            $("#controlcanvas").on("soundbuttontouch", function(event) {
                handleSoundButtonEvent(event);
            });

            $("#controlcanvas").on("dht22buttontouch", function(event) {
                handleDHT22ButtonEvent(event);
            });

            $("#controlcanvas").on("bmp085buttontouch", function(event) {
                handleBMP085ButtonEvent(event);
            });

            var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
            $page.on("pageshow", function(event, ui) {
                lcarsControlView.refresh();
            });

            lcarsControlView.refresh();

            log.debug("initControlPage: using websocket at " + "ws://" + location.host + "/sensorevents");

            var sensorWebSocket = new WebSocket("ws://" + location.host + "/sensorevents");

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

            lcarsControlView.updateIndoorHumidity(parseInt(dht22Service.getHumidity()));
            lcarsControlView.updateIndoorPressure(parseInt(bmp085Service.getPressure()));
            lcarsControlView.updateLuminance("FRONT",parseInt(Math.floor(luminanceService.getLuminance("front"))));
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

var handleDHT22ButtonEvent = function(event) {
    log.debug("handleDHT22ButtonEvent: dht22buttontouch");
    var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
    lcarsControlView.updateTemperatureDHT22(dht22Service.getTemperature()
            .toFixed(1));
    lcarsControlView.updateHumidityDHT22(parseInt(dht22Service.getHumidity()));
};

var handleBMP085ButtonEvent = function(event) {
    log.debug("handleBMP085ButtonEvent: bmp085buttontouch");
    var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
    lcarsControlView.updateTemperatureBMP085(bmp085Service.getTemperature()
            .toFixed(1));
    lcarsControlView
            .updatePressureBMP085(parseInt(bmp085Service.getPressure()));
};

var handleSoundButtonEvent = function(event) {
    log.debug("handleSoundButtonEvent: soundbuttontouch");
    soundService.toggle();
};
