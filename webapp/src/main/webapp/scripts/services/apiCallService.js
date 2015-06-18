angular
    .module('app')
    .factory('ApiCallService', [
        '$http', '$window',  'Session', '$translate', '$location', function($http, $window, Session, $translate, $location) {
            var service = {};
            service.apiUrl = function (){
                var isProdEnvironment = $location.host() === 'www.neerpoints.com';
                var url = 'http://localhost:9090/api/';
                if(isProdEnvironment) {
                    url = 'http://services-neerpoints.rhcloud.com/api/';
                }
                return url;
            };

            service.callApi = function(method, path, data) {
                var key = Session.user ? Session.user.apiKey : '';
                return $http({
                    method: method,
                    url: service.apiUrl() + path,
                    data: data,
                    headers: {'Content-Type': 'application/json', 'Api-Key': key, 'Language': $translate.use()}
                })
            };
            return service;
        }
    ]);