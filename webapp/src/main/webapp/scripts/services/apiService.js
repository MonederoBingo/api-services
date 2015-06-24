angular
    .module('app')
    .factory('ApiService', [
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

            service.authApiUrl = function (){
                var isProdEnvironment = $location.host() === 'www.neerpoints.com';
                var url = 'http://localhost:9090/auth/';
                if(isProdEnvironment) {
                    url = 'http://services-neerpoints.rhcloud.com/auth/';
                }
                return url;
            };

            service.callApi = function(method, path, data) {
                var userId = Session.user ? Session.user.companyUserId : '';
                var apiKey = Session.user ? Session.user.apiKey : '';
                console.log(Session.user);
                return $http({
                    method: method,
                    url: service.apiUrl() + path,
                    data: data,
                    headers: {'Content-Type': 'application/json', 'Api-Key': apiKey, 'User-Id': userId, 'Language': $translate.use()}
                })
            };

            service.callAuthApi = function(method, path, data) {
                var key = Session.user ? Session.user.apiKey : '';
                return $http({
                    method: method,
                    url: service.authApiUrl() + path,
                    data: data,
                    headers: {'Content-Type': 'application/json', 'Language': $translate.use()}
                })
            };
            return service;
        }
    ]);