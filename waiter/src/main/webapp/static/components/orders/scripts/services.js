angular.module('App')

.service('OrdersService', function($http) {
	this.find = function(id) {
		return $http.get('/api/v1/orders/' + id);
	};
	this.delete = function(id) {
		return $http.delete('/api/v1/orders/' + id);
	};
	this.page = function(resource) {
		return $http.post('/api/v1/orders/page', resource);
	};
	this.setStatus = function(id, status) {
		return $http.get('/api/v1/orders/set-status/' + id + "/" + status);
	};
});