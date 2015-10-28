/* global log, sensordataService, RangeEnum */

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

            $("#controlcanvas").on("graphbuttontouch", function(event) {
                handleGraphButtonEvent(event);
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

var clock = function() {
    var lcarsControlView = $("#controlcanvas").data("jLCARSControlView");
    lcarsControlView.updateClock();
};