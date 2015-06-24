describe("clientsAddCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, ApiService, Session, authRequestHandler, $translate, url;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiService = $injector.get('ApiService');
        url = ApiService.apiUrl() + 'clients/';
        Session = $injector.get('Session');
        Session.user = {};
        Session.user.companyId = 1;
        authRequestHandler = $httpBackend.when('POST', url).respond({success: true, 'message': 'xxx', 'object': {}});
    }));

    describe('call api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('clientsAddCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
            expect(scope.isProcessing).toBe(true);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isError).toBe(false);
            expect(scope.showMessage).toBe(true);
            expect(scope.formData.phone).toBe('');
            expect(scope.message).toBe('xxx');
        });
    });

    describe('call api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            authRequestHandler.respond(201, {success: false, 'message': 'error', 'object': {}});
            var scope = {};
            $controller('clientsAddCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
            expect(scope.isProcessing).toBe(true);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isError).toBe(true);
            expect(scope.showMessage).toBe(true);
            expect(scope.formData.phone).toBe('');
            expect(scope.message).toBe('error');
        });
    });

    describe('call api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            authRequestHandler.respond(500, {success: false, 'message': 'error', 'object': {}});
            var scope = {};
            $controller('clientsAddCtrl', {$scope: scope, ApiService: ApiService, Session: Session});
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
            expect(scope.isProcessing).toBe(true);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isError).toBe(true);
            expect(scope.isProcessing).toBe(false);
            expect(scope.showMessage).toBe(true);
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
        });
    });

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
});