angular
    .module('app')
    .controller('myLogoCtrl', [
        '$scope', '$http', 'ApiCallService', 'Session', '$window', '$translate', '$location',
        function($scope, $http, ApiCallService, Session, $window, $translate, $location) {
            if(Session.isClosed()) {
                $window.location.href = "/#/";
            }
            $scope.isProcessing = false;
            $scope.logoUrl = ApiCallService.apiUrl() + "companies/logo/" + Session.user.companyId + "?" + new Date().getTime();
            $scope.processForm = function() {
                $scope.isProcessing = true;
                $scope.isError = false;
                var file = $scope.myFile;
                var fd = new FormData();
                fd.append('file', file);
                $http({
                    transformRequest: angular.identity,
                    method: 'POST',
                    url: ApiCallService.apiUrl()  + 'companies/logo/' + Session.user.companyId,
                    data: fd,
                    headers: {'Content-Type': undefined, 'Api-Key': ApiCallService.apiKey, 'Language': $translate.use()}
                })
                    .success(function(data) {
                        console.log(data);
                        //$scope.logoUrl = ApiCallService.apiUrl() + "companies/logo/" + Session.user.companyId + "?" + new Date().getTime();
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
                        $scope.isProcessing = false;
                        $scope.isError = true;
                        $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.showMessage = true;
                    });
            };

        }
    ]);