describe("myLogoTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, ApiCallService, Session, authRequestHandler, $translate, url;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiCallService = $injector.get('ApiCallService');
        url = ApiCallService.apiUrl() + 'companies/logo/1';
        Session = $injector.get('Session');
        Session.user = {};
        Session.user.companyId = 1;
        authRequestHandler = $httpBackend.when('POST', url)
            .respond({success: true, 'message': 'xxx', 'object': {}});
    }));

    describe('call api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $httpBackend.expectPOST(url);
            $controller('myLogoCtrl', {$scope: scope, ApiCallService: ApiCallService, Session: Session});;
            expect(scope.isProcessing).toBe(false);
            expect(scope.logoUrl.substring(0, scope.logoUrl.lastIndexOf('?'))).toBe(ApiCallService.apiUrl() + "companies/logo/" + Session.user.companyId);
            scope.processForm();
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('xxx');
            expect(scope.isError).toBe(false);
            expect(scope.showMessage).toBe(true);

        });
    });

    describe('call api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(201, {success: false, 'message': 'error', 'object': {}});
            $httpBackend.expectPOST(url);
            $controller('myLogoCtrl', {$scope: scope, ApiCallService: ApiCallService, Session: Session});;
            expect(scope.isProcessing).toBe(false);
            expect(scope.logoUrl.substring(0, scope.logoUrl.lastIndexOf('?'))).toBe(ApiCallService.apiUrl() + "companies/logo/" + Session.user.companyId);
            scope.processForm();
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('error');
            expect(scope.isError).toBe(true);
            expect(scope.showMessage).toBe(true);

        });
    });


    describe('call api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandler.respond(500, {success: false, 'message': 'error', 'object': {}});
            $httpBackend.expectPOST(url);
            $controller('myLogoCtrl', {$scope: scope, ApiCallService: ApiCallService, Session: Session});;
            expect(scope.isProcessing).toBe(false);
            expect(scope.logoUrl.substring(0, scope.logoUrl.lastIndexOf('?'))).toBe(ApiCallService.apiUrl() + "companies/logo/" + Session.user.companyId);
            scope.processForm();
            expect(scope.isProcessing).toBe(true);
            expect(scope.isError).toBe(false);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
            expect(scope.isError).toBe(true);
            expect(scope.showMessage).toBe(true);
        });
    });


    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
});