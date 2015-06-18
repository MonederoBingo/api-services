describe("pointsCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, $httpBackend, ApiCallService, Session,
        authRequestHandlerGET, authRequestHandlerPOST, $translate, urlGET, urlPOST, $sce;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        $httpBackend = $injector.get('$httpBackend');
        $translate = $injector.get('$translate');
        ApiCallService = $injector.get('ApiCallService');
        $sce = $injector.get('$sce');
        urlGET = ApiCallService.apiUrl() + 'points_configuration/1';
        urlPOST = ApiCallService.apiUrl() + 'points/';
        Session = $injector.get('Session');
        Session.user = {};
        Session.user.companyId = 1;
        authRequestHandlerGET = $httpBackend.when('GET', urlGET)
            .respond({success: true, 'message': 'xxx', 'object': {"pointsToEarn": 0}});
        authRequestHandlerPOST = $httpBackend.when('POST', urlPOST)
            .respond({success: true, 'message': 'xxx', 'object': 0});

    }));

    describe('call get points configuration api getting no awarding points strategy configured', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $httpBackend.expectGET(urlGET);
            $controller('pointsCtrl', {$scope: scope, ApiCallService: ApiCallService, Session: Session, $sce: $sce});
            expect(scope.showMessage).toBe(false);
            expect(scope.isProcessing).toBe(false);
            $httpBackend.flush();
            expect(scope.isProcessing).toBe(false);
            expect(scope.isWarning).toBe(true);
            expect(scope.showMessage).toBe(true);
            expect(scope.message.toString()).toBe($sce.trustAsHtml(
                $translate.instant('YOU_HAVE_NOT_CONFIGURED_YOUR_POINTS_AWARDING_STRATEGY')).toString());
        });
    });

    describe('call process award points api getting successful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            $httpBackend.expectGET(urlGET);
            $controller('pointsCtrl', {$scope: scope, ApiCallService: ApiCallService, Session: Session, $sce: $sce});
            scope.processForm();
            expect(scope.isWarning).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.formData.companyId).toBe(1);
            expect(scope.isProcessing).toBe(true);
            expect(scope.showMessage).toBe(false);
            $httpBackend.flush();
            expect(scope.isError).toBe(false);
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('xxx');
            expect(scope.isWarning).toBe(true);
            expect(scope.formData.phone).toBe('');
            expect(scope.formData.saleAmount).toBe('');
            expect(scope.formData.saleKey).toBe('');
            expect(scope.showMessage).toBe(true);

        });
    });

    describe('call process award points api getting unsuccessful response', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerPOST.respond(201, {success: false, 'message': 'error', 'object': {}});
            $httpBackend.expectGET(urlGET);
            $controller('pointsCtrl', {$scope: scope, ApiCallService: ApiCallService, Session: Session, $sce: $sce});
            scope.processForm();
            expect(scope.isWarning).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.formData.companyId).toBe(1);
            expect(scope.isProcessing).toBe(true);
            expect(scope.showMessage).toBe(false);
            $httpBackend.flush();
            expect(scope.isError).toBe(true);
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe('error');
            expect(scope.isWarning).toBe(true);
            expect(scope.formData.phone).toBe('');
            expect(scope.formData.saleAmount).toBe('');
            expect(scope.formData.saleKey).toBe('');
            expect(scope.showMessage).toBe(true);

        });
    });

    describe('call process award points api getting http error', function() {
        it('changes scope variables depending on api call', function() {
            var scope = {};
            authRequestHandlerPOST.respond(500, {success: false, 'message': 'error', 'object': {}});
            $httpBackend.expectGET(urlGET);
            $controller('pointsCtrl', {$scope: scope, ApiCallService: ApiCallService, Session: Session, $sce: $sce});
            scope.processForm();
            expect(scope.isWarning).toBe(false);
            expect(scope.isError).toBe(false);
            expect(scope.formData.companyId).toBe(1);
            expect(scope.isProcessing).toBe(true);
            expect(scope.showMessage).toBe(false);
            $httpBackend.flush();
            expect(scope.isError).toBe(true);
            expect(scope.isProcessing).toBe(false);
            expect(scope.message).toBe($translate.instant('AN_ERROR_OCCURRED'));
            expect(scope.showMessage).toBe(true);

        });
    });

    afterEach(function() {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });
});