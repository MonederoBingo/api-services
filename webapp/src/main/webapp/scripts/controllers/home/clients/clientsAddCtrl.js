angular
    .module('app')
    .controller('clientsAddCtrl', [
        '$scope', '$http', 'ApiCallService', 'Session','$window', '$translate', function($scope, $http, ApiCallService, Session, $window, $translate) {
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
                ApiCallService.callApi('POST', 'clients/', $scope.formData)
                    .success(function(data) {
                        console.log(data);
                        $scope.isProcessing = false;
                        if (data.success) {
                            $scope.message = data.message;
                        } else {
                            $scope.message = data.message;
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

