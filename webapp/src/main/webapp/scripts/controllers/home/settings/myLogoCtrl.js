angular
    .module('app')
    .controller('myLogoCtrl', [
        '$scope', '$http', 'ApiService', 'Session', '$window', '$translate', '$location',
        function($scope, $http, ApiService, Session, $window, $translate, $location) {
            if(Session.isClosed()) {
                $window.location.href = "/#/";
            }
            $scope.isProcessing = false;
            getLogo();
            function getLogo() {
                $scope.logoUrl = ApiService.authApiUrl() + "company/logo/" + Session.user.companyId + "?" + new Date().getTime();
            }

            $scope.processForm = function() {
                $scope.isProcessing = true;
                $scope.isError = false;
                var file = $scope.myFile;
                var fd = new FormData();
                fd.append('file', file);
                $http({
                    transformRequest: angular.identity,
                    method: 'POST',
                    url: ApiService.apiUrl()  + 'companies/logo/' + Session.user.companyId,
                    data: fd,
                    headers: {'Content-Type': undefined, 'Api-Key': Session.user.apiKey,  'User-Id': Session.user.companyUserId ,
                        'Language': $translate.use()}
                })
                    .success(function(data) {
                        console.log(data);
                        getLogo();
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