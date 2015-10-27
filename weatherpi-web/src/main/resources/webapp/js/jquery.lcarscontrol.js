/* global RangeEnum, log */

/**
 *
 */

;
(function($) {

    var FRAME_INSET = 20;

    var colorTable = {
        header                : '#FF9933',
        text                  : '#3366CC',
        background            : '#000000',
        frame                 : '#CC99CC',
        button_normal         : '#99CCFF',
        togglebutton_selected : '#FFFF99',
        togglebutton_normal   : '#99CCFF',
        statusbutton_set      : '#FFCC66',
        statusbutton_normal   : '#99CCFF'
    };
    
    var lastTouch = {
        x : -1,
        y : -1
    };
    
    var lastLuminanceFront      = '0';

    var lastIndoorHumidity       = '0';
    var lastIndoorTemperature    = '0';
    var lastIndoorPressure       = '0';
    
    var lastOutdoorHumidity       = '0';
    var lastOutdoorTemperature    = '0';
    
    var lastTime                 = new Date().toLocaleTimeString(navigator.language, {hour: '2-digit', minute:'2-digit'});;
    
    var graphData = {
        data  : null,
        range : RangeEnum.DAY        
    };
    
    var buttons = {
        indoorButtonRect     : {text : 'INDOOR',  x : 0, y : 0, w : 0, h : 0, color : colorTable.frame },
        outdoorButtonRect   : {text : 'OUTDOOR', x : 0, y : 0, w : 0, h : 0 , color : colorTable.frame}
    };

    var graphButtonRangeGroup = {
        dayButtonRect      : {text : 'DAY',  range: RangeEnum.DAY,   x : 0, y : 0, w : 0, h : 0, selected : true  },
        weekButtonRect     : {text : 'WEEK', range: RangeEnum.WEEK,  x : 0, y : 0, w : 0, h : 0, selected : false },
        monthButtonRect    : {text : 'MONTH',range: RangeEnum.MONTH, x : 0, y : 0, w : 0, h : 0, selected : false },        
        yearButtonRect     : {text : 'YEAR', range: RangeEnum.YEAR,  x : 0, y : 0, w : 0, h : 0, selected : false }        
    };
    
    var graphButtonSensorGroup = {
        temperatureButtonRect : {text : 'TEMP.',    sensor: "temperature", x : 0, y : 0, w : 0, h : 0, selected : true },
        humidityButtonRect    : {text : 'HUMIDITY', sensor: "humidity",  x : 0, y : 0, w : 0, h : 0, selected : false },
        pressureButtonRect    : {text : 'PRESSURE', sensor: "pressure", x : 0, y : 0, w : 0, h : 0, selected : false }        
    };

    var graphButtonPlaceGroup = {
        indoorButtonRect   : {text : 'INDOOR',  place: 'indoor',  x : 0, y : 0, w : 0, h : 0, selected : true },
        outdoor1ButtonRect : {text : 'OUTDOOR', place: 'outdoor', x : 0, y : 0, w : 0, h : 0, selected : false }       
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

    var baseButton = {
        width  : 100,
        height : 40,
        space  : 5
    };

    var header = {
            size : 40,
            radius : 20,
            space : baseButton.space
        };

    var button = {
            size : baseButton.height,
            radius : baseButton.height / 2,
            labelWidth : baseButton.width - baseButton.height / 2,
            textWidth : 50,
            space : baseButton.space,
            seperatorBoxWidth : baseButton.space
            //labeledInfoButtonWidth : baseButton.height / 2 + button.labelWidth + button.textWidth 
        };
        
    var frame = {
        smallSize : 20,
        thinSize  : 10,
        largeSize : baseButton.width
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
           
            if ((bb = isTouchedButtonGroup(graphButtonRangeGroup)) !== null) {
                setSelected(graphButtonRangeGroup,bb,canvas); 
                $element.triggerHandler(getGraphEventData());
            } else if ((bb = isTouchedButtonGroup(graphButtonPlaceGroup)) !== null) {
                setSelected(graphButtonPlaceGroup,bb,canvas); 
                $element.triggerHandler(getGraphEventData());
            } else if ((bb = isTouchedButtonGroup(graphButtonSensorGroup)) !== null) {
                setSelected(graphButtonSensorGroup,bb,canvas); 
                $element.triggerHandler(getGraphEventData());
            }
            ev.gesture.preventDefault();

        });
    };

    jLCARSControlView.prototype = {

        refresh : function() {
            refresh(this.canvas);
        },

        updateIndoorTemperature : function(temperature) {
            if (temperature === lastIndoorTemperature) {
                // no change, nothing to do
                return;
            }
            lastIndoorTemperature = temperature;
            refresh(this.canvas);
        },

        updateIndoorHumidity : function(humidity) {
            if (humidity === lastIndoorHumidity) {
                // no change, nothing to do
                return;
            }
            lastIndoorHumidity = humidity;
            refresh(this.canvas);
        },

        updateIndoorPressure : function(pressure) {
            if (pressure === lastIndoorPressure) {
                // no change, nothing to do
                return;
            }
            lastIndoorPressure = pressure;
            refresh(this.canvas);
        },

        updateOutdoorTemperature : function(temperature) {
            if (temperature === lastOutdoorTemperature) {
                // no change, nothing to do
                return;
            }
            lastOutdoorTemperature = temperature;
            refresh(this.canvas);
        },

        updateOutdoorHumidity : function(humidity) {
            if (humidity === lastOutdoorHumidity) {
                // no change, nothing to do
                return;
            }
            lastOutdoorHumidity = humidity;
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
        
        updateClock : function() {
            
            var tt = new Date().toLocaleTimeString(navigator.language, {hour: '2-digit', minute:'2-digit'});
            if (tt !== lastTime) {
                lastTime = tt;
                refresh(this.canvas);
            }    
        },
        
        updateGraphData : function(data, range) {
            graphData.data  = data;
            graphData.range = range;
            
            graphData.data.sensorData.sort(function(a,b){
              return new Date(b.time) - new Date(a.time);
            });
            refresh(this.canvas);
        },
        
        getMaxGraphData : function() {
            return historyFrameRect.w;
        }
    };

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
    
    function getGraphEventData() {
        
        var range;
        for (var name in graphButtonRangeGroup) {
            if (graphButtonRangeGroup[name].selected){
                range = graphButtonRangeGroup[name].range;
            }
        } 

        var place;
        for (var name in graphButtonPlaceGroup) {
            if (graphButtonPlaceGroup[name].selected){
                place = graphButtonPlaceGroup[name].place;
            }
        } 

        var sensor;
        for (var name in graphButtonSensorGroup) {
            if (graphButtonSensorGroup[name].selected){
                sensor = graphButtonSensorGroup[name].sensor;
            }
        } 
        
        return {    
            type   :"graphbuttontouch",
            range  : range,
            place  : place,
            sensor : sensor
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
        historyFrameRect.h = 236;

        sensorFrameRect.x = FRAME_INSET;
        sensorFrameRect.y = historyFrameRect.y + historyFrameRect.h + baseButton.space;
        sensorFrameRect.w = 0.55 * windowWidth;
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
        ctx.font      = "40pt LcarsGTJ3";
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
        ctx.font      = "30pt LcarsGTJ3";
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
        var leftBarX = x + frame.largeSize + baseButton.space + 2 * baseButton.width + 2 * baseButton.space;
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
        var rightBarX = x + w ;
        
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
        drawGraphArea(ctx,leftBarX + baseButton.space + frame.smallSize,leftBarY,rightBarX - leftBarX - 2 * baseButton.space - 2* frame.smallSize, leftBarH,graphData);
        /////////
        
        var buttonGapY = y + baseButton.height;
        var buttonPosX = x;
        var buttonPosY = y;

        graphButtonRangeGroup.dayButtonRect.x = buttonPosX;
        graphButtonRangeGroup.dayButtonRect.y = buttonPosY;
        graphButtonRangeGroup.dayButtonRect.w = frame.largeSize;
        graphButtonRangeGroup.dayButtonRect.h = baseButton.height;
        
        graphButtonSensorGroup.temperatureButtonRect.x = buttonPosX + frame.largeSize + baseButton.space;
        graphButtonSensorGroup.temperatureButtonRect.y = buttonPosY;
        graphButtonSensorGroup.temperatureButtonRect.w = frame.largeSize;
        graphButtonSensorGroup.temperatureButtonRect.h = baseButton.height;

        graphButtonPlaceGroup.indoorButtonRect.x = buttonPosX + 2 * frame.largeSize + 2 * baseButton.space;
        graphButtonPlaceGroup.indoorButtonRect.y = buttonPosY;
        graphButtonPlaceGroup.indoorButtonRect.w = frame.largeSize;
        graphButtonPlaceGroup.indoorButtonRect.h = baseButton.height;

        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);

        buttonPosY = buttonGapY + baseButton.space;

        graphButtonRangeGroup.weekButtonRect.x = buttonPosX;
        graphButtonRangeGroup.weekButtonRect.y = buttonPosY;
        graphButtonRangeGroup.weekButtonRect.w = frame.largeSize;
        graphButtonRangeGroup.weekButtonRect.h = baseButton.height;

        graphButtonSensorGroup.pressureButtonRect.x = buttonPosX + frame.largeSize + baseButton.space;
        graphButtonSensorGroup.pressureButtonRect.y = buttonPosY;
        graphButtonSensorGroup.pressureButtonRect.w = frame.largeSize;
        graphButtonSensorGroup.pressureButtonRect.h = baseButton.height;

        graphButtonPlaceGroup.outdoor1ButtonRect.x = buttonPosX + 2 * frame.largeSize + 2 * baseButton.space;
        graphButtonPlaceGroup.outdoor1ButtonRect.y = buttonPosY;
        graphButtonPlaceGroup.outdoor1ButtonRect.w = frame.largeSize;
        graphButtonPlaceGroup.outdoor1ButtonRect.h = baseButton.height;

        buttonGapY = buttonGapY + baseButton.height + baseButton.space;

        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);
        
        buttonPosY = buttonGapY + baseButton.space;

        graphButtonRangeGroup.monthButtonRect.x = buttonPosX;
        graphButtonRangeGroup.monthButtonRect.y = buttonPosY;
        graphButtonRangeGroup.monthButtonRect.w = frame.largeSize;
        graphButtonRangeGroup.monthButtonRect.h = baseButton.height;

        graphButtonSensorGroup.humidityButtonRect.x = buttonPosX + frame.largeSize + baseButton.space;
        graphButtonSensorGroup.humidityButtonRect.y = buttonPosY;
        graphButtonSensorGroup.humidityButtonRect.w = frame.largeSize;
        graphButtonSensorGroup.humidityButtonRect.h = baseButton.height;  
        
        buttonGapY = buttonGapY + baseButton.height + baseButton.space;

        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);
        
        buttonPosY = buttonGapY + baseButton.space;

        graphButtonRangeGroup.yearButtonRect.x = buttonPosX;
        graphButtonRangeGroup.yearButtonRect.y = buttonPosY;
        graphButtonRangeGroup.yearButtonRect.w = frame.largeSize;
        graphButtonRangeGroup.yearButtonRect.h = baseButton.height;

        //drawToggleButton(ctx,graphButtonRangeGroup.weekButtonRect);
  
        buttonGapY = buttonGapY + baseButton.height + baseButton.space;

        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);
        
        // draw the button groups
        drawToggleButtonGroup(ctx,graphButtonRangeGroup);
        drawToggleButtonGroup(ctx,graphButtonSensorGroup);
        drawToggleButtonGroup(ctx,graphButtonPlaceGroup);
        
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
        
        var buttonGapY = y + (frame.smallSize + baseButton.height);
        var buttonPosX = x + w - frame.largeSize;
        var infoButtonPosX = buttonPosX - getLabeledInfoButtonWidth() - 30 - button.space;
        var buttonPosY = buttonGapY + baseButton.space;

        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);

        buttons.indoorButtonRect.x = buttonPosX;
        buttons.indoorButtonRect.y = buttonPosY;
        buttons.indoorButtonRect.w = frame.largeSize;
        buttons.indoorButtonRect.h = 2 * baseButton.height + baseButton.space;

        drawButton(ctx,buttons.indoorButtonRect);
        
        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"HUMIDITY",lastIndoorHumidity,"#FFCC66");
        drawLabeledInfoButton(ctx,infoButtonPosX - getLabeledInfoButtonWidth() - button.space,buttonPosY,"TEMP.",lastIndoorTemperature,"#FFCC66");

        buttonPosY = buttonPosY + baseButton.height + baseButton.space;
        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"PRESSURE",lastIndoorPressure,"#FFCC66");

        buttonGapY = buttonGapY + 2 * baseButton.height + 2 * baseButton.space;
        
        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);
        
        buttonPosY = buttonGapY + baseButton.space;
        
        buttons.outdoorButtonRect.x = buttonPosX;
        buttons.outdoorButtonRect.y = buttonPosY;
        buttons.outdoorButtonRect.w = frame.largeSize;
        buttons.outdoorButtonRect.h = baseButton.height;

        drawButton(ctx,buttons.outdoorButtonRect);

        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"HUMIDITY",lastOutdoorHumidity,"#3366CC");
        drawLabeledInfoButton(ctx,infoButtonPosX - getLabeledInfoButtonWidth() - button.space,buttonPosY,"TEMP.",lastOutdoorTemperature,"#3366CC");


        buttonGapY = buttonGapY + baseButton.height + baseButton.space;
        
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
        ctx.font      = "20pt LcarsGTJ3";
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
        
        ctx.font         = "30pt LcarsGTJ3";
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
        ctx.font      = "30pt LcarsGTJ3";
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

        ctx.font         = "20pt LcarsGTJ3";
        ctx.textAlign    = "right";
        ctx.textBaseline = "top";
        ctx.fillStyle    = colorTable.background;
        ctx.fillText(button.text,button.x + frame.largeSize - baseButton.space,button.y + baseButton.space);
        ctx.restore();
    }
    
    function drawToggleButton(ctx,button) {
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

        ctx.font         = "20pt LcarsGTJ3";
        ctx.textAlign    = "right";
        ctx.textBaseline = "top";
        ctx.fillStyle    = colorTable.background;
        ctx.fillText(button.text,button.x + frame.largeSize - baseButton.space,button.y + baseButton.space);
        ctx.restore();
    }
    
    function drawToggleButtonGroup(ctx,buttonGroup) {
        for (var name in buttonGroup) {
            drawToggleButton(ctx,buttonGroup[name]);
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
        
        var step = Math.floor(w / 24);
        
        
        
        
        
        var width  = step * Math.floor(w/step);
        var height = step * Math.floor(h/step) - step;
        
        var xZeroMS = x + w / 2 + width / 2;
        var yZero = y + h / 2 + height / 2;
                

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
        
        if (graphData.data === null || graphData.data.sensordata === null) {
            return;
        }
        var maxValue = graphData.data.maxValue;
        var minValue = graphData.data.minValue;
                
        var pixelPerMS = width / RangeEnum.properties[graphData.range].ms;                
        var pixelPerUnit = height / (maxValue  - minValue);
         
        log.debug("drawGraphArea: maxValue     " + maxValue); 
        log.debug("drawGraphArea: minValue     " + minValue);
        log.debug("drawGraphArea: pixelPerUnit " + pixelPerUnit); 
        var maxDate = new Date(graphData.data.sensorData[0].time);
        var maxTimeInMS = maxDate.getTime(); 

        ctx.beginPath();         
        ctx.lineWidth = 2;
        ctx.strokeStyle = '#FF9933';
        ctx.moveTo(xZeroMS - pixelPerMS *(maxTimeInMS - new Date(graphData.data.sensorData[0].time).getTime()),yZero - graphData.data.sensorData[0].value * pixelPerUnit + minValue * pixelPerUnit);
        for (var index = 0; index < graphData.data.sensorData.length; ++index) {
            ctx.lineTo(xZeroMS - pixelPerMS *(maxTimeInMS - new Date(graphData.data.sensorData[index].time).getTime()),yZero - graphData.data.sensorData[index].value * pixelPerUnit + minValue * pixelPerUnit);
        }
 
        ctx.stroke();
        
        
        ctx.restore();
    }
    
    function setSelected(buttonGroup, button,canvas) {
        for (var name in buttonGroup) {
            buttonGroup[name].selected = false;
        }
        button.selected = true;
        drawToggleButtonGroup(canvas.getContext('2d'),graphButtonRangeGroup);        
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