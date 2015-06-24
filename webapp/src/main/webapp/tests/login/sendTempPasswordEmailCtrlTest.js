describe("sendTempPasswordEmailCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, authRequestHandler, ApiService, $translate, url;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiService = $injector.get('ApiService');
        url = ApiService.apiUrl() + 'company_users/send_temp_password_email';
        authRequestHandler = $httpBackend.when('POST', url).respond({success: true, 'message': 'xxx', 'object': ''});
    }));

    describe('call sen temp password email api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('sendTempPasswordEmailCtrl', {$scope: scope});
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

    describe('call sen temp password email api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond({success: false, 'message': 'error', 'object': {}});
            $controller('sendTempPasswordEmailCtrl', {$scope: scope});
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

    describe('call send temp password email api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(500, {success: false, 'message': 'error', 'object': {}});
            $controller('sendTempPasswordEmailCtrl', {$scope: scope});
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