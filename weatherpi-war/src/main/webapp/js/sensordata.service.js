
/* global log, WEATHERPI_REST_SENSORDATA */

function SensordataService(){
    var self = this;
    var servicePath = WEATHERPI_REST_SENSORDATA ;

    self.doRestCall = function(sensor,nameParam, rangeParam, placeParam, samplesParam, cb) {
            
        var dataObj = {samples : samplesParam};
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

    self.getTemperature = function(rangeParam, placeParam, samplesParam, cb){
        self.doRestCall('temperature','bme280', rangeParam, placeParam, samplesParam, cb);
    };
    
    self.getPressure = function(rangeParam, placeParam, samplesParam, cb){        
        self.doRestCall('pressure','bme280', rangeParam, placeParam, samplesParam, cb);
    };
    
    self.getHumidity = function(rangeParam, placeParam, samplesParam, cb){        
        self.doRestCall('humidity','bme280',rangeParam, placeParam, samplesParam, cb);
    };

    self.getWindspeed = function(rangeParam, placeParam, samplesParam, cb){        
        self.doRestCall('windspeed','eltako_ws',rangeParam, placeParam, samplesParam, cb);
    };
}

var sensordataService = new SensordataService();