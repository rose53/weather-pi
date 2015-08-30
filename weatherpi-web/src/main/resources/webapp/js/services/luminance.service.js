
function LuminanceService(){
    var self = this;
    var servicePath = RUGOREST_LUMINANCE + '/';

    self.getLuminance = function (location){
        
        return 4711;
        
    var serviceUrl = servicePath + 'luminance/' + location;
    var luminance = null;
    log.debug("getLuminance: " + serviceUrl);
    $.ajax({
        type : 'GET',
        url : serviceUrl,
        dataType : 'json',
        processData : false,
        contentType : 'application/json',
        async : false,
        success : function(data) {
        luminance = data;
        log.debug("getLuminance: luminance = " + luminance);
        }
    });
    return luminance;
    };

}

var luminanceService = new LuminanceService();