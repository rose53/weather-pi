/**
 *
 */

;
(function($) {

    var FRAME_INSET = 20;

    var lastTouch = {
        x : -1,
        y : -1
    };

    var lastLuminanceFront      = '0';

    var lastIndoorHumidity       = '0';
    var lastIndoorTemperature    = '0';
    var lastIndoorPressure       = '0';

    var lastTime                 = new Date().toLocaleTimeString(navigator.language, {hour: '2-digit', minute:'2-digit'});;

    var buttons = {
        dayButtonRect      : {text : 'DAY',   x : 0, y : 0, w : 0, h : 0, selected : true },
        weekButtonRect     : {text : 'WEEK',  x : 0, y : 0, w : 0, h : 0, selected : false },
        indorButtonRect     : {text : 'INDOOR',  x : 0, y : 0, w : 0, h : 0 },
        outdoorButtonRect     : {text : 'OUTDOOR', x : 0, y : 0, w : 0, h : 0 },
        bmp085ButtonRect    : {text : 'BMP085', x : 0, y : 0, w : 0, h : 0 },
        luminanceButtonRect : {text : 'Luminance', x : 0, y : 0, w : 0, h : 0 }
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

            if (    lastTouch.x > buttons.dayButtonRect.x && lastTouch.x < (buttons.dayButtonRect.x + buttons.dayButtonRect.w)
                        && lastTouch.y > buttons.dayButtonRect.y && lastTouch.y < (buttons.dayButtonRect.y + buttons.dayButtonRect.h)) {
                buttons.dayButtonRect.selected = !buttons.dayButtonRect.selected;
                drawToggleButton(canvas.getContext('2d'),buttons.dayButtonRect);
                $element.triggerHandler({
                    type:"daybuttontouch"
                  });
            } else if (    lastTouch.x > buttons.weekButtonRect.x && lastTouch.x < (buttons.weekButtonRect.x + buttons.weekButtonRect.w)
                        && lastTouch.y > buttons.weekButtonRect.y && lastTouch.y < (buttons.weekButtonRect.y + buttons.weekButtonRect.h)) {
                buttons.weekButtonRect.selected = !buttons.weekButtonRect.selected;
                drawToggleButton(canvas.getContext('2d'),buttons.weekButtonRect);

                $element.triggerHandler({
                    type:"weekbuttontouch"
                  });
            } else if (    lastTouch.x > buttons.indorButtonRect.x && lastTouch.x < (buttons.indorButtonRect.x + buttons.indorButtonRect.w)
                        && lastTouch.y > buttons.indorButtonRect.y && lastTouch.y < (buttons.indorButtonRect.y + buttons.indorButtonRect.h)) {

                $element.triggerHandler({
                    type:"indoorbuttontouch"
                  });
            } else if (    lastTouch.x > buttons.outdoorButtonRect.x && lastTouch.x < (buttons.outdoorButtonRect.x + buttons.outdoorButtonRect.w)
                        && lastTouch.y > buttons.outdoorButtonRect.y && lastTouch.y < (buttons.outdoorButtonRect.y + buttons.outdoorButtonRect.h)) {
                $element.triggerHandler({
                    type:"outdoorbuttontouch"
                  });
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
        }
    };

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
        historyFrameRect.h = 250;

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
        drawIndoorFrame(ctx,sensorFrameRect.x,sensorFrameRect.y,sensorFrameRect.w,sensorFrameRect.h);
        drawOutdoorFrame(ctx,forecastFrameRect.x,forecastFrameRect.y,forecastFrameRect.w,forecastFrameRect.h,baseButtonPosX);


    }


    function drawHeader(ctx,x,y,w) {
        ctx.save();

        ctx.fillStyle = colorTable.header;
        ctx.font      = "40pt LcarsGTJ3";
        ctx.beginPath();
        ctx.arc(x + header.radius, header.radius + baseButton.space, header.radius, Math.radians(90), Math.radians(270), false);
        ctx.moveTo(x + header.radius,y);
        ctx.lineTo(x + header.radius + header.size,y);
        ctx.lineTo(x + header.radius + header.size,y + header.size);
        ctx.lineTo(x + header.radius,y + header.size);
        ctx.fill();
        ctx.closePath();

        ctx.fillStyle = "#3366CC";
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
        var leftBarX = x + frame.largeSize + 50;
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
        var rightBarX = x + w - getStatusButtonWidth() - 2 * baseButton.space;

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

        drawGraphArea(ctx,leftBarX + baseButton.space + frame.smallSize,leftBarY,rightBarX - leftBarX - 2 * baseButton.space - 2* frame.smallSize, leftBarH);
        /////////

        var buttonGapY = y + baseButton.height;
        var buttonPosX = x;
        var buttonPosY = y;

        buttons.dayButtonRect.x = buttonPosX;
        buttons.dayButtonRect.y = buttonPosY;
        buttons.dayButtonRect.w = frame.largeSize;
        buttons.dayButtonRect.h = baseButton.height;

        drawButton(ctx,buttons.dayButtonRect);
        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);

        buttonPosY = buttonGapY + baseButton.space;

        buttons.weekButtonRect.x = buttonPosX;
        buttons.weekButtonRect.y = buttonPosY;
        buttons.weekButtonRect.w = frame.largeSize;
        buttons.weekButtonRect.h = baseButton.height;

        drawToggleButton(ctx,buttons.weekButtonRect);

        buttonGapY = buttonGapY + baseButton.height + baseButton.space;

        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);


        drawStatusButton(ctx,x + w - getStatusButtonWidth(),y,lastTime,false);

        ctx.restore();
    }

    /**
     * Draws the sensor information.
     * @param {type} ctx
     * @param {type} x
     * @param {type} y
     * @param {type} w the width of the sensor area on the screen in pixel
     * @param {type} h the height of the sensor area on the screen in pixel
     * @param {type} baseButtonPosX description
     * @returns {undefined}
     */
    function drawOutdoorFrame(ctx,x,y,w,h,baseButtonPosX) {
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

/*
        drawButtonVerticalGap(ctx,baseButtonPosX - baseButton.space,y,frame.smallSize);
        drawButtonVerticalGap(ctx,baseButtonPosX - baseButton.space,y + h - frame.smallSize,frame.smallSize);

        var buttonGapY = y + (frame.smallSize + baseButton.height);
        var buttonPosX = baseButtonPosX;
        var buttonPosY = buttonGapY + baseButton.space;

        // temperature and humidity

        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);



        // temperature and pressure

        buttons.bmp085ButtonRect.x = x;
        buttons.bmp085ButtonRect.y = buttonPosY;
        buttons.bmp085ButtonRect.w = frame.largeSize;
        buttons.bmp085ButtonRect.h = 2 * baseButton.height +  baseButton.space;

        drawButton(ctx,buttons.bmp085ButtonRect);
        //drawLabeledInfoButton(ctx,buttonPosX,buttonPosY,"TEMP.",lastTemperatureBMP085,"#3366CC");
        buttonPosY = buttonPosY + baseButton.height + baseButton.space;
        //drawLabeledInfoButton(ctx,buttonPosX,buttonPosY,"PRESSURE",lastPressureBMP085,"#3366CC");

        buttonGapY = buttonGapY + 2 * baseButton.height + 2 * baseButton.space;
        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);

        // luminance front and rear
        buttonPosY = buttonGapY + baseButton.space;

        buttons.luminanceButtonRect.x = x;
        buttons.luminanceButtonRect.y = buttonPosY;
        buttons.luminanceButtonRect.w = frame.largeSize;
        buttons.luminanceButtonRect.h = baseButton.height +  baseButton.space;

        drawButton(ctx,buttons.luminanceButtonRect);
        var ibw = drawLabeledInfoButton(ctx,buttonPosX,buttonPosY,"FRONT",lastLuminanceFront,"#3366CC");

        buttonGapY = buttonGapY + baseButton.height + baseButton.space;
        drawButtonHorizontalGap(ctx,x,buttonGapY,frame.largeSize);

        drawButtonVerticalGap(ctx,baseButtonPosX + ibw,y,frame.smallSize);
        drawButtonVerticalGap(ctx,baseButtonPosX + ibw,y + h - frame.smallSize,frame.smallSize);
        */
        ctx.restore();
    }


    function drawIndoorFrame(ctx,x,y,w,h) {
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

        // engine on/off
        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);

        buttons.indorButtonRect.x = buttonPosX;
        buttons.indorButtonRect.y = buttonPosY;
        buttons.indorButtonRect.w = frame.largeSize;
        buttons.indorButtonRect.h = 2 * baseButton.height + baseButton.space;

        drawButton(ctx,buttons.indorButtonRect);

        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"HUMIDITY",lastIndoorHumidity,"#3366CC");
        drawLabeledInfoButton(ctx,infoButtonPosX - getLabeledInfoButtonWidth() - button.space,buttonPosY,"TEMP.",lastIndoorTemperature,"#3366CC");

        buttonPosY = buttonPosY + baseButton.height + baseButton.space;
        drawLabeledInfoButton(ctx,infoButtonPosX,buttonPosY,"PRESSURE",lastIndoorPressure,"#3366CC");

        buttonGapY = buttonGapY + 2 * baseButton.height + 2 * baseButton.space;

        drawButtonHorizontalGap(ctx,buttonPosX,buttonGapY,frame.largeSize);

        buttonPosY = buttonGapY + baseButton.space;

        buttons.outdoorButtonRect.x = buttonPosX;
        buttons.outdoorButtonRect.y = buttonPosY;
        buttons.outdoorButtonRect.w = frame.largeSize;
        buttons.outdoorButtonRect.h = baseButton.height;

        drawButton(ctx,buttons.outdoorButtonRect);

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
        ctx.fillStyle   = colorTable.button_normal;
        ctx.strokeStyle = colorTable.button_normal;
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

        for (var yAxis = 0; yAxis <= w / 2; yAxis += step) {
            ctx.moveTo(x + w / 2 + yAxis,y);
            ctx.lineTo(x + w / 2 + yAxis,y + h);
            ctx.moveTo(x + w / 2 - yAxis,y);
            ctx.lineTo(x + w / 2 - yAxis,y + h);
        }

        for (var xAxis = 0; xAxis <= h / 2; xAxis += step) {
            ctx.moveTo(x,  y + h / 2 + xAxis);
            ctx.lineTo(x + w, y + h / 2 + xAxis);
            ctx.moveTo(x,  y + h / 2 - xAxis);
            ctx.lineTo(x + w, y + h / 2 - xAxis);
        }
        ctx.lineWidth = 1;
        ctx.strokeStyle = colorTable.frame;
        ctx.stroke();


        ctx.restore();
    }

    $.fn.jLCARSControlView = function(options) {

        // var log = log4javascript.getLogger('jMobileDocView');

        return this.each(function() {
            if (!$.data(this, 'jLCARSControlView')) {
                $.data(this, 'jLCARSControlView', new jLCARSControlView(this, options));
            }
        });

    };
})(jQuery);