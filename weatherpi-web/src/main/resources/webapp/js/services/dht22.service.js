
function DHT22Service(){
	var self = this;
	var servicePath = RUGOREST_DHT22 + '/';

	self.getTemperature = function (){
            
            return 13;
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

	self.getHumidity = function (){
            
            return 42; 
		var serviceUrl = servicePath + 'humidity';
		var humidity = null;
		log.debug("getHumidity: " + serviceUrl);
		$.ajax({
			type : 'GET',
			url : serviceUrl,
			dataType : 'json',
			processData : false,
			contentType : 'application/json',
			async : false,
			success : function(data) {
				humidity = data;
				log.debug("getTemperature: humidity = " + humidity);
			}
		});
		return humidity;
	};
}

var dht22Service = new DHT22Service();