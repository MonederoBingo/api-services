angular
    .module('app')
    .controller('signinCtrl', [
        '$scope', '$http', '$rootScope', '$window', 'authEvents', 'AuthService', '$translate', 'Session', 'ApiCallService',
        function($scope, $http, $rootScope, $window, authEvents, AuthService, $translate, Session, ApiCallService) {
            $scope.showMessage = false;
            $scope.isError = false;
            $scope.isUserActive = true;
            $scope.credentials = {
                username: '',
                password: ''
            };
            $scope.isProcessing = false;
            $scope.message = '';
            $scope.login = function(credentials) {
                $scope.showMessage = false;
                $scope.isError = false;
                $scope.isUserActive = true;
                $scope.isProcessing = true;
                ApiCallService.callApi('POST',  'company_users/login', credentials)
                    .success(function(data) {
                        $scope.isProcessing = false;
                        if (data.success) {
                            Session.create(data.object);
                            $translate.use(data.object.language);
                            if (data.object.mustChangePassword) {
                                $window.location.href = "/#/change_password";
                            } else{
                                $window.location.href = "/#/points";
                            }
                        } else {
                            $scope.message = data.message;
                            $scope.showMessage = true;
                            $scope.isError = true;
                            if(data.object && !data.object.isActive){
                                $scope.isUserActive = false;
                            }
                        }
                    })
                    .error(function() {
                        $scope.isProcessing = false;
                        $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.showMessage = true;
                        $scope.isError = true;
                    });

            };
        }
    ]);
