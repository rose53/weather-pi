
//=== CONSTANTS ===
var RUGOREST_ROOT      = 'service/';
var RUGOREST_MOTOR     = RUGOREST_ROOT + 'motor';
var RUGOREST_DISTANCE  = RUGOREST_ROOT + 'distance';
var RUGOREST_GP2Y0A21  = RUGOREST_ROOT + 'gp2y0a21';
var RUGOREST_DHT22     = RUGOREST_ROOT + 'dht22';
var RUGOREST_BMP085    = RUGOREST_ROOT + 'bmp085';
var RUGOREST_LUMINANCE = RUGOREST_ROOT + 'luminance';

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
