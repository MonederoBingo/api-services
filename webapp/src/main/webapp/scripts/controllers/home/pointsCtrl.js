angular
    .module('app')
    .controller('pointsCtrl', [
        '$scope', '$http', 'ApiCallService', 'Session', '$window', '$translate', '$sce',
        function($scope, $http, ApiCallService, Session, $window, $translate, $sce) {
            if(Session.isClosed()) {
                $window.location.href = "/#/";
            }
            $scope.formData = {};
            $scope.showMessage = false;
            $scope.isProcessing = false;

            ApiCallService.callApi('GET', 'points_configuration/' + Session.user.companyId)
                .success(function(data) {
                    if(data.object.pointsToEarn == 0 ) {
                        $scope.isWarning = true;
                        $scope.showMessage = true;
                        $scope.message = $sce.trustAsHtml($translate.instant('YOU_HAVE_NOT_CONFIGURED_YOUR_POINTS_AWARDING_STRATEGY'));
                    }
                });

            $scope.processForm = function() {
                $scope.isWarning = false;
                $scope.isError = false;
                $scope.formData.companyId = Session.user.companyId;
                $scope.isProcessing = true;
                $scope.showMessage = false;
                ApiCallService.callApi('POST', 'points/', $scope.formData)
                    .success(function(data) {
                        console.log(data);
                        $scope.isProcessing = false;
                        if (data.success) {
                            $scope.message = data.message;
                            if(data.object <= 0) {
                                $scope.isWarning = true;
                            }
                        } else {
                            $scope.message = data.message;
                            $scope.isError = true;
                        }
                        $scope.formData.phone = '';
                        $scope.formData.saleAmount = '';
                        $scope.formData.saleKey = '';
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