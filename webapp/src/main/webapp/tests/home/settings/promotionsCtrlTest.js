describe("settingsPromotionsCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, ApiService, Session, authRequestHandlerGET, authRequestHandlerDELETE, $translate, urlGET, urlDELETE;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiService = $injector.get('ApiService');
        urlGET = ApiService.apiUrl() + 'promotion_configuration/1';
        urlDELETE = ApiService.apiUrl() + 'promotion_configuration/1';
        Session = $injector.get('Session');
        Session.user = {};
        Session.user.companyId = 1;
        authRequestHandlerGET = $httpBackend.when('GET', urlGET)
            .respond({success: true, 'message': 'xxx', 'object': [
                {"promotionConfigurationId": 1},
                {"promotionConfigurationId": 2}
            ]});
        authRequestHandlerDELETE = $httpBackend.when('DELETE', urlDELETE)
            .respond({success: true, 'message': 'the promotion was deleted'});
    }));

    describe('call get promotions api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('settingsPromotionsCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.showMessage).toBe(false);
            $httpBackend.expectGET(urlGET);
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.promotions.length).toBe(2);
            expect(scope.promotions[0].promotionConfigurationId).toBe(1);
            expect(scope.promotions[1].promotionConfigurationId).toBe(2);
        });
    });

    describe('call get promotions api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerGET.respond(500, {success: false, 'message': 'error', 'object': {}});
            $controller('settingsPromotionsCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.showMessage).toBe(false);
            $httpBackend.expectGET(urlGET);
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.isError).toBe(true);
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
            expect(scope.showMessage).toBe(true);
            expect(scope.$scope.formData.phone).toBe('');
        });
    });

    describe('call delete promotion api getting successful response', function() {
        beforeEach(function () {
            spyOn(Array.prototype, "splice");
        });
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('settingsPromotionsCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.showMessage).toBe(false);
            $httpBackend.expectGET(urlGET);
            $httpBackend.flush();
            $httpBackend.expectDELETE(urlDELETE);
            scope.deletePromotion(0, false);
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('the promotion was deleted');
            expect(Array.prototype.splice).toHaveBeenCalled();
            expect(scope.showMessage).toBe(true);
        });
    });

    describe('call delete promotion api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerDELETE.respond({success: false, 'message': 'error', 'object': {}});
            var controller = $controller('settingsPromotionsCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.showMessage).toBe(false);
            $httpBackend.expectGET(urlGET);
            $httpBackend.flush();
            $httpBackend.expectDELETE(urlDELETE);
            scope.deletePromotion(0, false);
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('error');
            expect(scope.isError).toBe(true);
            expect(scope.showMessage).toBe(true);
        });
    });

    describe('call delete promotion api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerDELETE.respond(500, {success: false, 'message': 'error', 'object': {}});
            var controller = $controller('settingsPromotionsCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.showMessage).toBe(false);
            $httpBackend.expectGET(urlGET);
            $httpBackend.flush();
            $httpBackend.expectDELETE(urlDELETE);
            scope.deletePromotion(0, false);
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
            expect(scope.isError).toBe(true);
            expect(scope.showMessage).toBe(true);
        });
    });

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
});