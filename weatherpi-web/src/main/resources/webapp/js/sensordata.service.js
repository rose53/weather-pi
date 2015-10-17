
/* global log, WEATHERPI_REST_SENSORDATA */

function SensordataService(){
    var self = this;
    var servicePath = WEATHERPI_REST_SENSORDATA + '/';

    self.doRestCall = function(sensor,rangeParam, placeParam, samplesParam, cb) {
            
        var dataObj = {range   : rangeParam,
                       place   : placeParam,
                       samples : samplesParam};
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

    self.getTemperature = function(rangeParam, placeParam, samplesParam, cb){
        self.doRestCall('temperature',rangeParam, placeParam, samplesParam, cb);
    };
    
    self.getPressure = function(rangeParam, placeParam, samplesParam, cb){        
        self.doRestCall('pressure',rangeParam, placeParam, samplesParam, cb);
    };
    
    self.getHumidity = function(rangeParam, placeParam, samplesParam, cb){        
        self.doRestCall('humidity',rangeParam, placeParam, samplesParam, cb);
    };

}

var sensordataService = new SensordataService();