angular
    .module('app')
    .service('Session', function($window) {
        this.create = function(object) {
            this.user = object;
            $window.sessionStorage["user"] = JSON.stringify(object)
        };
        this.destroy = function() {
            this.user = null;
            $window.sessionStorage["user"] = null;
        };
        this.isClosed = function() {
            return (this.user == "null" || this.user == null);
        }
    });