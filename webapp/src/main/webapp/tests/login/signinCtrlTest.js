describe("sendTempPasswordEmailCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, authRequestHandler, ApiService, $translate, url, Session;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        Session = $injector.get('Session');
        ApiService = $injector.get('ApiService');
        url = ApiService.apiUrl() + 'company_users/login';
        authRequestHandler = $httpBackend.when('POST', url).respond({success: true, 'message': 'xxx', 'object': ''});
    }));

    describe('call login api getting successful response', function() {
        beforeEach(function () {
            spyOn(Session, "create");
            spyOn($translate, "use");
        });
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('signinCtrl', {$scope: scope});
            $httpBackend.expectPOST(url);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.isUserActive).toBe(true);
            expect(scope.credentials).not.toBeUndefined();
            expect(scope.credentials.username).not.toBeUndefined();
            expect(scope.credentials.password).not.toBeUndefined();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('');
            scope.login();
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.isUserActive).toBe(true);
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(Session.create).toHaveBeenCalled();
            expect($translate.use).toHaveBeenCalled();
            expect(scope.isProcessing).toBe(false);
        });
    });

    describe('call login api getting unsuccessful response', function() {
        beforeEach(function () {
            spyOn(Session, "create");
            spyOn($translate, "use");
        });
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond({success: false, 'message': 'error', 'object': {}});
            $controller('signinCtrl', {$scope: scope});
            $httpBackend.expectPOST(url);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.isUserActive).toBe(true);
            expect(scope.credentials).not.toBeUndefined();
            expect(scope.credentials.username).not.toBeUndefined();
            expect(scope.credentials.password).not.toBeUndefined();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('');
            scope.login();
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.isUserActive).toBe(true);
            expect(scope.isProcessing).toBe(true);
            $httpBackend.flush();
            expect(Session.create).toHaveBeenCalled();
            expect($translate.use).toHaveBeenCalled();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('error');
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(true);
            expect(scope.isUserActive).toBe(false);
        });
    });

    describe('call login api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(500, {success: false, 'message': 'error', 'object': {}});
            $controller('signinCtrl', {$scope: scope});
            $httpBackend.expectPOST(url);
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.isUserActive).toBe(true);
            expect(scope.credentials).not.toBeUndefined();
            expect(scope.credentials.username).not.toBeUndefined();
            expect(scope.credentials.password).not.toBeUndefined();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('');
            scope.login();
            expect(scope.showMessage).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.isUserActive).toBe(true);
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