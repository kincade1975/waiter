angular.module('App')

// ====================================================================================
//	TopnavbarController
// ====================================================================================
.controller('TopnavbarController', function($scope, $http, $window, $state, $log, $location) {
	
	$scope.logout = function() {
		$http.post('/logout', {})
			.success(function() {
				$window.location.href = '';
			})
			.error(function(data) {
				$log.error(data);
			});
	};
	
})

// ====================================================================================
//	NavigationController
// ====================================================================================
.controller('NavigationController', function($rootScope, $scope) {

})

// ====================================================================================
//	HomeController
// ====================================================================================
.controller('HomeController', function($rootScope, $scope, $state) {
	$state.go(($rootScope.principal.superadmin || $rootScope.principal.admin) ? 'dashboard.overview' : 'orders');
});