
/* global log, WEATHERPI_REST_SENSORDATA */

function SensordataService(){
    var self = this;
    var servicePath = WEATHERPI_REST_SENSORDATA + '/';

    self.getHello = function(){
    var serviceUrl = servicePath + 'temperature';
    var hello = null;
    log.debug("getHello: " + serviceUrl);
    $.support.cors = true;
    $.ajax({
        type : 'GET',
        url : serviceUrl,
        crossDomain: true,
        dataType : 'json',
        contentType : 'text/plain',
        success : function(data) {
           //hello = data;
           log.debug("getHello: hello = " + data);
        }
    }).done(function(data) {
        log.debug("getHello: hello = " + data);
        });
    return "hello test";
    };

    self.getTemperature = function(rangeParam,cb){

        var dataObj = {range : rangeParam};
        var serviceUrl = servicePath + 'temperature';
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
}

var sensordataService = new SensordataService();