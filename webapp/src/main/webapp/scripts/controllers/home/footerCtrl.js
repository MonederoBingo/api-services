angular
    .module('app')
    .controller('footerCtrl', [
        '$scope', 'Session', function($scope, Session) {
            $scope.companyName = Session.user ? Session.user.companyName : '';
        }
    ]);