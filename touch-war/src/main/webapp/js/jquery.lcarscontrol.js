/* global RangeEnum, log, ForecastIconEnum, MoonPhaseIconEnum, moonPhaseImages, sunriseIconSource, sunsetIconSource, forecastImages */

/**
 *
 */

;
(function($) {

    var FRAME_INSET = 15;
    
    var days = ["Sun","Mon","Tue","Wed","Thur","Fri","Sat"];

    var colorTable = {
        header                : '#FF9933',
        text                  : '#3366CC',
        background            : '#000000',
        frame                 : '#CC99CC',
        button_normal         : '#99CCFF',
        togglebutton_selected : '#FFFF99',
        togglebutton_normal   : '#99CCFF',
        statusbutton_set      : '#FFCC66',
        statusbutton_normal   : '#99CCFF',
        forecast_max_temp     : '#FFCC66',
        forecast_min_temp     : '#3366CC'
    };
    
    var baseButton = {
        width  : 70,
        height : 30,
        space  : 5,
        labelFont      : "16pt LcarsGTJ3",
        smallLabelFont : "12pt LcarsGTJ3"
    };
    
        var button = {
            size : baseButton.height,
            radius : baseButton.height / 2,
            labelWidth : baseButton.width - baseButton.height / 2,
            textWidth : 30,
            space : baseButton.space,
            seperatorBoxWidth : baseButton.space,
            font : baseButton.labelFont
            //labeledInfoButtonWidth : baseButton.height / 2 + button.labelWidth + button.textWidth 
        };
        
    var frame = {
        smallSize : 15,
        thinSize  : 8,
        largeSize : baseButton.width
    };
    
    var lastTouch = {
        x : -1,
        y : -1
    };
    
    var lastLuminanceFront      = '0';

    var lastAnemometerWindspeed  = '0';
 
    var lastBirdhouseHumidity       = '0';
    var lastBirdhouseTemperature    = '0';
    var lastBirdhousePressure       = '0';
    
    var lastDustsensorTemperature   = '0';
    var lastDustsensorHumidity      = '0';
    var lastDustsensorPm10          = '0';
    var lastDustsensorPm25          = '0';

    var lastTime                 = new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});;
    
    
    
    
    var graphData = {
        data    : null,
        sensor  : "temperature",
        range   : RangeEnum.DAY,
        draw    : true
    };        
    
    var graphDataAverage = {
        data    : null,
        sensor  : "temperature",
        range   : RangeEnum.DAY,
        draw    : true
    };
    
    var graphData2 = {
        data    : null,
        sensor  : "humidity",
        range   : RangeEnum.DAY,
        draw    : true
    };

    var graphData2Average = {
        data    : null,
        sensor  : "humidity",
        range   : RangeEnum.DAY,
        draw    : true
    };

    var daily = [];
    var currently;
    
    var buttons = {
        birdhouseButtonRect   : {text : 'BIRDHOUSE', x : 0, y : 0, w : 0, h : 0 , color : colorTable.frame},
        anemometerButtonRect  : {text : 'WINDGAUGE', x : 0, y : 0, w : 0, h : 0 , color : colorTable.frame},
        dustsensorButtonRect  : {text : 'DUST', x : 0, y : 0, w : 0, h : 0 , color : colorTable.frame}
    };

    var graphButtonRangeGroup = {
        dayButtonRect      : {text : 'DAY',  textSize: 'small', range: RangeEnum.DAY,   x : 0, y : 0, w : frame.largeSize, h : frame.smallSize, selected : true  },
        weekButtonRect     : {text : 'WEEK', textSize: 'small', range: RangeEnum.WEEK,  x : 0, y : 0, w : frame.largeSize, h : frame.smallSize, selected : false },
        monthButtonRect    : {text : 'MONTH',textSize: 'small', range: RangeEnum.MONTH, x : 0, y : 0, w : frame.largeSize, h : frame.smallSize, selected : false }      
    };
    
    var graphButtonSensorGroup = {
        temperatureButtonRect : {text : 'TEMP.',     textSize: 'large', name: "bme280",   sensor: "temperature", place: 'birdhouse', x : 0, y : 0, w : frame.largeSize, h : baseButton.height, yAxis : 'left', selected : true, graph : true, average : true },
        humidityButtonRect    : {text : 'HUMIDITY',  textSize: 'large', name: "bme280",   sensor: "humidity",    place: 'birdhouse', x : 0, y : 0, w : frame.largeSize, h : baseButton.height, yAxis : 'left', selected : false, graph : true, average : true },
        pressureButtonRect    : {text : 'PRESSURE',  textSize: 'large', name: "bme280",   sensor: "pressure",    place: 'birdhouse', x : 0, y : 0, w : frame.largeSize, h : baseButton.height, yAxis : 'left', selected : false, graph : true, average : false },
        windspeedButtonRect   : {text : 'WINDSPEED', textSize: 'large', name: "eltako_ws",sensor: "windspeed",   place: 'anemometer', x : 0, y : 0, w : frame.largeSize, h : baseButton.height, yAxis : 'left', selected : false, graph : true, average : true },
        pm10ButtonRect        : {text : 'PM10',      textSize: 'large', name: "sds011",   sensor: "dust_pm10",   place: 'dustsensor', x : 0, y : 0, w : frame.largeSize, h : baseButton.height, yAxis : 'left', selected : false, graph : false, average : true }
    };

    var graphButtonSensorGroup2 = {
        temperatureButtonRect : {text : 'TEMP.',     textSize: 'large', name: "bme280",   sensor: "temperature", place: 'birdhouse', x : 0, y : 0, w : frame.largeSize, h : baseButton.height, yAxis : 'right', selected : false, graph : true, average : true },
        humidityButtonRect    : {text : 'HUMIDITY',  textSize: 'large', name: "bme280",   sensor: "humidity",    place: 'birdhouse', x : 0, y : 0, w : frame.largeSize, h : baseButton.height, yAxis : 'right', selected : true, graph : true, average : true },
        pressureButtonRect    : {text : 'PRESSURE',  textSize: 'large', name: "bme280",   sensor: "pressure",    place: 'birdhouse', x : 0, y : 0, w : frame.largeSize, h : baseButton.height, yAxis : 'right', selected : false, graph : true, average : false },
        windspeedButtonRect   : {text : 'WINDSPEED', textSize: 'large', name: "eltako_ws",sensor: "windspeed",   place: 'anemometer', x : 0, y : 0, w : frame.largeSize, h : baseButton.height, yAxis : 'right', selected : false, graph : true, average : true },
        pm10ButtonRect        : {text : 'PM10',      textSize: 'large', name: "sds011",   sensor: "dust_pm10",   place: 'dustsensor', x : 0, y : 0, w : frame.largeSize, h : baseButton.height, yAxis : 'right', selected : false, graph : false, average : true }
    };

    var forcastButtons = {
        currentButtonRect     : {text : 'TODAY',  x : 0, y : 0, w : 0, h : 0, color : colorTable.frame },
        nextDaysButtonRect   : {text : 'NEXT DAYS', x : 0, y : 0, w : 0, h : 0 , color : colorTable.frame}
    };

    var historyFrameRect = {
        x : 0,
        y : 0,
        w : 0,
        h : 0
    };
    var sensorFrameRect = {
        x : 0,
        y : 0,
        w : 0,
        h : 0
    };

    var forecastFrameRect = {
        x : 0,
        y : 0,
        w : 0,
        h : 0
    };



    var header = {
            size : 30,
            radius : 15,
            space : baseButton.space
        };



    // These are the default settings if none are specified.
    var settings = {
        zoomMode : 2
    //-1=Off(zoomingMode); 0=1:1; 1=FitToWidth; 2=FitToWindow; 3=FitToHeight
    };



    var jLCARSControlView = function(element, options) {
        var $element = $(element);

        var canvas = $element[0];

        $.extend(settings, options);


        this.element = element;
        this.$element = $element;
        this.canvas = canvas;

        $(window).bind('orientationchange resize', function (event) {
            window.scrollTo(1, 0);
            refresh(canvas);
            event.preventDefault();
            return false;
        });

        $element.hammer({ drag_max_touches:0}).on("touch drag", function(ev) {

            var offset = $element.offset();

            lastTouch.x       = ev.gesture.center.pageX - offset.left;
            lastTouch.y       = ev.gesture.center.pageY - offset.top;
            var bb = null;
            if ((bb = isTouchedButtonGroup(graphButtonRangeGroup)) !== null) {
                setSelected(graphButtonRangeGroup,bb,canvas); 
                $element.triggerHandler(getGraphEventData(graphButtonSensorGroup));
                $element.triggerHandler(getGraphEventData(graphButtonSensorGroup2));
            } else if ((bb = isTouchedButtonGroup(graphButtonSensorGroup)) !== null) {
                setSelected(graphButtonSensorGroup,bb,canvas); 
                $element.triggerHandler(getGraphEventData(graphButtonSensorGroup));
            } else if ((bb = isTouchedButtonGroup(graphButtonSensorGroup2)) !== null) {
                setSelected(graphButtonSensorGroup2,bb,canvas); 
                $element.triggerHandler(getGraphEventData(graphButtonSensorGroup2));
            }
            ev.gesture.preventDefault();

        });
    };

    jLCARSControlView.prototype = {

        refresh : function() {
            refresh(this.canvas);
        },

        updateBirdhousePressure : function(pressure) {
            if (pressure === lastBirdhousePressure) {
                // no change, nothing to do
                return;
            }
            lastBirdhousePressure = pressure;
            refresh(this.canvas);
        },

        updateAnemometerWindspeed : function(windspeed) {
            if (windspeed === lastAnemometerWindspeed) {
                // no change, nothing to do
                return;
            }
            lastAnemometerWindspeed = windspeed;
            refresh(this.canvas);
        },

        updateBirdhouseTemperature : function(temperature) {
            if (temperature === lastBirdhouseTemperature) {
                // no change, nothing to do
                return;
            }
            lastBirdhouseTemperature = temperature;
            refresh(this.canvas);
        },

        updateBirdhouseHumidity : function(humidity) {
            if (humidity === lastBirdhouseHumidity) {
                // no change, nothing to do
                return;
            }
            lastBirdhouseHumidity = humidity;
            refresh(this.canvas);
        },

        updateLuminance : function(location,luminance) {
            if (location.toUpperCase() === "FRONT") {
                if (luminance !== lastLuminanceFront) {
                    lastLuminanceFront = luminance;
                    refresh(this.canvas);
                }    
            } 
        },
        
        updateDustsensorTemperature : function(temperature) {
            if (temperature === lastDustsensorTemperature) {
                // no change, nothing to do
                return;
            }
            lastDustsensorTemperature = temperature;
            refresh(this.canvas);
        },
        
        updateDustsensorHumidity : function(humidity) {
            if (humidity === lastDustsensorHumidity) {
                // no change, nothing to do
                return;
            }
            lastDustsensorHumidity = humidity;
            refresh(this.canvas);
        },
        
        updateDustsensorPm10 : function(pm10) {
            if (pm10 === lastDustsensorPm10) {
                // no change, nothing to do
                return;
            }
            lastDustsensorPm10 = pm10;
            refresh(this.canvas);
        },
 
        updateDustsensorPm25 : function(pm25) {
            if (pm25 === lastDustsensorPm25) {
                // no change, nothing to do
                return;
            }
            lastDustsensorPm25 = pm25;
            refresh(this.canvas);
        },
        
        updateClock : function() {
            
            var tt = new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
            
            if (tt.length > 5) {
                tt = tt.substring(0,5);
                
            }
            
            if (tt !== lastTime) {
                lastTime = tt;
                refresh(this.canvas);
            }    
        },
        
        getGraphDataParams : function() {
            return getGraphEventData(graphButtonSensorGroup);  
        },
        
        getGraphData2Params : function() {
            return getGraphEventData(graphButtonSensorGroup2);  
        },
               
        updateGraphData : function(data, sensor, range, draw) {
            updateGraphData(graphData, data, sensor, range, draw);
        },
        
        updateGraphData2 : function(data, sensor, range, draw) {
            updateGraphData(graphData2, data, sensor, range, draw);
            refresh(this.canvas);
        },
        
        updateGraphDataAverage : function(data, sensor, range, draw) {
            updateGraphData(graphDataAverage, data, sensor, range, draw);
        },
        
        updateGraphData2Average : function(data, sensor, range, draw) {
            updateGraphData(graphData2Average, data, sensor, range, draw);
        },
        
        getMaxGraphData : function() {
            return historyFrameRect.w;
        },
        
        updateDailyForecast: function(dailyData) {
            daily = dailyData;            
            refresh(this.canvas);
        },
        
        updateCurrentlyForecast: function(currentlyData) {
            currently = currentlyData;            
            refresh(this.canvas);
        }
    };

    function updateGraphData(gData, data, sensor, range, draw) {
        gData.data = data;
        if (gData.data !== null) {
            gData.data.sensorData.sort(function(a,b){
                return new Date(b.time) - new Date(a.time);
            });
        }
        gData.sensor = sensor;
        gData.range  = range;
        gData.draw   = draw;
        refresh(this.canvas);  
    }

    function isTouched(button) {
        
        if (   lastTouch.x > button.x && lastTouch.x < (button.x + button.w)
            && lastTouch.y > button.y && lastTouch.y < (button.y + button.h)) {
            return true;
        } else {
            return false;
        }
    }

    function isTouchedButtonGroup(buttonGroup) {
        for (var name in buttonGroup) {
            if (isTouched(buttonGroup[name])){
                return buttonGroup[name];
            }
        } 
        return null;
    }
    
    function getGraphEventData(buttonGroup) {
        
        var range;
        for (var name in graphButtonRangeGroup) {
            if (graphButtonRangeGroup[name].selected){
                range = graphButtonRangeGroup[name].range;
            }
        } 

        var place;        
        var sensor;
        var sensorName;
        var graph;
        var average;
        var yAxis;
        for (var name in buttonGroup) {
            if (buttonGroup[name].selected){
                sensor = buttonGroup[name].sensor;
                sensorName = buttonGroup[name].name;
                place = buttonGroup[name].place;
                graph = buttonGroup[name].graph;
                average = buttonGroup[name].average;
                yAxis = buttonGroup[name].yAxis;
            }
        } 
        
        return {    
            type    : "graphbuttontouch",
            name    : sensorName,
            range   : range,
            place   : place,
            sensor  : sensor,
            graph   : graph,
            average : average,
            yAxis   : yAxis
        };
    }
    
    function refresh(canvas) {

        if (!canvas) {
            return;
        }
 
        $(canvas).attr({
            width: $(window).innerWidth(),
            height: $(window).innerHeight()
        });

        redraw(canvas);
    }

    function redraw(canvas) {

        if (!canvas) {
            return;
        }

        var ctx = canvas.getContext('2d');

        var windowWidth = canvas.width;
        var windowHeight = canvas.height;

        historyFrameRect.x = FRAME_INSET;
        historyFrameRect.y = header.size + 2 * baseButton.space;
        historyFrameRect.w = windowWidth - 2 * FRAME_INSET;
        historyFrameRect.h = 210;

        sensorFrameRect.x = FRAME_INSET;
        sensorFrameRect.y = historyFrameRect.y + historyFrameRect.h + baseButton.space;
        sensorFrameRect.w = 0.64 * windowWidth;
        sensorFrameRect.h = windowHeight - historyFrameRect.y - historyFrameRect.h - 2 * baseButton.space;

        forecastFrameRect.x = sensorFrameRect.w + baseButton.space + sensorFrameRect.x;
        forecastFrameRect.y = historyFrameRect.y + historyFrameRect.h + baseButton.space;
        forecastFrameRect.w = windowWidth - sensorFrameRect.w - baseButton.space - 2 * FRAME_INSET;
        forecastFrameRect.h = sensorFrameRect.h;

        var baseButtonPosX = forecastFrameRect.x + frame.largeSize + 30 + baseButton.space;
            
        ctx.clearRect(0, 0, windowWidth, windowHeight);

        drawHeader(ctx,FRAME_INSET,baseButton.space,windowWidth - FRAME_INSET);
        drawHistoryFrame(ctx,historyFrameRect.x,historyFrameRect.y,historyFrameRect.w,historyFrameRect.h,baseButtonPosX);
        drawActSensorDataFrame(ctx,sensorFrameRect.x,sensorFrameRect.y,sensorFrameRect.w,sensorFrameRect.h);
        drawForecastFrame(ctx,forecastFrameRect.x,forecastFrameRect.y,forecastFrameRect.w,forecastFrameRect.h,baseButtonPosX);
    }


    function drawHeader(ctx,x,y,w) {
        ctx.save();

        ctx.fillStyle = colorTable.header;
        
        ctx.beginPath();
        ctx.arc(x + header.radius, header.radius + baseButton.space, header.radius, Math.radians(90), Math.radians(270), false);
        ctx.moveTo(x + header.radius,y);
        ctx.lineTo(x + header.radius + header.size,y);
        ctx.lineTo(x + header.radius + header.size,y + header.size);
        ctx.lineTo(x + header.radius,y + header.size);
        ctx.fill();
        ctx.closePath();
        
        ctx.fillStyle = "#3366CC";
        ctx.font      = "30pt LcarsGTJ3";
        var metrics = ctx.measureText("WeatherPi");
        var width = metrics.width;
        ctx.fillText("WeatherPi",x + w - 2 * header.radius - header.size - width - header.space, y + header.size);

        ctx.fillStyle = "#FF9933";

        ctx.fillRect(x + header.radius + header.size + header.space, y, w - 3 * header.radius - 2*  header.size - width - 3* header.space , header.size);

        ctx.beginPath();
        ctx.arc(x + w - 2 * header.radius, header.radius + baseButton.space, header.radius, Math.radians(90), Math.radians(270), true);
        ctx.moveTo(x + w - 2 * header.radius - header.size,y);
        ctx.lineTo(x + w - 2 * header.radius,y);
        ctx.lineTo(x + w - 2 * header.radius,y + header.size);
        ctx.lineTo(x + w - 2 * header.radius - header.size,y + header.size);
        ctx.fill();
        ctx.closePath();

        // Draw label text
        ctx.font      = "20pt LcarsGTJ3";
        ctx.fillStyle = colorTable.background;
        ctx.textBaseline = "middle";
                
        ctx.fillText(lastTime, x + frame.largeSize + header.space, y + header.size / 2);
        ctx.restore();
    }


    function drawHistoryFrame(ctx,x,y,w,h,baseButtonPosX) {

        ctx.save();

        ctx.fillStyle = colorTable.frame;

        ctx.beginPath();
        ctx.moveTo(x ,y);
        ctx.lineTo(x,y + h - (frame.smallSize + baseButton.height)  );
        ctx.quadraticCurveTo(x,y + h,x + (frame.smallSize + baseButton.height),y + h);
        ctx.lineTo(x + w,y + h);
        ctx.lineTo(x + w,y + h - frame.smallSize);
        ctx.lineTo(x + frame.largeSize + baseButton.height,y + h - frame.smallSize);
        ctx.quadraticCurveTo(x + frame.largeSize,y + h - frame.smallSize,x + frame.largeSize,y + h - frame.smallSize - baseButton.height);
        ctx.lineTo(x + frame.largeSize,y);

        ctx.fill();
        ctx.closePath();
        
        // image left
        var leftBarX = x + frame.largeSize + 3 * baseButton.space;
        var leftBarY = y + baseButton.space;
        var leftBarH = h - 2 * baseButton.space - frame.smallSize; 
                
        ctx.fillStyle = '#ffff99';

        ctx.beginPath();
        ctx.moveTo(leftBarX + frame.smallSize ,leftBarY);
        ctx.lineTo(leftBarX + frame.thinSize,leftBarY);
        ctx.quadraticCurveTo(leftBarX,leftBarY,leftBarX,leftBarY + frame.thinSize);
        ctx.lineTo(leftBarX,leftBarY + leftBarH - frame.thinSize);
        ctx.quadraticCurveTo(leftBarX,leftBarY + leftBarH,leftBarX + frame.thinSize,leftBarY + leftBarH);
        ctx.lineTo(leftBarX + frame.thinSize,leftBarY + leftBarH);
        ctx.lineTo(leftBarX + frame.smallSize ,leftBarY + leftBarH);
        ctx.lineTo(leftBarX + frame.smallSize ,leftBarY + leftBarH - frame.thinSize);
        ctx.lineTo(leftBarX + frame.thinSize,leftBarY + leftBarH - frame.thinSize);
        ctx.lineTo(leftBarX + frame.thinSize,leftBarY +  frame.thinSize);
        ctx.lineTo(leftBarX + frame.smallSize,leftBarY + frame.thinSize);

        ctx.fill();
        ctx.closePath();
        
        //drawButtonVerticalGap(ctx,baseButtonPosX - baseButton.space,y + h - frame.smallSize,frame.smallSize);
        
        // image right
        var rightBarX = x + w - frame.largeSize - baseButton.space;
        
        ctx.beginPath();
        ctx.moveTo(rightBarX - frame.smallSize ,leftBarY);
        ctx.lineTo(rightBarX - frame.smallSize +  frame.thinSize,leftBarY);
        ctx.quadraticCurveTo(rightBarX,leftBarY,rightBarX,leftBarY + frame.thinSize);
        ctx.lineTo(rightBarX, leftBarY + leftBarH - frame.thinSize);
        ctx.quadraticCurveTo(rightBarX,leftBarY + leftBarH,rightBarX - frame.thinSize,leftBarY + leftBarH);
        ctx.lineTo(rightBarX - frame.smallSize , leftBarY + leftBarH);
        ctx.lineTo(rightBarX - frame.smallSize , leftBarY + leftBarH - frame.thinSize);
        ctx.lineTo(rightBarX - frame.thinSize , leftBarY + leftBarH - frame.thinSize);
        ctx.lineTo(rightBarX - frame.thinSize , leftBarY + frame.thinSize);
        ctx.lineTo(rightBarX - frame.smallSize , leftBarY + frame.thinSize);

        ctx.fill();
        ctx.closePath();

        //var graphData = jQuery.parseJSON(testData);
        drawGraphArea(ctx,leftBarX + 3 * baseButton.space + frame.smallSize,leftBarY,rightBarX - leftBarX - 6 * baseButton.space - 2* frame.smallSize, leftBarH,graphData);
        /////////
        
        // draw range buttons
        
        var rangeButtonPosX = x + w - frame.largeSize;
        var rangeButtonPosY = y + h - frame.smallSize;
        
        graphButtonRangeGroup.dayButtonRect.x = rangeButtonPosX;
        graphButtonRangeGroup.dayButtonRect.y = rangeButtonPosY;

        drawButtonVerticalGap(ctx,rangeButtonPosX - baseButton.space,rangeButtonPosY,frame.smallSize); 

        rangeButtonPosX = rangeButtonPosX - frame.largeSize - baseButton.space;
        
        graphButtonRangeGroup.weekButtonRect.x = rangeButtonPosX;
        graphButtonRangeGroup.weekButtonRect.y = rangeButtonPosY;
        
        drawButtonVerticalGap(ctx,rangeButtonPosX - baseButton.space,rangeButtonPosY,frame.smallSize); 
        
        rangeButtonPosX = rangeButtonPosX - frame.largeSize - baseButton.space;
        
        graphButtonRangeGroup.monthButtonRect.x = rangeButtonPosX;
        graphButtonRangeGroup.monthButtonRect.y = rangeButtonPosY;
        
        drawButtonVerticalGap(ctx,rangeButtonPosX - baseButton.space,rangeButtonPosY,frame.smallSize); 
        
        var buttonGapY = y + baseButton.height;
        var buttonPosX = x;
        var button2PosX = x + w - frame.largeSize;
        var buttonPosY = y;        

        graphButtonSensorGroup.temperatureButtonRect.x = buttonPosX;
        graphButtonSensorGroup.temperatureButtonRect.y = buttonPosY;
        
        graphButtonSensorGroup2.temperatureButtonRect.x = button2PosX;
        graphButtonSensorGroup2.temperatureButtonRect.y = buttonPosY;
 
        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);

        buttonPosY = buttonGapY + baseButton.space;

        graphButtonSensorGroup.pressureButtonRect.x = buttonPosX;
        graphButtonSensorGroup.pressureButtonRect.y = buttonPosY;
        
        graphButtonSensorGroup2.pressureButtonRect.x = button2PosX;
        graphButtonSensorGroup2.pressureButtonRect.y = buttonPosY;
 
        buttonGapY = buttonGapY + baseButton.height + baseButton.space;

        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);
        
        buttonPosY = buttonGapY + baseButton.space;

        graphButtonSensorGroup.humidityButtonRect.x = buttonPosX;
        graphButtonSensorGroup.humidityButtonRect.y = buttonPosY;
        
        graphButtonSensorGroup2.humidityButtonRect.x = button2PosX;
        graphButtonSensorGroup2.humidityButtonRect.y = buttonPosY;

        buttonGapY = buttonGapY + baseButton.height + baseButton.space;

        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);
        
        buttonPosY = buttonGapY + baseButton.space;
        
        graphButtonSensorGroup.windspeedButtonRect.x = buttonPosX;
        graphButtonSensorGroup.windspeedButtonRect.y = buttonPosY;
        
        graphButtonSensorGroup2.windspeedButtonRect.x = button2PosX;
        graphButtonSensorGroup2.windspeedButtonRect.y = buttonPosY;

        buttonGapY = buttonGapY + baseButton.height + baseButton.space;

        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);
        
        buttonPosY = buttonGapY + baseButton.space;
        
        graphButtonSensorGroup.pm10ButtonRect.x = buttonPosX;
        graphButtonSensorGroup.pm10ButtonRect.y = buttonPosY;
        
        graphButtonSensorGroup2.pm10ButtonRect.x = button2PosX;
        graphButtonSensorGroup2.pm10ButtonRect.y = buttonPosY;
        
        buttonGapY = buttonGapY + baseButton.height + baseButton.space;

        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);
        
        buttonPosY = buttonGapY + baseButton.space;
        
        // draw the button groups
        drawToggleButtonGroup(ctx,graphButtonRangeGroup);
        drawToggleButtonGroup(ctx,graphButtonSensorGroup);
        drawToggleButtonGroup(ctx,graphButtonSensorGroup2);
        
        ctx.restore();
    }

    function drawForecastFrame(ctx,x,y,w,h,baseButtonPosX) {
        ctx.save();

        ctx.fillStyle = colorTable.frame;
        ctx.beginPath();
        ctx.moveTo(x + w,y);
        ctx.lineTo(x  +  (frame.smallSize + baseButton.height),y);
        //ctx.lineTo(x,y +  (frame.smallSize + baseButton.height));
        ctx.quadraticCurveTo(x,y,x,y + (frame.smallSize + baseButton.height));
        ctx.lineTo(x,y + h - (frame.smallSize + baseButton.height));
        ctx.quadraticCurveTo(x,y + h,x + (frame.smallSize + baseButton.height),y + h);
        //ctx.lineTo(x + (frame.smallSize + baseButton.height),y + h);
        ctx.lineTo(x + w,y + h);
        ctx.lineTo(x + w,y + h - frame.smallSize);
        ctx.lineTo(x + frame.largeSize + baseButton.height,y + h - frame.smallSize);
        ctx.quadraticCurveTo(x + frame.largeSize,y + h - frame.smallSize,x + frame.largeSize,y + h - frame.smallSize - baseButton.height);
        ctx.lineTo(x + frame.largeSize,y + frame.smallSize + baseButton.height);
        ctx.quadraticCurveTo(x + frame.largeSize,y + frame.smallSize,x + frame.largeSize + baseButton.height,y + frame.smallSize);
        ctx.lineTo(x + w,y + frame.smallSize);

        ctx.fill();
        ctx.closePath();

        var buttonGapY = y + (frame.smallSize + baseButton.height - baseButton.space);
        var buttonPosX = x;
        //var infoButtonPosX = buttonPosX - getLabeledInfoButtonWidth() - button.space;
        var buttonPosY = buttonGapY + baseButton.space;

        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);

        forcastButtons.currentButtonRect.x = buttonPosX;
        forcastButtons.currentButtonRect.y = buttonPosY;
        forcastButtons.currentButtonRect.w = frame.largeSize;
        forcastButtons.currentButtonRect.h = baseButton.height + baseButton.space;

        drawButton(ctx,forcastButtons.currentButtonRect);

        buttonGapY = buttonGapY + baseButton.height + baseButton.space;
        
        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);
        
        if (daily.length >= 1) {
            var dBoxX = forcastButtons.currentButtonRect.x + forcastButtons.currentButtonRect.w + baseButton.space;
            var dBoxY = forcastButtons.currentButtonRect.y;
            var dBoxW = w - frame.largeSize - baseButton.space;

            ctx.beginPath();

            ctx.save();
            ctx.textBaseline = "middle";
            ctx.font         = "12pt LcarsGTJ3";
            ctx.fillStyle    = colorTable.forecast_max_temp;
            ctx.textAlign    = "center";
            var sunriseTimeStr = daily[0].sunriseTime;
            var sunsetTimeStr  = daily[0].sunsetTime;
            
            ctx.fillText(sunriseTimeStr.substring(11,16),dBoxX + 1.5 * baseButton.height / 2, dBoxY + 30);
            ctx.fillText(sunsetTimeStr.substring(11,16),dBoxX + 1.5 * baseButton.height + 1.5 * baseButton.height / 2 , dBoxY + 30);
            ctx.restore();

            ctx.drawImage(sunriseIconSource,dBoxX + 1.5 * baseButton.height / 4,dBoxY - 3,0.9 * baseButton.height,0.9 * baseButton.height);
            ctx.drawImage(sunsetIconSource,dBoxX + 1.5 * baseButton.height + 1.5 * baseButton.height / 4,dBoxY - 3,0.9 * baseButton.height,0.9 * baseButton.height);
            ctx.stroke();
        }
        buttonPosY = buttonPosY + baseButton.height + baseButton.space;

        forcastButtons.nextDaysButtonRect.x = buttonPosX;
        forcastButtons.nextDaysButtonRect.y = buttonPosY;
        forcastButtons.nextDaysButtonRect.w = frame.largeSize;
        forcastButtons.nextDaysButtonRect.h = 3 * baseButton.height + 2 * baseButton.space;

        drawButton(ctx,forcastButtons.nextDaysButtonRect);
        
        buttonGapY = buttonGapY + 3 * baseButton.height + 3 * baseButton.space;
        
        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);
        
        var fBoxX = forcastButtons.nextDaysButtonRect.x + forcastButtons.nextDaysButtonRect.w + baseButton.space;
        var fBoxY = forcastButtons.nextDaysButtonRect.y + baseButton.space;
        var fBoxW = w - frame.largeSize - baseButton.space;
        
        ctx.beginPath();
        
        ctx.textBaseline = "middle";
        ctx.font         = "12pt LcarsGTJ3";
         
        var fontHeight = 12;
        
        var dayBoxWidth = fBoxW / 3;
        for (var i = 0; i < 3; i++) {
            if (daily.length >= 8) {
                ctx.save();
                var time = new Date(daily[i+1].time);
                ctx.fillStyle    = '#ffff99';
                ctx.textAlign    = "left";
                ctx.fillText(days[time.getDay()] + ' ' + time.getDate() + '.' + (time.getMonth() + 1),fBoxX + i * dayBoxWidth + 2, fBoxY + fontHeight / 2);
                ctx.restore();
                
                ctx.drawImage(forecastImages[ForecastIconEnum.getForecastIconEnumForName(daily[i+1].icon) - 1],fBoxX + i * dayBoxWidth,fBoxY - 10 + fontHeight, dayBoxWidth, dayBoxWidth);

                ctx.save();
                ctx.fillStyle    = colorTable.forecast_max_temp;
                ctx.textAlign    = "left";
                var metrics = ctx.measureText(daily[i+1].temperatureMax.toFixed(1));
                ctx.fillText(daily[i+1].temperatureMax.toFixed(1),fBoxX + i * dayBoxWidth + 2, fBoxY + dayBoxWidth - 10 + fontHeight);
                
                //var width = metrics.width;
                ctx.fillStyle    = colorTable.forecast_min_temp;
                ctx.fillText(daily[i+1].temperatureMin.toFixed(1),fBoxX + i * dayBoxWidth + metrics.width + 5, fBoxY + dayBoxWidth - 10 + fontHeight);
                

                ctx.fillStyle    = colorTable.forecast_max_temp;
                ctx.textAlign    = "left";
                metrics = ctx.measureText(parseInt(daily[i+1].pressure));
                ctx.fillText(parseInt(daily[i+1].pressure),fBoxX + i * dayBoxWidth + 2, fBoxY + dayBoxWidth - 10 + 2  * fontHeight + 2);
                
                //var width = metrics.width;
                ctx.textAlign    = "left";
                ctx.fillText(parseInt(100 * daily[i+1].humidity),fBoxX + i * dayBoxWidth + metrics.width + 10, fBoxY + dayBoxWidth - 10 + 2 * fontHeight + 2);

                ctx.restore();
                
                ctx.drawImage(moonPhaseImages[MoonPhaseIconEnum.getPhaseIconEnum(daily[i+1].moonPhase) - 1],fBoxX + i * dayBoxWidth ,fBoxY + dayBoxWidth - 20 + 2 * fontHeight,dayBoxWidth, dayBoxWidth);
            }
        }
        
        ctx.lineWidth = 1;
        ctx.strokeStyle = '#FF0000';
        ctx.stroke();
 
        ctx.restore();
    }


    function drawActSensorDataFrame(ctx,x,y,w,h) {
        ctx.save();
        ctx.fillStyle = colorTable.frame;
        ctx.beginPath();
        ctx.moveTo(x,y);

        ctx.lineTo(x + w - (frame.smallSize + baseButton.height),y);
        ctx.quadraticCurveTo(x + w,y,x + w,y + (frame.smallSize + baseButton.height));

        ctx.lineTo(x + w,y + h - (frame.smallSize + baseButton.height));

        ctx.quadraticCurveTo(x + w,y + h,x + w - (frame.smallSize + baseButton.height),y + h);
        ctx.lineTo(x,y + h);
        ctx.lineTo(x,y + h - frame.smallSize);
        ctx.lineTo(x + w - frame.largeSize - baseButton.height,y + h - frame.smallSize);
 
        ctx.quadraticCurveTo(x + w - frame.largeSize,y + h - frame.smallSize,x + w - frame.largeSize,y + h - frame.smallSize - baseButton.height);
        ctx.lineTo(x + w - frame.largeSize,y + frame.smallSize + baseButton.height);
        ctx.quadraticCurveTo(x + w - frame.largeSize,y + frame.smallSize,x + w - frame.largeSize - baseButton.height,y + frame.smallSize);
        ctx.lineTo(x ,y +  frame.smallSize);

        ctx.fill();

        ctx.closePath();
        
        var buttonGapY = y + (frame.smallSize + baseButton.height- baseButton.space);
        var buttonPosX = x + w - frame.largeSize;
        var infoButtonPosX = buttonPosX - getLabeledInfoButtonWidth() - button.space;
        var buttonPosY = buttonGapY + baseButton.space;

        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);

        buttons.birdhouseButtonRect.x = buttonPosX;
        buttons.birdhouseButtonRect.y = buttonPosY;
        buttons.birdhouseButtonRect.w = frame.largeSize;
        buttons.birdhouseButtonRect.h = baseButton.height + baseButton.space;

        drawButton(ctx,buttons.birdhouseButtonRect);
        
        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"PRESSURE",lastBirdhousePressure,"#FFCC66");
        drawLabeledInfoButton(ctx,infoButtonPosX - (getLabeledInfoButtonWidth() + button.space),buttonPosY,"HUMIDITY",lastBirdhouseHumidity,"#FFCC66");
        drawLabeledInfoButton(ctx,infoButtonPosX - 2 *(getLabeledInfoButtonWidth() + button.space),buttonPosY,"TEMP.",lastBirdhouseTemperature,"#FFCC66");
        
        /*
        buttonPosY = buttonPosY + baseButton.height + baseButton.space;
        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"HUMIDITY",lastBirdhouseHumidity,"#FFCC66");

        buttonPosY = buttonPosY + baseButton.height + baseButton.space;
        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"PRESSURE",lastBirdhousePressure,"#FFCC66");
        */

        buttonGapY = buttonGapY + baseButton.height + baseButton.space;
        
        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);
        buttonPosY = buttonGapY + baseButton.space;
        
        buttons.anemometerButtonRect.x = buttonPosX;
        buttons.anemometerButtonRect.y = buttonPosY;
        buttons.anemometerButtonRect.w = frame.largeSize;
        buttons.anemometerButtonRect.h = baseButton.height;

        drawButton(ctx,buttons.anemometerButtonRect);
        
        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"WS",lastAnemometerWindspeed,"#3366CC");
        
        buttonGapY = buttonGapY + baseButton.height + baseButton.space;
        
        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);
        
        buttonPosY = buttonGapY + baseButton.space;
        
        buttons.dustsensorButtonRect.x = buttonPosX;
        buttons.dustsensorButtonRect.y = buttonPosY;
        buttons.dustsensorButtonRect.w = frame.largeSize;
        buttons.dustsensorButtonRect.h = baseButton.height;

        drawButton(ctx,buttons.dustsensorButtonRect);
        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"PM2.5",lastDustsensorPm25,"#FFCC66");
        drawLabeledInfoButton(ctx,infoButtonPosX - (getLabeledInfoButtonWidth() + button.space),buttonPosY,"PM10",lastDustsensorPm10,"#FFCC66");
        
        buttonPosY = buttonPosY + baseButton.height + baseButton.space;
        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"HUMIDITY",lastDustsensorHumidity,"#FFCC66");
        drawLabeledInfoButton(ctx,infoButtonPosX - (getLabeledInfoButtonWidth() + button.space),buttonPosY,"TEMP.",lastDustsensorTemperature,"#FFCC66");

        buttonGapY = buttonGapY + 2 * baseButton.height + 2 * baseButton.space;
        
        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);

        ctx.restore(); 
    }
    
 

    function drawLabeledInfoButton(ctx,x,y,labelText,infoText,labelColor) {
        ctx.save();
        ctx.textBaseline = "middle";
        var leftHalfCirclePosX = x + button.radius;

        ctx.fillStyle = labelColor;
        ctx.beginPath();
        ctx.arc(leftHalfCirclePosX, y + button.radius, button.radius, Math.radians(90), Math.radians(270), false);
        ctx.moveTo(leftHalfCirclePosX,y);
        ctx.lineTo(leftHalfCirclePosX + button.labelWidth,y);
        ctx.lineTo(leftHalfCirclePosX + button.labelWidth,y + button.size);
        ctx.lineTo(leftHalfCirclePosX,y + button.size);
        ctx.fill();
        ctx.closePath();
        
        // Draw label text
        ctx.font      = button.font;
        ctx.fillStyle = colorTable.background;
        ctx.textAlign = "left";
        ctx.fillText(labelText, leftHalfCirclePosX, y + button.size / 2);

        ctx.fillStyle = labelColor;

        // draw small seperator box
        var seperatorBoxPosX = leftHalfCirclePosX + button.labelWidth + baseButton.space;
        
        ctx.beginPath();
        ctx.moveTo(seperatorBoxPosX,y);
        ctx.lineTo(seperatorBoxPosX + button.seperatorBoxWidth,y);
        ctx.lineTo(seperatorBoxPosX + button.seperatorBoxWidth,y + button.size);
        ctx.lineTo(seperatorBoxPosX,y + button.size);
        ctx.fill();
        ctx.closePath();

        // Draw info text
        var infoBoxPosX = seperatorBoxPosX + button.seperatorBoxWidth + baseButton.space;
        
        ctx.font         = "20pt LcarsGTJ3";
        ctx.textAlign    = "right";
        
        ctx.fillText(infoText, infoBoxPosX + button.textWidth, y + button.size / 2);

        var rightHalfCirclePosX = infoBoxPosX + button.textWidth + baseButton.space;
        
        ctx.beginPath();
        ctx.arc(rightHalfCirclePosX + baseButton.space, y + button.radius, button.radius, Math.radians(90), Math.radians(270), true);
        ctx.moveTo(rightHalfCirclePosX ,y);
        ctx.lineTo(rightHalfCirclePosX  + baseButton.space,y);
        ctx.lineTo(rightHalfCirclePosX  + baseButton.space,y + button.size);
        ctx.lineTo(rightHalfCirclePosX ,y + button.size);
        ctx.fill();
        ctx.closePath();
        
        ctx.restore();
        
        
        return rightHalfCirclePosX + baseButton.space + button.radius - x;
    }

    function drawStatusButton(ctx,x,y,statusText,status) {
        ctx.save();
        ctx.textBaseline = "middle";
        var leftHalfCirclePosX  = x + button.radius;
        var rightHalfCirclePosX = leftHalfCirclePosX + button.labelWidth + 3 * baseButton.space + button.seperatorBoxWidth + button.textWidth + baseButton.space;

        if (status) {
            ctx.fillStyle = colorTable.statusbutton_set;
        } else {
            ctx.fillStyle = colorTable.statusbutton_normal;
        }
        
        ctx.beginPath();
        ctx.arc(leftHalfCirclePosX, y + button.radius, button.radius, Math.radians(90), Math.radians(270), false);
        ctx.moveTo(leftHalfCirclePosX,y);
        ctx.lineTo(rightHalfCirclePosX,y);
        ctx.lineTo(rightHalfCirclePosX,y + button.size);
        ctx.lineTo(leftHalfCirclePosX,y + button.size);       
        ctx.fill();
        ctx.closePath();
        ctx.beginPath();
        ctx.arc(rightHalfCirclePosX - 1, y + button.radius, button.radius, Math.radians(90), Math.radians(270), true);
        ctx.fill();
        ctx.closePath();
        
        // Draw label text
        ctx.font      = button.font;
        ctx.fillStyle = colorTable.background;
        ctx.textAlign = "center";
        
        ctx.fillText(statusText, leftHalfCirclePosX+ rightHalfCirclePosX/2 - leftHalfCirclePosX /2, y + button.size / 2);

        ctx.restore();
        return rightHalfCirclePosX + baseButton.space + button.radius - x;
    }

    function drawButton(ctx,button) {
        ctx.save();

        ctx.beginPath();
        ctx.rect(button.x,button.y,button.w,button.h);
        ctx.fillStyle   = button.color;
        ctx.strokeStyle = button.color;
        ctx.fill();
        ctx.lineWidth = 1;
        ctx.stroke();

        ctx.font         = baseButton.labelFont;
        ctx.textAlign    = "right";
        ctx.textBaseline = "top";
        ctx.fillStyle    = colorTable.background;
        ctx.fillText(button.text,button.x + frame.largeSize - baseButton.space,button.y + baseButton.space);
        ctx.restore();
    }
    
    function drawToggleButton(ctx,button,small) {
        ctx.save();

        ctx.beginPath();
        ctx.rect(button.x,button.y,button.w,button.h);
        if (button.selected) {
            ctx.fillStyle = colorTable.togglebutton_selected;
            ctx.strokeStyle = colorTable.togglebutton_selected;
        } else {
            ctx.fillStyle = colorTable.togglebutton_normal;
            ctx.strokeStyle = colorTable.togglebutton_normal;
        }
        ctx.fill();
        ctx.lineWidth = 1;
        ctx.stroke();
        ctx.fillStyle    = colorTable.background;
        if (!small) {
            ctx.font         = baseButton.labelFont;
            ctx.textAlign    = "right";
            ctx.textBaseline = "middle";
            ctx.fillText(button.text,button.x  + button.w - baseButton.space ,button.y + button.h / 2);
        } else {
            ctx.font         = baseButton.smallLabelFont;
            ctx.textAlign    = "center";
            ctx.textBaseline = "middle";   
            ctx.fillText(button.text,button.x  + button.w /2 ,button.y + button.h / 2);
        }
        
        
        ctx.restore();
    }
    
    function drawToggleButtonGroup(ctx,buttonGroup) {
        for (var name in buttonGroup) {
            drawToggleButton(ctx,buttonGroup[name],buttonGroup[name].textSize === 'small');
        } 
    }
    
    function drawButtonHorizontalGap(ctx,x,y,w) {
        ctx.save();

        ctx.beginPath();
        ctx.rect(x,y,w /* + 100*/ ,baseButton.space);
        ctx.fillStyle = colorTable.background;
        ctx.fill();
        ctx.lineWidth = 1;
        ctx.strokeStyle = colorTable.background; //colorTable.background; // '#ff0000'; for debugging
        ctx.stroke();

        ctx.restore();
    }
    
    function drawButtonVerticalGap(ctx,x,y,h) {
        ctx.save();

        ctx.beginPath();
        ctx.rect(x,y,baseButton.space, h /* + 100*/);
        ctx.fillStyle = colorTable.background;
        ctx.fill();
        ctx.lineWidth = 1;
        ctx.strokeStyle = colorTable.background; //colorTable.background; // '#ff0000'; for debugging
        ctx.stroke();

        ctx.restore();
    }

    function getLabeledInfoButtonWidth() {
        return 2 * button.radius + button.labelWidth + 4 * baseButton.space + button.seperatorBoxWidth + button.textWidth;
    }

    function getStatusButtonWidth() {
        return 2 * button.radius + button.labelWidth + 5 * baseButton.space + button.seperatorBoxWidth + button.textWidth;
    }

    function drawGraphArea(ctx,x,y,w,h) {
                    
        ctx.save();
        
        var step = Math.floor(h / 10);
        
        var width  = step * Math.floor(w/step);
        var height = step * Math.floor(h/step) ;
        
        var xZeroMS  = x + w / 2 + width / 2;
        var xMinMS   = xZeroMS - width;
        var yZero    = y + h / 2 + height / 2;
        var yMax     = yZero - height;
                        
        ctx.beginPath();        
        for (var yAxis = 0; yAxis <= w / 2; yAxis += step) {
            ctx.moveTo(x + w / 2 + yAxis,y);
            ctx.lineTo(x + w / 2 + yAxis,y + h);
            if (yAxis !== 0) {
                ctx.moveTo(x + w / 2 - yAxis,y);
                ctx.lineTo(x + w / 2 - yAxis,y + h);
            }
        }
        
        for (var xAxis = 0; xAxis <= h / 2; xAxis += step) {
            ctx.moveTo(x,  y + h / 2 + xAxis);
            ctx.lineTo(x + w, y + h / 2 + xAxis);
            if (xAxis !== 0) {
                ctx.moveTo(x,  y + h / 2 - xAxis);
                ctx.lineTo(x + w, y + h / 2 - xAxis);
            }
        }
        ctx.lineWidth = 1;
        ctx.strokeStyle = '#006699';
        ctx.stroke();
        
        if (   ((graphData.data === null || graphData.data.sensordata === null) && graphData.draw)
            || ((graphDataAverage.data === null || graphDataAverage.data.sensordata === null) && graphDataAverage.draw)
            || ((graphData2.data === null || graphData2.data.sensordata === null) && graphData2.draw)
            || ((graphData2Average.data === null || graphData2Average.data.sensordata === null) && graphData2Average.draw)) {
            return;
        }
        
        var maxValue;
        var minValue;
        if (graphData.draw && graphDataAverage.draw) {
            maxValue = Math.max(graphData.data.maxValue,graphDataAverage.data.maxValue);
            minValue = Math.min(graphData.data.minValue,graphDataAverage.data.minValue);            
        } else if (graphData.draw && !graphDataAverage.draw) {
            maxValue = graphData.data.maxValue;
            minValue = graphData.data.minValue;
        } else if (!graphData.draw && graphDataAverage.draw) {
            maxValue = graphDataAverage.data.maxValue;
            minValue = graphDataAverage.data.minValue;
        }
        
        
        if (graphData.draw) {
            drawGraph(ctx,width,height,xMinMS,xZeroMS,yZero,yMax,step,graphData,maxValue,minValue,true,'#FF9933');
        }
        if (graphDataAverage.draw) {
            drawGraph(ctx,width,height,xMinMS,xZeroMS,yZero,yMax,step,graphDataAverage,maxValue,minValue,true,'#99CCFF');
        }
  
        if (graphData2.draw && graphData2Average.draw) {
            maxValue = Math.max(graphData2.data.maxValue,graphData2Average.data.maxValue);
            minValue = Math.min(graphData2.data.minValue,graphData2Average.data.minValue);            
        } else if (graphData2.draw && !graphData2Average.draw) {
            maxValue = graphData2.data.maxValue;
            minValue = graphData2.data.minValue;
        } else if (!graphData2.draw && graphData2Average.draw) {
            maxValue = graphData2Average.data.maxValue;
            minValue = graphData2Average.data.minValue;
        }
        
        if (graphData2.draw) {
            drawGraph(ctx,width,height,xMinMS,xZeroMS,yZero,yMax,step,graphData2,maxValue,minValue,false,'#664466');
        }
        if (graphData2Average.draw) {
            drawGraph(ctx,width,height,xMinMS,xZeroMS,yZero,yMax,step,graphData2Average,maxValue,minValue,false,'#cc99cc');
        }
        ctx.restore();
    }

    function drawGraph(ctx,width,height,xMinMS,xZeroMS,yZero,yMax,step,gData,maxValue,minValue,left,color) {
        ctx.save();
        
        if (gData.sensor === 'temperature') {
            maxValue = Math.floor(maxValue + 2);
            minValue = Math.ceil(minValue - 2);
        } else if (gData.sensor === 'humidity') {
            maxValue = 100;
            minValue = 0;
        } else if (gData.sensor === 'pressure') {
            maxValue = Math.ceil(maxValue);
            minValue = Math.floor(minValue);
        } else if (gData.sensor === 'windspeed') {
            maxValue = Math.ceil(maxValue / 10) * 10;
            minValue = 0;
        } else if (gData.sensor === 'dust_pm10') {
            maxValue = Math.ceil(maxValue / 50) * 50;
            minValue = 0;
        }
          
        // draw y - axis units
        //ctx.beginPath();
        var pixelPerMS = width / RangeEnum.properties[gData.range].ms;                
        var pixelPerUnit = height / (maxValue  - minValue);
        log.debug("drawGraphArea: maxValue     " + maxValue); 
        log.debug("drawGraphArea: minValue     " + minValue);
        log.debug("drawGraphArea: pixelPerUnit " + pixelPerUnit); 
        
        ctx.save();
        ctx.textBaseline = "middle";
        ctx.font         = "12pt LcarsGTJ3";
        ctx.fillStyle    = colorTable.forecast_max_temp;
        ctx.textAlign    = "center";

        
        
        var i = 1;
        if (left) {
            ctx.fillText(minValue,xMinMS - baseButton.space,  yZero);
            ctx.fillText(maxValue,xMinMS - baseButton.space,  yMax);
            for (var yPos = yZero - step; yPos > yMax; yPos -= step) {
               ctx.fillText((minValue + step * i / pixelPerUnit).toFixed(1),xMinMS - baseButton.space,  yPos); 
               i++;
            }
        } else {
            ctx.fillText(minValue,xZeroMS + baseButton.space,  yZero);
            ctx.fillText(maxValue,xZeroMS + baseButton.space,  yMax);
            for (var yPos = yZero - step; yPos > yMax; yPos -= step) {
               ctx.fillText((minValue + step * i / pixelPerUnit).toFixed(1),xZeroMS + baseButton.space,  yPos); 
               i++;
            }
        }
        ctx.restore();
        
        
        var maxDate = new Date(gData.data.sensorData[0].time);
        var maxTimeInMS = maxDate.getTime(); 

        ctx.beginPath();         
        ctx.lineWidth = 2;
        ctx.strokeStyle = color;
        
        ctx.moveTo(xZeroMS - pixelPerMS *(maxTimeInMS - new Date(gData.data.sensorData[0].time).getTime()),
                   yZero - (gData.data.sensorData[0].value - minValue) * pixelPerUnit);
        for (var index = 0; index < gData.data.sensorData.length; ++index) {
            ctx.lineTo(xZeroMS - pixelPerMS *(maxTimeInMS - new Date(gData.data.sensorData[index].time).getTime()),
                       yZero - (gData.data.sensorData[index].value - minValue) * pixelPerUnit);
        }
 
        ctx.stroke();
        
        ctx.restore();
    }
    function setSelected(buttonGroup, button,canvas) {
        for (var name in buttonGroup) {
            buttonGroup[name].selected = false;
        }
        button.selected = true;
        drawToggleButtonGroup(canvas.getContext('2d'),graphButtonRangeGroup,true);        
    };
        
    $.fn.jLCARSControlView = function(options) {

        // var log = log4javascript.getLogger('jMobileDocView');

        return this.each(function() {
            if (!$.data(this, 'jLCARSControlView')) {
                $.data(this, 'jLCARSControlView', new jLCARSControlView(this, options));
            }
        });

    };
})(jQuery);