angular
    .module('app')
    .controller('clientsAddCtrl', [
        '$scope', '$http', 'ApiService', 'Session','$window', '$translate', function($scope, $http, ApiService, Session, $window, $translate) {
            if(Session.isClosed()) {
                $window.location.href = "/#/";
            }
            $scope.formData = {};
            $scope.isProcessing = false;
            $scope.processForm = function() {
                $scope.showMessage = false;
                $scope.isError = false;
                $scope.formData.companyId = Session.user.companyId;
                $scope.isProcessing = true;
                ApiService.sendRequestToApi('POST', 'clients/', $scope.formData)
                    .success(function(data) {
                        console.log(data);
                        $scope.isProcessing = false;
                        if (data.success) {
                            $scope.message = data.message.message;
                        } else {
                            $scope.message = data.message.message;
                            $scope.isError = true;
                        }
                        $scope.formData.phone = '';
                        $scope.showMessage = true;
                    })
                    .error(function() {
                        $scope.isProcessing = false;
                        $scope.isError = true;
                        $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.showMessage = true;
                    });
            }
        }
    ]);

