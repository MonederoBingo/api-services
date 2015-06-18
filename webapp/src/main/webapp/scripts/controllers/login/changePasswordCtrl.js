angular
    .module('app')
    .controller('changePasswordCtrl', [
        '$scope', 'ApiCallService', '$translate', 'Session', '$timeout', function($scope, ApiCallService, $translate, Session, $timeout) {
            $scope.formData = {};
            $scope.isProcessing = false;
            $scope.processForm = function() {
                $scope.formData.email = Session.email;
                $scope.showMessage = false;
                $scope.isProcessing = true;
                $scope.isError = false;
                ApiCallService.callApi('POST', 'company_users/change_password', $scope.formData)
                    .success(function(data) {
                        $scope.isProcessing = false;
                        if (data.success) {
                            $scope.message = data.message;
                            $timeout(function(){
                                window.location.href = "/#/points";
                            }, 3000);
                        } else {
                            $scope.message = data.message;
                            $scope.isError = true;
                        }
                        $scope.showMessage = true;
                    })
                    .error(function(status) {
                        $scope.isError = true;
                        $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.showMessage = true;
                        $scope.isProcessing = false;
                    });

            };
        }

    ]);