
//=== CONSTANTS ===
var WEATHERPI_HOST           = location.host;
//var WEATHERPI_HOST           = "weatherpi.local:8080";

var RangeEnum = {
    ACTUAL : 1,
    DAY    : 2,
    WEEK   : 3,
    MONTH  : 4,
    YEAR   : 5,

    properties: {
    1: {name: "Actual",
        value: 1,
        queryvalue: "actual",
        ms: 24 * 60 * 60 * 1000},
    2: {name: "Day",
        value: 2,
        queryvalue: "day",
        ms: 24 * 60 * 60 * 1000},
    3: {name: "Week",
        value: 3,
        queryvalue: "week",
        ms: 7 * 24 * 60 * 60 * 1000},
    4: {name: "Month",
        value: 4,
        queryvalue: "month",
        ms: 31 *  24 * 60 * 60 * 1000},
    5: {name: "Year",
        value: 5,
        queryvalue: "year",
        ms: 365 * 24 * 60 * 60 * 1000}
  }
};

var getImage = function(src) {
    var retVal = new Image();
    retVal.src = src;
    return retVal;
};

var ForecastIconEnum = {
    CLEAR_DAY   : 1,
    CLEAR_NIGHT : 2,
    RAIN   : 3,
    SNOW  : 4,
    SLEET   : 5,
    WIND   : 6,
    FOG   : 7,
    CLOUDY   : 8,
    PARTLY_CLOUDY_DAY : 9,
    PARTLY_CLOUDY_NIGHT : 10,

    properties: {
    1:  {name: "clear-day",
         value: 1,
         src: "images/sun.svg"},
    2:  {name: "clear-night",
         value: 2,
         src: "images/moon.svg"},
    3:  {name: "rain",
         value: 3,
         src: "images/cloud-rain.svg"},
    4:  {name: "snow",
         value: 4,
         src: "images/cloud-snow.svg"},
    5:  {name: "sleet",
         value: 5,
         src: "images/cloud-hail.svg"},
    6:  {name: "wind",
         value: 6,
         src: "images/wind.svg"},
    7:  {name: "fog",
         value: 7,
         src: "images/cloud-fog.svg"},
    8:  {name: "cloudy",
         value: 8,
         src: "images/cloud.svg"},
    9:  {name: "partly-cloudy-day",
         value: 9,
         src: "images/cloud-sun.svg"},
    10: {name: "partly-cloudy-night",
         value: 10,
         src: "images/cloud-moon.svg"}
    },

    getForecastIconEnumForName : function(iconName) {
        if (this.properties[this.CLEAR_DAY].name === iconName) {
            return this.CLEAR_DAY;
        } else if (this.properties[this.CLEAR_NIGHT].name === iconName) {
            return this.CLEAR_NIGHT;
        } else if (this.properties[this.RAIN].name === iconName) {
            return this.RAIN;
        } else if (this.properties[this.SNOW].name === iconName) {
            return this.SNOW;
        } else if (this.properties[this.SLEET].name === iconName) {
            return this.SLEET;
        } else if (this.properties[this.WIND].name === iconName) {
            return this.WIND;
        } else if (this.properties[this.FOG].name === iconName) {
            return this.FOG;
        } else if (this.properties[this.CLOUDY].name === iconName) {
            return this.CLOUDY;
        } else if (this.properties[this.PARTLY_CLOUDY_DAY].name === iconName) {
            return this.PARTLY_CLOUDY_DAY;
        } else if (this.properties[this.PARTLY_CLOUDY_NIGHT].name === iconName) {
            return this.PARTLY_CLOUDY_NIGHT;
        } else {
            return null;
        }
    }
};

var forecastImages = [];

forecastImages.push(getImage("images/sun.svg"));
forecastImages.push(getImage("images/moon.svg"));
forecastImages.push(getImage("images/cloud-rain.svg"));
forecastImages.push(getImage("images/cloud-snow.svg"));
forecastImages.push(getImage("images/cloud-hail.svg"));
forecastImages.push(getImage("images/wind.svg"));
forecastImages.push(getImage("images/cloud-fog.svg"));
forecastImages.push(getImage("images/cloud.svg"));
forecastImages.push(getImage("images/cloud-sun.svg"));
forecastImages.push(getImage("images/cloud-moon.svg"));


var MoonPhaseIconEnum = {
    MOON_NEW   : 1,
    MOON_WAXING_CRESCENT : 2,
    MOON_FIRST_QUARTER : 3,
    MOON_WAXING_GIBBOUS: 4,
    MOON_FULL  : 5,
    MOON_WANING_GIBBOUS : 6,
    MOON_LAST_QUARTER   : 7,
    MOON_WANING_CRESCENT : 8,



    properties: {
    1:  {name: "new moon",
         value: 1,
         src: "images/moon-new.svg"},
    2:  {name: "waxing crescent",
         value: 2,
         src: "images/moon-waxing-crescent.svg"},
    3:  {name: "first quarter",
         value: 3,
         src: "images/moon-first-quarter.svg"},
    4:  {name: "waxing gibbous",
         value: 4,
         src: "images/moon-waxing-gibbous.svg"},
    5:  {name: "full moon",
         value: 5,
         src: "images/moon-full.svg"},
    6:  {name: "waning gibbous",
         value: 6,
         src: "images/moon-waning-gibbous.svg"},
    7:  {name: "last quarter",
         value: 7,
         src: "images/moon-last-quarter.svg"},
    8:  {name: "waning crescent",
         value: 8,
         src: "images/moon-waning-crescent.svg"}
    },

    getPhaseIconEnum : function(moonPhase) {
        if (moonPhase <= 0.0) {
            return this.MOON_NEW;
        } else if (0.0 < moonPhase && moonPhase < 0.25) {
            return this.MOON_WAXING_CRESCENT;
        } else if (moonPhase === 0.25) {
            return this.MOON_FIRST_QUARTER;
        } else if (0.25 < moonPhase && moonPhase < 0.5) {
            return this.MOON_WAXING_GIBBOUS;
        } else if (moonPhase === 0.5) {
            return this.MOON_FULL;
        } else if (0.5 < moonPhase && moonPhase < 0.75) {
            return this.MOON_WANING_GIBBOUS;
        } else if (moonPhase === 0.75) {
            return this.MOON_LAST_QUARTER;
        } else if (moonPhase > 0.75) {
            return this.MOON_WANING_CRESCENT;
        }
    }
};

var moonPhaseImages = [];

moonPhaseImages.push(getImage("images/moon-new.svg"));
moonPhaseImages.push(getImage("images/moon-waxing-crescent.svg"));
moonPhaseImages.push(getImage("images/moon-first-quarter.svg"));
moonPhaseImages.push(getImage("images/moon-waxing-gibbous.svg"));
moonPhaseImages.push(getImage("images/moon-full.svg"));
moonPhaseImages.push(getImage("images/moon-waning-gibbous.svg"));
moonPhaseImages.push(getImage("images/moon-last-quarter.svg"));
moonPhaseImages.push(getImage("images/moon-waning-crescent.svg"));

var sunriseIconSource = new Image();
sunriseIconSource.src = 'images/sunrise.png';

var sunsetIconSource = new Image();
sunsetIconSource.src = 'images/sunset.png';

/*
 * Converts degrees to radians
 */
Math.radians = function(degrees) {
    return degrees * Math.PI / 180;
};

/*
 * Converts degrees to radians
 */
Math.degrees = function(radians) {
    return radians * 180 / Math.PI;
};

Number.prototype.padLeft = function(n,str) {
    return Array(n-String(this).length+1).join(str||'0')+this;
};
