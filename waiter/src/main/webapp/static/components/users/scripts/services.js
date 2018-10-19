angular.module('App')

.service('UsersService', function($http) {
	this.findAll = function() {
		return $http.get('/api/v1/users');
	};
	this.find = function(id) {
		return $http.get('/api/v1/users/' + id);
	};
	this.save = function(resource) {
		return $http.post('/api/v1/users', resource);
	};
	this.activate = function(id) {
		return $http.get('/api/v1/users/' + id + "/activate");
	};
	this.deactivate = function(id) {
		return $http.get('/api/v1/users/' + id + "/deactivate");
	};
	this.delete = function(id) {
		return $http.delete('/api/v1/users/' + id);
	};
	this.page = function(resource) {
		return $http.post('/api/v1/users/page', resource);
	};
});