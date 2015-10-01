
//=== CONSTANTS ===
var WEATHERPI_REST_ROOT       = 'rest/resources/';
var WEATHERPI_REST_SENSORDATA = WEATHERPI_REST_ROOT + 'sensordata';

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
