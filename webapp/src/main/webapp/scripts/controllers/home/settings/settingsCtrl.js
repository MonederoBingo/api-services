angular
    .module('app')
    .controller('settingsCtrl', ['$scope', '$window', 'Session', function ($scope, $window, Session) {
        if(Session.isClosed()) {
            $window.location.href = "/#/";
        }
}]);