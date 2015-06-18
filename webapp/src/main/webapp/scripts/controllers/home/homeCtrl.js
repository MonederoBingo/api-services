angular
    .module('app')
    .controller('homeCtrl', [
        'Session', '$window', function(Session, $window) {
            if(Session.isClosed()) {
                $window.location.href = "/#/";
            }
        }
    ]);
