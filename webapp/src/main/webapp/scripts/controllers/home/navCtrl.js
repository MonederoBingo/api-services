angular
    .module('app')
    .controller('navCtrl', [
        '$scope', '$location', '$window', 'Session', function($scope, $location, $window, Session) {
            if(Session.isClosed()) {
                $window.location.href = "/#/";
            }
            $scope.isActive = function(viewLocation) {
                return viewLocation === $location.path();
            };
            $scope.loc = function() {
                return $location.path();
            };
        }
    ]);
