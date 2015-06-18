describe("settingsCtrlTest", function() {
    beforeEach(module('app'));

    var $controller, Session, $window;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
        Session = $injector.get('Session');
        Session.user = {};
        Session.user.companyId = 1;
        $window = $injector.get('$window');
    }));

    describe('verifying settings tab options', function() {
        it('verifies tab names', function() {
            var scope = {};
            $controller('settingsCtrl', {$scope: scope, $window: $window, Session: Session});
        });
    });
});