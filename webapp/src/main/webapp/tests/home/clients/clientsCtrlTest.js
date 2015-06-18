describe("clientsCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, ApiCallService, Session, authRequestHandler, $translate, url;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiCallService = $injector.get('ApiCallService');
        url = ApiCallService.apiUrl() + 'clients/1';
        Session = $injector.get('Session');
        Session.user = {};
        Session.user.companyId = 1;
        authRequestHandler = $httpBackend.when('GET', url)
            .respond({success: true, 'message': 'xxx', 'object': 'clients'});

    }));

    describe('call api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $httpBackend.expectGET(url);
            $controller('clientsCtrl', {$scope: scope, ApiCallService: ApiCallService, Session: Session});
            expect(scope.isProcessing).toBe(true);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.message).toBe('');
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.clients).toBe('clients');
            expect(scope.message).toBe('');
        });
    });

    describe('call api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(201, {success: false, 'message': 'error', 'object': {}});
            $httpBackend.expectGET(url);
            $controller('clientsCtrl', {$scope: scope, ApiCallService: ApiCallService, Session: Session});
            expect(scope.isProcessing).toBe(true);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.message).toBe('');
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(true);
            expect(scope.clients).toBeUndefined();
            expect(scope.message).toBe('error');
        });
    });

    describe('call api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(500, {success: false, 'message': 'error', 'object': {}});
            $httpBackend.expectGET(url);
            $controller('clientsCtrl', {$scope: scope, ApiCallService: ApiCallService, Session: Session});
            expect(scope.isProcessing).toBe(true);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.message).toBe('');
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(true);
            expect(scope.clients).toBeUndefined();
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
        });
    });

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
});