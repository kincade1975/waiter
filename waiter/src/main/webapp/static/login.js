angular.module('LoginApp', [ 'toaster' ])

.controller('LoginController', function($rootScope, $scope, $http, $window, toaster) {
	if ($window.location.hash.indexOf('logoutSuccess') != -1) {
		$window.location.hash = "";
	}
	
	$scope.login = function() {
		$http.post("/login.html", null, {
			params : { username: $scope.username, password: $scope.password }
		})
		.success(function(data, status, headers, config) {
			if (data.result) {
				$window.location.href = "/";
			} else {
				toaster.error({ 
					title: "Neuspješna prijava", body: data.message 
				});
			}
		})
		.error(function(data, status, headers, config) {
			
		});
	}
});
