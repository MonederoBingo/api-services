angular
    .module('app')
    .controller('logoutCtrl', [
        '$window', 'AuthService',
        function($window, AuthService) {
            AuthService.logout();
        }
    ]);