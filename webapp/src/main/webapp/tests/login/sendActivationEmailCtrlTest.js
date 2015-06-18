describe("sendActivationEmailCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, authRequestHandler, ApiCallService, $translate, url;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiCallService = $injector.get('ApiCallService');
        url = ApiCallService.apiUrl() + 'company_users/send_activation_email/user@email.com';
        authRequestHandler = $httpBackend.when('POST', url).respond({success: true, 'message': 'xxx', 'object': ''});
    }));

    describe('call activation email api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('sendActivationEmailCtrl', {$scope: scope});
            scope.formData = {};
            scope.formData.email = 'user@email.com';
            expect(scope.formData).toBeDefined();
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
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

    describe('call activation email api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond({success: false, 'message': 'error', 'object': {}});
            $controller('sendActivationEmailCtrl', {$scope: scope});
            scope.formData = {};
            scope.formData.email = 'user@email.com';
            expect(scope.formData).toBeDefined();
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
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

    describe('call activation email api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(500, {success: false, 'message': 'error', 'object': {}});
            $controller('sendActivationEmailCtrl', {$scope: scope});
            scope.formData = {};
            scope.formData.email = 'user@email.com';
            expect(scope.formData).toBeDefined();
            expect(scope.isProcessing).toBe(false);
            $httpBackend.expectPOST(url);
            scope.processForm();
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