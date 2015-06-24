describe("promotionsCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, authRequestHandler, ApiService, $translate, url;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiService = $injector.get('ApiService');
        url = ApiService.apiUrl() + 'company_users/activate/undefined';
        authRequestHandler = $httpBackend.when('GET', url).respond({success: true, 'message': 'xxx', 'object': ''});
    }));

    describe('call get points configuration api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $controller('activateCtrl', {$scope: scope});
            $httpBackend.expectGET(url);
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            expect(scope.formData).not.toBeUndefined();
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('xxx');
            expect(scope.formData.description).toBe('');
            expect(scope.formData.requiredPoints).toBe('');
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(false);

        });
    });

    describe('call get points configuration api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond({success: false, 'message': 'xxx', 'object': ''});
            $controller('activateCtrl', {$scope: scope});
            $httpBackend.expectGET(url);
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            expect(scope.formData).not.toBeUndefined();
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
            expect(scope.showMessage).toBe(true);
            expect(scope.isError).toBe(true);

        });
    });

    describe('call get points configuration api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(500, {success: false, 'message': 'xxx', 'object': ''});
            $controller('activateCtrl', {$scope: scope});
            $httpBackend.expectGET(url);
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            expect(scope.formData).not.toBeUndefined();
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