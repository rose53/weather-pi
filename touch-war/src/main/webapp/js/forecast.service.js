
/* global log, WEATHERPI_HOST */

function ForecastService(){
    var self = this;
    var servicePath = location.protocol + '//' + WEATHERPI_HOST + '/forecast/resources/forecast/';

    self.daily = function(cb) {

        var serviceUrl = servicePath + 'daily';
    $.ajax({
            type : 'GET',
            url : serviceUrl,
        dataType : 'json',
        contentType : 'application/json'
        }).done(function(data) {
            cb(data);
        });
    };

    self.currently = function(cb) {

        var serviceUrl = servicePath + 'currently';
    $.ajax({
            type : 'GET',
            url :  serviceUrl,
        dataType : 'json',
        contentType : 'application/json'
        }).done(function(data) {
            cb(data);
        });
    };
}

var forecastService = new ForecastService();