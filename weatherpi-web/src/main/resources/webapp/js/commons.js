
//=== CONSTANTS ===
var WEATHERPI_REST_ROOT       = 'rest/resources/';
var WEATHERPI_REST_SENSORDATA = WEATHERPI_REST_ROOT + 'sensordata';

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
