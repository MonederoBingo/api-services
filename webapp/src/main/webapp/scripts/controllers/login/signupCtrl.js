angular
    .module('app')
    .controller('signupCtrl', [
        '$scope', 'ApiService', '$translate', function($scope, ApiService, $translate) {
            $scope.formData = {};
            $scope.isProcessing = false;
            $scope.processForm = function() {
                $scope.formData.userName = 'Admin';
                $scope.formData.urlImageLogo = '';
                $scope.formData.language = $translate.use();
                $scope.showMessage = false;
                $scope.isProcessing = true;
                $scope.isError = false;
                ApiService.callAuthApi('POST', 'company/register', $scope.formData)
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
                    .error(function(status) {
                        $scope.isError = true;
                        $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.showMessage = true;
                        $scope.isProcessing = false;
                    });

            };
        }

    ]);