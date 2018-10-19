angular.module('App')

.service('SessionsService', function($http) {
	this.findActiveSessions = function() {
		return $http.get('/api/v1/sessions');
	};
});