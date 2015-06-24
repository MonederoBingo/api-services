describe("promotionsCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, Session, authRequestHandlerGET, authRequestHandlerPOST, ApiService,
        PromotionsService, $translate, urlGET, urlPOST, urlGetPoints;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiService = $injector.get('ApiService');
        PromotionsService = $injector.get('PromotionsService');
        urlGET = ApiService.apiUrl() + 'promotion_configuration/1/1234567890';
        urlPOST = ApiService.apiUrl() + 'promotions';
        urlGetPoints = ApiService.apiUrl() + 'clients/1/1234567890';
        Session = $injector.get('Session');
        Session.user = {};
        Session.user.companyId = 1;
        authRequestHandlerGET = $httpBackend.when('GET', urlGET).respond({success: true, 'message': 'xxx', 'object': ''});
        authRequestHandlerPOST = $httpBackend.when('POST', urlPOST).respond({success: true, 'message': 'xxx', 'object': ''});
        $httpBackend.when('GET', urlGetPoints).respond({success: true, 'message': 'xxx', 'object': ''});

    }));

    // Search Form test

    describe('call get points configuration api getting no achieved promotions', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('promotionsCtrl', {$scope: scope, Session: Session});
            scope.formData = {};
            scope.formData.phone = '1234567890';
            expect(scope.showMessage).toBe(false);
            $httpBackend.expectGET(urlGET);
            scope.processSearchForm();
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            expect(scope.isWarning).toBe(false);
            expect(scope.clientPoints).toBe(0);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('xxx');
            expect(scope.promotions.length).toBe(0);
            expect(scope.isWarning).toBe(true);
            expect(scope.showMessage).toBe(true);

        });
    });

    describe('call get points configuration api getting achieved promotions', function() {
        beforeEach(function () {
            spyOn(PromotionsService, "getClientPoints");
        });
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerGET.respond({success: true, 'message': 'xxx', 'object': [
                {"promotionId": 1},
                {"promotionId": 2}
            ]});
            $controller('promotionsCtrl', {$scope: scope, Session: Session});
            scope.formData = {};
            scope.formData.phone = '1234567890';
            expect(scope.showMessage).toBe(false);
            $httpBackend.expectGET(urlGET);
            scope.processSearchForm();
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            expect(scope.isWarning).toBe(false);
            expect(scope.clientPoints).toBe(0);
            $httpBackend.flush();
            expect(PromotionsService.getClientPoints).toHaveBeenCalled();
            expect(scope.promotions.length).toBe(2);
            expect(scope.isError).toBe(false);
        });
    });

    describe('call get points configuration api getting unsuccessful response', function() {
        beforeEach(function () {
            spyOn(PromotionsService, "getClientPoints");
        });
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerGET.respond({success: false, 'message': 'error', 'object': [
                {"promotionId": 1},
                {"promotionId": 2}
            ]});
            $controller('promotionsCtrl', {$scope: scope, Session: Session});
            scope.formData = {};
            scope.formData.phone = '1234567890';
            expect(scope.showMessage).toBe(false);
            $httpBackend.expectGET(urlGET);
            scope.processSearchForm();
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            expect(scope.isWarning).toBe(false);
            expect(scope.clientPoints).toBe(0);
            $httpBackend.flush();
            expect(PromotionsService.getClientPoints).not.toHaveBeenCalled();
            expect(scope.message).toBe('error');
            expect(scope.isError).toBe(true);
            expect(scope.showMessage).toBe(true);
        });
    });

    describe('call get points configuration api getting http error', function() {
        beforeEach(function () {
            spyOn(PromotionsService, "getClientPoints");
        });
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerGET.respond(500, {success: false, 'message': 'error', 'object': [
                {"promotionId": 1},
                {"promotionId": 2}
            ]});
            $controller('promotionsCtrl', {$scope: scope, Session: Session});
            scope.formData = {};
            scope.formData.phone = '1234567890';
            expect(scope.showMessage).toBe(false);
            $httpBackend.expectGET(urlGET);
            scope.processSearchForm();
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            expect(scope.isWarning).toBe(false);
            expect(scope.clientPoints).toBe(0);
            $httpBackend.flush();
            expect(PromotionsService.getClientPoints).not.toHaveBeenCalled();
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
            expect(scope.isError).toBe(true);
            expect(scope.showMessage).toBe(true);
        });
    });

    //Apply form test

    describe('call award points api getting successful response', function() {
        beforeEach(function () {
            spyOn(PromotionsService, "getClientPoints");
            spyOn(Array.prototype, "splice");
        });
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('promotionsCtrl', {$scope: scope, Session: Session});
            scope.promotions = [{'promotionConfigurationId': 1}];
            scope.applyData = {};
            scope.applyData.promotionConfigurationId = 1;
            scope.formData = {};
            scope.formData.phone = '1234567890';

            expect(scope.showMessage).toBe(false);
            $httpBackend.expectPOST(urlPOST);
            scope.processApplyForm(0);
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            expect(scope.applyData.promotionConfigurationId).toBe(1);
            expect(scope.applyData.phone).toBe('1234567890');
            expect(scope.applyData.companyId).toBe(1);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('xxx');
            expect(PromotionsService.getClientPoints).toHaveBeenCalled();
            expect(Array.prototype.splice).toHaveBeenCalled();
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(false);
        });
    });

    describe('call award points api getting http error', function() {
        beforeEach(function () {
            spyOn(PromotionsService, "getClientPoints");
        });
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerPOST.respond(500, {success: true, 'message': 'xxx', 'object': ''});
            $controller('promotionsCtrl', {$scope: scope, Session: Session});
            scope.promotions = [{'promotionConfigurationId': 1}];
            scope.applyData = {};
            scope.applyData.promotionConfigurationId = 1;
            scope.formData = {};
            scope.formData.phone = '1234567890';

            expect(scope.showMessage).toBe(false);
            $httpBackend.expectPOST(urlPOST);
            scope.processApplyForm(0);
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            expect(scope.applyData.promotionConfigurationId).toBe(1);
            expect(scope.applyData.phone).toBe('1234567890');
            expect(scope.applyData.companyId).toBe(1);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
            expect(PromotionsService.getClientPoints).not.toHaveBeenCalled();
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(true);
        });
    });

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
});