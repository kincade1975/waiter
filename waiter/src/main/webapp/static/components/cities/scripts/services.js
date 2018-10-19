angular.module('App')

.service('CitiesService', function($http) {
	this.findAll = function() {
		return $http.get('/api/v1/cities');
	};
	this.find = function(id) {
		return $http.get('/api/v1/cities/' + id);
	};
	this.save = function(resource) {
		return $http.post('/api/v1/cities', resource);
	};
	this.delete = function(id) {
		return $http.delete('/api/v1/cities/' + id);
	};
	this.page = function(resource) {
		return $http.post('/api/v1/cities/page', resource);
	};
});