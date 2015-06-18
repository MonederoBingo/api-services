describe("logoutCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, AuthService;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        AuthService = $injector.get('AuthService');
    }));

    describe('call logout from AuthService', function() {
        beforeEach(function () {
            spyOn(AuthService, "logout");
        });
        it('calls AuthService.logout', function() {
            $controller('logoutCtrl', {});
            expect(AuthService.logout).toHaveBeenCalled();
        });
    });
});