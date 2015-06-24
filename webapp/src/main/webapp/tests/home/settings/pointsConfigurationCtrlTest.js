describe("settingsPointsConfigurationCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, ApiService, Session, authRequestHandlerGET, authRequestHandlerPUT, $translate, urlGet, urlPut;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiService = $injector.get('ApiService');
        urlGet = ApiService.apiUrl() + 'points_configuration/1';
        urlPut = ApiService.apiUrl() + 'points_configuration';
        Session = $injector.get('Session');
        Session.user = {};
        Session.user.companyId = 1;
        authRequestHandlerGET = $httpBackend.when('GET', urlGet)
            .respond({success: true, 'message': 'xxx', 'object': {pointsToEarn:10, requiredAmount:100}});
        authRequestHandlerPUT = $httpBackend.when('PUT', urlPut)
            .respond({success: true, 'message': 'xxx', 'object': {pointsToEarn:10, requiredAmount:100}});
    }));

    describe('call getting points api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $httpBackend.expectGET(urlGet);
            $controller('settingsPointsConfigurationCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.formData.pointsToEarn).toBe(10);
            expect(scope.formData.requiredAmount).toBe(100);
        });
    });

    describe('call getting points api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerGET.respond(500, {success: false, 'message': 'error', 'object': {}});
            $httpBackend.expectGET(urlGet);
            $controller('settingsPointsConfigurationCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.formData.pointsToEarn).toBeUndefined();
            expect(scope.formData.requiredAmount).toBeUndefined();
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
        });
    });

    describe('call awarding points api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $httpBackend.expectGET(urlGet);
            $controller('settingsPointsConfigurationCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            $httpBackend.flush();
            $httpBackend.expectPUT(urlPut);
            scope.processForm();
            expect(scope.formData.companyId).toBe(1);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('xxx');
            expect(scope.showMessage).toBe(true);
        });
    });

    describe('call awarding points api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerPUT.respond(201, {success: false, 'message': 'error', 'object': {}});
            $httpBackend.expectGET(urlGet);
            $controller('settingsPointsConfigurationCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            $httpBackend.flush();
            $httpBackend.expectPUT(urlPut);
            scope.processForm();
            expect(scope.formData.companyId).toBe(1);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('error');
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(true);
        });
    });

    describe('call awarding points api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerPUT.respond(500, {success: false, 'message': 'error', 'object': {}});
            $httpBackend.expectGET(urlGet);
            $controller('settingsPointsConfigurationCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            $httpBackend.flush();
            $httpBackend.expectPUT(urlPut);
            scope.processForm();
            expect(scope.formData.companyId).toBe(1);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(true);
        });
    });

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
});