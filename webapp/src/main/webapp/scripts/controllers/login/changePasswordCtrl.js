angular
    .module('app')
    .controller('changePasswordCtrl', [
        '$scope', 'ApiService', '$translate', 'Session', '$timeout', function($scope, ApiService, $translate, Session, $timeout) {
            $scope.formData = {};
            $scope.isProcessing = false;
            $scope.isWarning = true;
            $scope.showMessage = true;
            $scope.message = $translate.instant('YOU_MUST_CHANGE_YOUR_PASSWORD');
            console.log(Session.user.email);
            $scope.processForm = function() {
                $scope.formData.email = Session.user.email;
                $scope.showMessage = false;
                $scope.isProcessing = true;
                $scope.isError = false;
                $scope.isWarning = false;
                ApiService.sendRequest('POST', 'company/change_password', $scope.formData)
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