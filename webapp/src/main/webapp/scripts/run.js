angular
    .module('app')
    .run(function($window, Session){
        var user = $window.localStorage["user"];
        if(user) {
            Session.create(JSON.parse(user));
        }
    });