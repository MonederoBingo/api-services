angular
    .module('app')
    .controller('sendTempPasswordEmailCtrl', [
        '$scope', '$http', 'ApiService', '$translate', function($scope, $http, ApiService, $translate) {

            $scope.isProcessing = false;
            $scope.processForm = function() {
                $scope.showMessage = false;
                $scope.isProcessing = true;
                $scope.isError = false;
                ApiService.sendRequest('POST',  'company/send_temp_password_email', $scope.email)
                    .success(function(data) {
                        console.log(data);
                        $scope.isProcessing = false;
                        if (data.success) {
                            $scope.formData = {};
                            $scope.message = data.message;
                        } else {
                            $scope.message = data.message;
                            $scope.isError = true;
                        }
                        $scope.showMessage = true;
                    })
                    .error(function() {
                        $scope.message = $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.isError = true;
                        $scope.showMessage = true;
                        $scope.isProcessing = false;
                    });

            };
        }

    ]);