angular
    .module('app')
    .controller('promotionsCtrl', [
        '$scope', '$http', 'ApiService', 'Session', '$window', '$translate', 'PromotionsService', '$rootScope',
        function($scope, $http, ApiService, Session, $window, $translate, PromotionsService, $rootScope) {
            if(Session.isClosed()) {
                $window.location.href = "/#/";
            }
            $scope.formData = {};
            $scope.applyData = {};
            $scope.showMessage = false;
            $scope.isProcessing = false;
            $scope.promotions = {};
            $scope.processSearchForm = function() {
                $scope.showMessage = false;
                $scope.isProcessing = true;
                $scope.isError = false;
                $scope.isWarning = false;
                ApiService.sendRequestToApi('GET', 'promotion_configuration/' + Session.user.companyId + "/" + $scope.formData.phone, $scope.formData)
                    .success(function(data) {
                        $scope.isProcessing = false;
                        if (data.success) {
                            $scope.message = data.message.message;
                            $scope.promotions = data.object;
                            if($scope.promotions.length == 0) {
                                $scope.isWarning = true;
                                $scope.showMessage = true;
                            } else{
                                PromotionsService.getClientPoints($scope.formData.phone);
                            }
                        } else {
                            $scope.message = data.message.message;
                            $scope.isError = true;
                            $scope.showMessage = true;
                        }

                    })
                    .error(function() {
                        $scope.isProcessing = false;
                        $scope.isError = true;
                        $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.showMessage = true;
                    });
            };
            $scope.processApplyForm = function(idx) {
                $scope.showMessage = false;
                $scope.isProcessing = true;
                $scope.isError = false;
                $scope.applyData.promotionConfigurationId = $scope.promotions[idx].promotionConfigurationId;
                $scope.applyData.phone = $scope.formData.phone;
                $scope.applyData.companyId = Session.user.companyId;
                ApiService.sendRequestToApi('POST', 'promotions', $scope.applyData)
                    .success(function(data) {
                        $scope.isProcessing = false;
                        if (data.success) {
                            $scope.message = data.message.message;
                            PromotionsService.getClientPoints($scope.applyData.phone);
                            $scope.promotions.splice(idx, 1);
                            $scope.formData.phone = '';
                        } else {
                            $scope.message = data.message.message;
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