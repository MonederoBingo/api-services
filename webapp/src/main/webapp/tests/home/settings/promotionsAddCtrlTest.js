describe("settingsPromotionsAddCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, ApiService, Session, authRequestHandler, $translate, url;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiService = $injector.get('ApiService');
        url = ApiService.apiUrl() + 'promotion_configuration/';
        Session = $injector.get('Session');
        Session.user = {};
        Session.user.companyId = 1;
        authRequestHandler = $httpBackend.when('POST', url)
            .respond({success: true, 'message': 'xxx', 'object': {pointsToEarn:10, requiredAmount:100}});
    }));

    describe('call api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('settingsPromotionsAddCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('xxx');
            expect(scope.formData.description).toBe('');
            expect(scope.formData.requiredPoints).toBe('');
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(false);
        });
    });

    describe('call api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(201, {success: false, 'message': 'error', 'object': {}});
            $controller('settingsPromotionsAddCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('error');
            expect(scope.formData.description).toBeUndefined();
            expect(scope.formData.requiredPoints).toBeUndefined();
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(true);
        });
    });

    describe('call api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(500, {success: false, 'message': 'error', 'object': {}});
            $controller('settingsPromotionsAddCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.isError).toBe(true);
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
            expect(scope.showMessage).toBe(true);
        });
    });

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
});