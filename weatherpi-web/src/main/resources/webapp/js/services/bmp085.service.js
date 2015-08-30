
function BMP085Service(){
	var self = this;
	var servicePath = RUGOREST_BMP085 + '/';

	self.getTemperature = function (){
            return 22;
            
            
		var serviceUrl = servicePath + 'temperature';
		var temperature = null;
		log.debug("getTemperature: " + serviceUrl);
		$.ajax({
			type : 'GET',
			url : serviceUrl,
			dataType : 'json',
			processData : false,
			contentType : 'application/json',
			async : false,
			success : function(data) {
				temperature = data;
				log.debug("getTemperature: temperature = " + temperature);
			}
		});
		return temperature;
	};

	self.getPressure = function (){
            
            return 1017;
		var serviceUrl = servicePath + 'pressure';
		var pressure = null;
		log.debug("getPressure: " + serviceUrl);
		$.ajax({
			type : 'GET',
			url : serviceUrl,
			dataType : 'json',
			processData : false,
			contentType : 'application/json',
			async : false,
			success : function(data) {
				pressure = data;
				log.debug("getPressure: pressure = " + pressure);
			}
		});
		return pressure;
	};
}

var bmp085Service = new BMP085Service();