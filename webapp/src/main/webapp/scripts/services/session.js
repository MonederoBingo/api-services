angular
    .module('app')
    .service('Session', function($window) {
        this.create = function(object) {
            this.user = object;
            $window.localStorage["user"] = JSON.stringify(object)
        };
        this.destroy = function() {
            this.user = null;
            $window.localStorage["user"] = null;
        };
        this.isClosed = function() {
            return (this.user == "null" || this.user == null);
        }
    });