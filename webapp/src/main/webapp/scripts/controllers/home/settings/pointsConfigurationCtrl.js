angular
    .module('app')
    .controller('settingsPointsConfigurationCtrl', [
        '$scope', '$http', 'ApiService', 'Session', '$window', '$translate', function($scope, $http, ApiService, Session, $window, $translate) {
            if(Session.isClosed()) {
                $window.location.href = "/#/";
            }
            $scope.formData = {};
            $scope.showMessage = false;
            $scope.isProcessing = true;
            ApiService.sendRequestToApi('GET', 'points_configuration/' + Session.user.companyId)
                .success(function(data) {
                    console.log(data);
                    $scope.isProcessing = false;
                    $scope.formData.pointsToEarn = data.object.pointsToEarn;
                    $scope.formData.requiredAmount = data.object.requiredAmount;
                })
                .error(function(data) {
                    $scope.isProcessing = false;
                    $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                });

            $scope.processForm = function() {
                $scope.formData.companyId = Session.user.companyId;
                $scope.showMessage = false;
                $scope.isError = false;
                $scope.isProcessing = true;
                ApiService.sendRequestToApi('PUT', 'points_configuration',  $scope.formData)
                    .success(function(data) {
                        console.log(data);
                        $scope.isProcessing = false;
                        if (data.success) {
                            $scope.message = data.message.message;
                        } else {
                            $scope.message = data.message.message;
                            $scope.isError = true;
                        }
                        $scope.showMessage = true;
                    })
                    .error(function(data) {
                        $scope.isProcessing = false;
                        $scope.isError = true;
                        $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.showMessage = true;
                    });
            };
        }
    ]);

