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
            $scope.clients = [];
            ApiService.sendRequestToApi('GET', 'clients/' + Session.user.companyId)
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

            $scope.sendSMSMessage = function(idx) {
                $scope.showMessage = false;
                $scope.isProcessing = true;
                $scope.isError = false;
                ApiService.sendRequestToApi('PUT', 'companies/' + Session.user.companyId + "/" + $scope.clients[idx].client.phone + '/send_promo_sms')
                    .success(function(data) {
                        console.log(data);
                        $scope.isProcessing = false;
                        if (data.success) {
                            $scope.message = data.message;
                            $("#send_button_" + idx).hide();
                        } else {
                            $scope.message = data.message;
                            $scope.isError = true;
                        }
                        $scope.showMessage = true;
                    })
                    .error(function() {
                        $scope.isProcessing = false;
                        $scope.isError = true;
                        $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.showMessage = true;
                    });
            };
        }
    ]);


