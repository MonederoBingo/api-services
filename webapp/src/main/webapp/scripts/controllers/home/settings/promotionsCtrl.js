angular
    .module('app')
    .controller('settingsPromotionsCtrl', [
        '$scope', '$http', 'ApiService', 'Session', '$window', '$translate',
        function($scope, $http, ApiService, Session, $window, $translate) {
            if (Session.isClosed()) {
                $window.location.href = "/#/";
            }
            $scope.showMessage = false;
            $scope.promotions = {};
            getPromotions();

            $scope.deletePromotion = function(idx, request_confirm) {
                if(request_confirm) {
                    bootbox.confirm( $translate.instant('DO_YOU_WANT_TO_DELETE_THE_PROMOTION'), function(result) {
                        if (result) {
                            deleteIt(idx);
                        }
                    });
                } else {
                    deleteIt(idx);
                }
            };

            function getPromotions() {
                $scope.isProcessing = true;
                ApiService.sendRequestToApi('GET', 'promotion_configuration/' + Session.user.companyId)
                    .success(function(data) {
                        $scope.isProcessing = false;
                        $scope.promotions = data.object;
                    })
                    .error(function() {
                        $scope.isProcessing = false;
                        $scope.isError = true;
                        $scope.message = $translate.instant('AN_ERROR_OCCURRED');
                        $scope.showMessage = true;
                    });
            }

            function deleteIt(idx) {
                $scope.showMessage = false;
                $scope.isProcessing = true;
                $scope.isError = false;
                ApiService.sendRequestToApi('DELETE', 'promotion_configuration/' + $scope.promotions[idx].promotionConfigurationId)
                    .success(function(data) {
                        console.log(data);
                        $scope.isProcessing = false;
                        if (data.success) {
                            $scope.message = data.message.message;
                            getPromotions();
                            $scope.promotions.splice(idx, 1);
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
            }
        }
    ]);