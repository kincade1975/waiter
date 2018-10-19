angular.module('App')

// ========================================================================
//		C O N S T A N T
// ========================================================================
.constant("constants", {
    "version": "1.0.0"
})

// ========================================================================
//		C O N F I G
// ========================================================================
.config(function config($provide, $stateProvider, $httpProvider, $urlRouterProvider, IdleProvider, KeepaliveProvider) {
	// idle settings
	IdleProvider.idle(86400);
	
	// keep alive settings
	KeepaliveProvider.interval(15);
	
	// 'getPrincipal' function
	var getPrincipal = function($rootScope, $window, $http, $log) {
		if (!$rootScope.principal) {
			// get data synchronously
			var request = new XMLHttpRequest();
			request.open('GET', $window.location.pathname.substring(0, window.location.pathname.lastIndexOf("/")) + '/principal', false);
			request.send(null);
			
			if (request.status == 200) {
				$rootScope.principal = jQuery.parseJSON(request.response);
			} else {
				$log.error('Error occurred while getting principal!', status, data);
			}
		}
	};
	
	$urlRouterProvider.when('/', '/home').otherwise('/');
	
	$stateProvider
		
		// ================================================
		// 	home
		// ================================================
		.state('home', {
		    url: "/home",
		    templateUrl: "/common/views/home.html",
		    controller: 'HomeController',
		    onEnter: getPrincipal
		})
	
		// ================================================
		// 	security
		// ================================================
		.state('security', {
		    abstract: true,
		    url: "/security",
		    templateUrl: "/common/views/content.html",
		    onEnter: getPrincipal
		})
		.state('security.denied', {
			url: "/denied",
		    templateUrl: "/common/views/denied.html"
		})
		
		// ================================================
		// 	dashboard
		// ================================================
		.state('dashboard', {
	        abstract: true,
	        url: "/dashboard",
	        templateUrl: "/common/views/content.html",
	        onEnter: getPrincipal,
	        params: { 'permission' : 'dashboard' },
	        resolve: {
	        	hasPermission: function(grant, $stateParams) {
	        		return grant.only({ test: 'hasPermission', state: 'security.denied' }, $stateParams);
	        	},
	        	loadPlugin: function ($ocLazyLoad) {
	        		return $ocLazyLoad.load({
	        			name: 'App',
	        			files: ['components/dashboard/scripts/controllers.js',
	        			        'components/dashboard/scripts/services.js']
	        		});
	        	}
	        }
	    })
	    .state('dashboard.overview', {
	        url: "/overview",
	        templateUrl: "/components/dashboard/views/overview.html",
	        controller: 'DashboardController'
	    })
    
		// ================================================
		// 	users
		// ================================================
		.state('users', {
	        abstract: true,
	        url: "/users",
	        templateUrl: "/common/views/content.html",
	        onEnter: getPrincipal,
	        resolve: {
	        	loadPlugin: function ($ocLazyLoad) {
	        		return $ocLazyLoad.load({
	        			name: 'App',
	        			files: ['components/users/scripts/controllers.js',
								'components/users/scripts/services.js',
								'components/orders/scripts/services.js',
								'components/cities/scripts/services.js']
	        		});
	        	}
	        }
	    })
	    .state('users.overview', {
	        url: "/overview",
	        templateUrl: "/components/users/views/overview.html",
	        controller: 'UsersOverviewController',
	        params: { 'permission' : 'users.read' },
	        resolve: {
	        	hasPermission: function(grant, $stateParams) {
	        		return grant.only({ test: 'hasPermission', state: 'security.denied' }, $stateParams);
	        	}
	        }
	    })
	    .state('users.create', {
	        url: "/create/:id",
	        templateUrl: "/components/users/views/edit.html",
	        controller: 'UsersEditController',
	        params: { 'permission' : 'users.create' },
	        resolve: {
	        	hasPermission: function(grant, $stateParams) {
	        		return grant.only({ test: 'hasPermission', state: 'security.denied' }, $stateParams);
	        	}
	        }
	    })
	    .state('users.edit', {
	        url: "/edit/:id",
	        templateUrl: "/components/users/views/edit.html",
	        controller: 'UsersEditController',
	        params: { 'permission' : 'users.update' },
	        resolve: {
	        	hasPermission: function(grant, $stateParams) {
	        		return grant.only({ test: 'hasPermission', state: 'security.denied' }, $stateParams);
	        	}
	        }
	    })
	    
	    // ================================================
		// 	sessions
		// ================================================
		.state('sessions', {
	        abstract: true,
	        url: "/sessions",
	        templateUrl: "/common/views/content.html",
	        onEnter: getPrincipal,
	        params: { 'permission' : 'sessions' },
	        resolve: {
	        	hasPermission: function(grant, $stateParams) {
	        		return grant.only({ test: 'hasPermission', state: 'security.denied' }, $stateParams);
	        	},
	        	loadPlugin: function ($ocLazyLoad) {
	        		return $ocLazyLoad.load({
	        			name: 'App',
	        			files: ['components/sessions/scripts/controllers.js',
	        			        'components/sessions/scripts/services.js']
	        		});
	        	}
	        }
	    })
	    .state('sessions.overview', {
	        url: "/overview",
	        templateUrl: "/components/sessions/views/overview.html",
	        controller: 'SessionsOverviewController'
	    })
	    
	    // ================================================
		// 	orders
		// ================================================
		.state('orders', {
		    url: "/orders",
		    templateUrl: "/components/orders/views/overview.html",
		    controller: 'OrdersController',
		    onEnter: getPrincipal,
		    params: { 'permission' : 'orders' },
		    resolve: {
	        	hasPermission: function(grant, $stateParams) {
	        		return grant.only({ test: 'hasPermission', state: 'security.denied' }, $stateParams);
	        	},
	        	loadPlugin: function ($ocLazyLoad) {
	        		return $ocLazyLoad.load({
	        			name: 'App',
	        			files: ['components/orders/scripts/controllers.js',
	        			        'components/orders/scripts/services.js']
	        		});
	        	}
	        }
		})
	    
	    // ================================================
		// 	settings
		// ================================================
	    .state('settings', {
	        abstract: true,
	        url: "/settings",
	        templateUrl: "/common/views/content.html",
	        onEnter: getPrincipal,
	    })
	    .state('settings.cities_overview', {
	        url: "/cities/overview",
	        templateUrl: "/components/cities/views/overview.html",
	        controller: 'CitiesOverviewController',
	        params: { 'permission' : 'settings' },
	        resolve: {
	        	hasPermission: function(grant, $stateParams) {
	        		return grant.only({ test: 'hasPermission', state: 'security.denied' }, $stateParams);
	        	},
	        	loadPlugin: function ($ocLazyLoad) {
	        		return $ocLazyLoad.load({
	        			name: 'App',
	        			files: ['components/cities/scripts/controllers.js',
	        			        'components/cities/scripts/services.js']
	        		});
	        	}
	        }
	    })
	    .state('settings.cities_create', {
	        url: "/cities/create/:id",
	        templateUrl: "/components/cities/views/edit.html",
	        controller: 'CitiesEditController',
	        params: { 'permission' : 'settings' },
	        resolve: {
	        	hasPermission: function(grant, $stateParams) {
	        		return grant.only({ test: 'hasPermission', state: 'security.denied' }, $stateParams);
	        	},
	        	loadPlugin: function ($ocLazyLoad) {
	        		return $ocLazyLoad.load({
	        			name: 'App',
	        			files: ['components/cities/scripts/controllers.js',
	        			        'components/cities/scripts/services.js']
	        		});
	        	}
	        }
	    })
	    .state('settings.cities_edit', {
	        url: "/cities/edit/:id",
	        templateUrl: "/components/cities/views/edit.html",
	        controller: 'CitiesEditController',
	        params: { 'permission' : 'settings' },
	        resolve: {
	        	hasPermission: function(grant, $stateParams) {
	        		return grant.only({ test: 'hasPermission', state: 'security.denied' }, $stateParams);
	        	},
	        	loadPlugin: function ($ocLazyLoad) {
	        		return $ocLazyLoad.load({
	        			name: 'App',
	        			files: ['components/cities/scripts/controllers.js',
	        			        'components/cities/scripts/services.js']
	        		});
	        	}
	        }
	    })
})

// ========================================================================
//		R U N
// ========================================================================
.run(function ($rootScope, $state, $stateParams, $templateCache, constants, $locale, $log, $localStorage, $uibModal, $http, $window, grant, Idle) {
	
	$rootScope.$state = $state;
	$rootScope.$stateParams = $stateParams;
	$rootScope.version = constants.version;
	$rootScope.uiGridRowHeight = 30;
	$rootScope.uiGridPageSize = 10;
	$rootScope.uiGridPageSizes = [10, 25, 50, 100];
	$rootScope.uiGridHeightNoData = 300;
	$rootScope.uiGridHeightCorrectionFactor = 93;
	
	$rootScope.dateFormat = "yyyy-MM-dd";
	$rootScope.dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
	
	$locale.NUMBER_FORMATS.DECIMAL_SEP= ',';
	$locale.NUMBER_FORMATS.GROUP_SEP= '.';
	
	// Idle/keep alive settings
	$rootScope.$on('Keepalive', function() {
		$http({
            method: "GET",
            url: $window.location.pathname.substring(0, window.location.pathname.lastIndexOf("/")) + '/ping',
        });
	});
	
	Idle.watch();
	
	// keep track of previous and current state via root scope variables
	$rootScope.previousState;
	$rootScope.currentState;
	$rootScope.$on('$stateChangeSuccess', function(ev, to, toParams, from, fromParams) {
	    $rootScope.previousState = from.name;
	    $rootScope.previousStateParams = fromParams;
	    $rootScope.currentState = to.name;
	    $rootScope.currentStateParams = toParams;
	});
	
	// custom ui-grid-filter
	$templateCache.put('ui-grid/ui-grid-filter-app',
		"<div class=\"ui-grid-filter-container\" ng-repeat=\"colFilter in col.filters\" ng-class=\"{'ui-grid-filter-cancel-button-hidden' : colFilter.disableCancelFilterButton === true }\">" +
			"<div ng-if=\"colFilter.type !== 'select'\">" + 
				"<input type=\"text\" class=\"input-sm form-control ui-grid-filter-input\" ng-model=\"colFilter.term\" ng-model-options=\"{ debounce : { 'default' : 250, 'blur' : 0 }}\" ng-attr-placeholder=\"{{colFilter.placeholder || ''}}\" aria-label=\"{{colFilter.ariaLabel || aria.defaultFilterLabel}}\"><div role=\"button\" class=\"ui-grid-filter-button\" ng-click=\"removeFilter(colFilter, $index)\" ng-if=\"!colFilter.disableCancelFilterButton\" ng-disabled=\"colFilter.term === undefined || colFilter.term === null || colFilter.term === ''\" ng-show=\"colFilter.term !== undefined && colFilter.term !== null && colFilter.term !== ''\"><i class=\"ui-grid-icon-cancel\" ui-grid-one-bind-aria-label=\"aria.removeFilter\">&nbsp;</i></div>" + 
			"</div>" +
		"</div>"
	);
	
	/**
	 * Function processes date filters (creationDate, lastModifiedDate) from UIGrid and adds them in filter array.
	 */
	$rootScope.processDateFilters = function(creationDateEl, lastModifiedDateEl, filterArray) {
		if (creationDateEl && creationDateEl.val() && creationDateEl.val().length > 0) {
			filterArray.push({ "name" : "creationDate", "term" : creationDateEl.val() });
		}
		if (lastModifiedDateEl && lastModifiedDateEl.val() && lastModifiedDateEl.val().length > 0) {
			filterArray.push({ "name" : "lastModifiedDate", "term" :  lastModifiedDateEl.val() });
		}
	}
	
	/**
	 * Function searches for date filters (creationDate, lastModifiedDate) from given filter array, 
	 * and sets their values to corresponding filter component of UIGrid. 
	 */
	$rootScope.setDateFilters = function(creationDateEl, lastModifiedDateEl, filterArray) {
		for (var i in filterArray) {
			var filter = filterArray[i];
			if (filter.name == "creationDate" && creationDateEl != undefined) {
				creationDateEl.val(filter.term);
			}
			if (filter.name == "lastModifiedDate" && lastModifiedDateEl != undefined) {
				lastModifiedDateEl.val(filter.term);
			}
		}
	}

	/** 
	 * Function saves UIGrid state. 
	 */
	$rootScope.saveGridState = function(view, gridApi, creationDateEl, lastModifiedDateEl) {
		if (view != undefined && gridApi != undefined && gridApi.saveState != undefined) {
			if ($localStorage.gridStates == undefined) {
				$localStorage.gridStates = new Array();
			}
			var gridState = gridApi.saveState.save();
			var dateFilters = new Array();
			$rootScope.processDateFilters(creationDateEl, lastModifiedDateEl, dateFilters);
			
			$localStorage.gridStates[view] = { gridState: gridState, dateFilters: dateFilters };
		}
	}
	
	/** 
	 * Function restores UIGrid state.
	 */
	$rootScope.restoreGridState = function(view, gridApi, creationDateEl, lastModifiedDateEl) {
		if (view != undefined && $localStorage.gridStates != undefined && $localStorage.gridStates[view] != undefined && gridApi != undefined && gridApi.saveState != undefined) {
			var state = $localStorage.gridStates[view];
			gridApi.saveState.restore(null, state.gridState);
			$rootScope.setDateFilters(creationDateEl, lastModifiedDateEl, state.dateFilters);
		}
	}
	
	// permissions
	$rootScope.hasPermission = function(permissions) {
		for (i=0; i<$rootScope.principal.permissions.length; i++) { 
			for (j=0; j<permissions.length; j++) {
				if (permissions[j] === $rootScope.principal.permissions[i]) {
					return true;
				}					
			}
		}
		return false;
	};
	
	grant.addTest('hasPermission', function() {
		if (!$rootScope.principal) {
			// get data synchronously
			var request = new XMLHttpRequest();
			request.open('GET', $window.location.pathname.substring(0, window.location.pathname.lastIndexOf("/")) + '/principal', false);
			request.send(null);
			
			if (request.status == 200) {
				$rootScope.principal = jQuery.parseJSON(request.response);
			} else {
				$log.error('Error occurred while getting principal!', status, data);
			}
		}
		
		var permission = this.stateParams.permission;
		
		return $rootScope.hasPermission([permission]);
	});
	
});