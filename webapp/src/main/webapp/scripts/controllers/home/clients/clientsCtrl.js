angular
    .module('app')
    .controller('clientsCtrl', [
        '$scope', '$http', 'ApiService', '$window', 'Session', '$translate', function($scope, $http, ApiService, $window, Session, $translate) {
            if(Session.isClosed()) {
                $window.location.href = "/#/";
            }
            $scope.message = '';
            $scope.isProcessing = true;
            $scope.showMessage = false;
            $scope.isError = false;
            ApiService.callApi('GET', 'clients/' + Session.user.companyId)
                .success(function(data) {
                    console.log(data);
                    $scope.isProcessing = false;
                    if (data.success) {
                        $scope.clients = data.object;
                    } else {
                        $scope.message = data.message;
                        $scope.isError = true;
                    }
                })
                .error(function() {
                    $scope.isProcessing = false;
                    $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                    $scope.showMessage = true;
                    $scope.isError = true;
                });

            $scope.go = function(path) {
                $window.location.href = path;
            };
        }
    ]);


