describe("signupCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, authRequestHandler, ApiService, $translate, url, Session;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        Session = $injector.get('Session');
        ApiService = $injector.get('ApiService');
        url = ApiService.apiUrl() + 'companies';
        authRequestHandler = $httpBackend.when('POST', url).respond({success: true, 'message': 'xxx', 'object': ''});
    }));

    describe('call sign up api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('signupCtrl', {$scope: scope});
            expect(scope.formData).toBeDefined();
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
            expect(scope.formData.userName).toBe('Admin');
            expect(scope.formData.urlImageLogo).toBe('');
            expect(scope.formData.language).toBe($translate.use());
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('xxx');
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(false);

        });
    });

    describe('call sign up api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond({success: false, 'message': 'error', 'object': {}});
            $controller('signupCtrl', {$scope: scope});
            expect(scope.formData).toBeDefined();
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
            expect(scope.formData.userName).toBe('Admin');
            expect(scope.formData.urlImageLogo).toBe('');
            expect(scope.formData.language).toBe($translate.use());
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('error');
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(true);

        });
    });

    describe('call sign up api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(500, {success: false, 'message': 'error', 'object': {}});
            $controller('signupCtrl', {$scope: scope});
            expect(scope.formData).toBeDefined();
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
            expect(scope.formData.userName).toBe('Admin');
            expect(scope.formData.urlImageLogo).toBe('');
            expect(scope.formData.language).toBe($translate.use());
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
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