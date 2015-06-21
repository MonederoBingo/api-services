angular
    .module('app')
    .controller('sendActivationEmailCtrl', [
        '$scope', '$http', 'ApiCallService', '$translate', function($scope, $http, ApiCallService, $translate) {

            $scope.formData = {};
            $scope.isProcessing = false;
            $scope.processForm = function() {
                $scope.showMessage = false;
                $scope.isProcessing = true;
                $scope.isError = false;
                ApiCallService.callAuthApi('POST',  'company/send_activation_email/', $scope.formData.email)
                    .success(function(data) {
                        $scope.isProcessing = false;
                        if (data.success) {
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