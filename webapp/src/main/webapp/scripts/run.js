angular
    .module('app')
    .run(function($window, Session){
        var user = $window.sessionStorage["user"];
        if(user) {
            Session.create(JSON.parse(user));
        }
    });