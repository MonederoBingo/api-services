angular
    .module('app')
    .controller('activateCtrl', [
        '$scope', '$http', 'ApiCallService', '$location', '$translate', function($scope, $http, ApiCallService, $location, $translate) {
            $scope.showMessage = false;
            $scope.isProcessing = true;
            $scope.isError = false;
            $scope.formData = {};
            ApiCallService.callApi('GET',  'company_users/activate/' + $location.search().key)
                .success(function(data) {
                    $scope.isProcessing = false;
                    if (data.success) {
                        $scope.message = data.message;
                        $scope.formData.description = '';
                        $scope.formData.requiredPoints = '';
                    } else {
                        $scope.message = $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.isError = true;
                    }
                    $scope.showMessage = true;
                })
                .error(function() {
                    $scope.isProcessing = false;
                    $scope.isError = true;
                    $scope.message = $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                    $scope.showMessage = true;
                });
        }
    ]);