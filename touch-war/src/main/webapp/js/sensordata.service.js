
/* global log, WEATHERPI_HOST  */

function SensordataService(){
    var self = this;
    var servicePath = location.protocol + '//' + WEATHERPI_HOST + '/weatherpi/resources/sensordata';

    self.doRestCall = function(sensor,nameParam, rangeParam, placeParam, samplesParam, cb, movingAverageParam) {

        var dataObj = {samples      : samplesParam,
                       movingAverage: movingAverageParam};
        var serviceUrl = servicePath + '/' + placeParam + '/' + nameParam + '/' + sensor + '/' + rangeParam;
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

    self.getTemperature = function(rangeParam, nameParam, placeParam, samplesParam, cb){
        self.doRestCall('temperature',nameParam, rangeParam, placeParam, samplesParam, cb,false);
    };

    self.getPressure = function(rangeParam, placeParam, samplesParam, cb){
        self.doRestCall('pressure','bme280', rangeParam, placeParam, samplesParam, cb, false);
    };

    self.getHumidity = function(rangeParam, nameParam, placeParam, samplesParam, cb){
        self.doRestCall('humidity',nameParam,rangeParam, placeParam, samplesParam, cb,false);
    };

    self.getWindspeed = function(rangeParam, placeParam, samplesParam, cb){
        self.doRestCall('windspeed','eltako_ws',rangeParam, placeParam, samplesParam, cb,false);
    }; 
    
    self.getDustPM10 = function(rangeParam, placeParam, samplesParam, cb){
        self.doRestCall('dust_pm10','sds011',rangeParam, placeParam, samplesParam, cb,false);
    }; 

    self.getDustPM25 = function(rangeParam, placeParam, samplesParam, cb){
        self.doRestCall('dust_pm25','sds011',rangeParam, placeParam, samplesParam, cb,false);
    }; 

}

var sensordataService = new SensordataService();