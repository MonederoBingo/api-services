angular
    .module('app', [
        'ui.router', 'translation', 'ngSanitize'
    ])
    .config([
        '$urlRouterProvider', '$stateProvider', '$translateProvider', function($urlRouteProvider, $stateProvider, $translateProvider) {
            $urlRouteProvider.otherwise('/');

            $stateProvider
                .state('welcome', {
                    url: '/',
                    views: {
                        content: {
                            templateUrl: '/templates/login/welcome.html',
                            controller: 'signinCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('signin', {
                    url: '/signin',
                    views: {
                        content: {
                            templateUrl: '/templates/login/signin.html',
                            controller: 'signinCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('logout', {
                    url: '/',
                    controller: 'logoutCtrl',
                    views:{
                        content: {
                            controller: 'logoutCtrl'
                        }
                    }
                })
                .state('signup', {
                    url: '/signup',
                    views: {
                        content: {
                            templateUrl: '/templates/login/signup.html',
                            controller: 'signupCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('change_password', {
                    url: '/change_password',
                    views: {
                        content: {
                            templateUrl: '/templates/login/change_password.html',
                            controller: 'changePasswordCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('send_activation_email', {
                    url: '/send_activation_email',
                    views: {
                        content: {
                            templateUrl: '/templates/login/send_activation_email.html',
                            controller: 'sendActivationEmailCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('send_temp_password_email', {
                    url: '/send_temp_password_email',
                    views: {
                        content: {
                            templateUrl: '/templates/login/send_temp_password_email.html',
                            controller: 'sendTempPasswordEmailCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('activate', {
                    url: '/activate',
                    views: {
                        content: {
                            templateUrl: '/templates/login/activate.html',
                            controller: 'activateCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('home', {
                    url: '/home',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/home.html',
                            controller: 'homeCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('clients', {
                    url: '/clients',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/clients/clients.html',
                            controller: 'clientsCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('settings', {
                    url: '/settings',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/settings/settings.html'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('clients_add', {
                    url: '/clients/add',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/clients/add.html',
                            controller: 'clientsAddCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('settings_points_configuration', {
                    url: '/settings/points_configuration',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/settings/points/points_configuration.html',
                            controller: 'settingsPointsConfigurationCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('settings_promotions', {
                    url: '/settings/promotions',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/settings/promotions/promotions.html',
                            controller: 'settingsPromotionsCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('settings_promotions_add', {
                    url: '/settings/promotions/add',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/settings/promotions/add.html',
                            controller: 'settingsPromotionsAddCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('settings_users', {
                    url: '/settings/users',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/settings/users/users.html'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('settings_users_add', {
                    url: '/settings/users/add',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/settings/users/add.html'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('settings_my_logo', {
                    url: '/settings/my_logo',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/settings/my_logo/my_logo.html',
                            controller: 'myLogoCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('points', {
                    url: '/points',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/points.html',
                            controller: 'pointsCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                })
                .state('promotions', {
                    url: '/promotions',
                    views: {
                        header: {
                            templateUrl: '/templates/home/nav.html',
                            controller: 'navCtrl'
                        },
                        content: {
                            templateUrl: '/templates/home/promotions.html',
                            controller: 'promotionsCtrl'
                        },
                        footer: {
                            templateUrl: '/templates/home/footer.html',
                            controller: 'footerCtrl'
                        }
                    }
                });
        }
    ]);