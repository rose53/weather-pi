
/* global log, WEATHERPI_REST_SENSORDATA */

function SensordataService(){
    var self = this;
    var servicePath = WEATHERPI_REST_SENSORDATA + '/';

    function doRestCall(sensor,rangeParam, placeParam, cb) {

        var dataObj = {range : rangeParam,
                       place : placeParam};
        var serviceUrl = servicePath + sensor;
    $.ajax({
            type : 'GET',
            url : serviceUrl,
            data : dataObj,
        dataType : 'json',
        contentType : 'application/json'
        }).done(function(data) {
            cb(data);
        });

    };

    self.getTemperature = function(rangeParam, placeParam, cb){
        doRestCall('temperature',rangeParam, placeParam, cb)
    };

    self.getPressure = function(rangeParam, placeParam, cb){
        doRestCall('pressure',rangeParam, placeParam, cb)
    };

    self.getHumidity = function(rangeParam, placeParam, cb){
        doRestCall('humidity',rangeParam, placeParam, cb)
    };

}

var sensordataService = new SensordataService();