describe("navCtrlTest", function() {
    beforeEach(module('app'));
    var $controller;

    beforeEach(inject(function($injector) {
        $controller = $injector.get('$controller');
    }));

    describe('verifying settings tab options', function() {
        it('verifies tab names', function() {
            var scope = {};
            $controller('navCtrl', {$scope: scope});
            expect(scope.isActive('')).toBe(true);
        });
    });
});