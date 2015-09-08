angular
    .module('app')
    .factory('PromotionsService', [
        '$rootScope', 'ApiService', 'Session', '$translate', function($rootScope, ApiService, Session, $translate) {
            var service = {};
            service.getClientPoints = function (phone){
                $rootScope.isProcessing = true;
                ApiService.sendRequestToApi('GET', 'clients/' + Session.user.companyId + "/" + phone, $rootScope.formData)
                    .success(function(data) {
                        $rootScope.isProcessing = false;
                        if (data.success) {
                            $rootScope.clientPoints = data.object;
                        } else {
                            $rootScope.message = data.message;
                            $rootScope.isError = true;
                        }
                    })
                    .error(function() {
                        $rootScope.isProcessing = false;
                        $rootScope.isError = true;
                        $rootScope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $rootScope.showMessage = true;
                    });
            };
            return service;
        }
    ]);