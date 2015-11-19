
/* global log, WEATHERPI_REST_FORCAST */

function ForecastService(){
    var self = this;
    var servicePath = WEATHERPI_REST_FORCAST + '/';

    self.dailyIcons = function(cb) {

        var serviceUrl = servicePath + 'daily/icons';
    $.ajax({
            type : 'GET',
            url : serviceUrl,
        dataType : 'json',
        contentType : 'application/json'
        }).done(function(data) {
            cb(data);
        });

    };
}

var forecastService = new ForecastService();