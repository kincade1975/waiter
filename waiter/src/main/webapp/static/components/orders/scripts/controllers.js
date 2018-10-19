angular.module('App')
	.controller('OrdersController', OrdersController);

function OrdersController($rootScope, $scope, $state, $log, $timeout, $interval, $filter, $http, $window, $localStorage, toaster, uiGridConstants, OrdersService) {
	var $translate = $filter('translate');
	
	$scope.rowHeight = 50;
	
	$scope.gridOptions = {
		rowHeight: $scope.rowHeight,
		enableFiltering: false,
		useExternalFiltering: false,
		useExternalSorting: false,
		useExternalPagination: false,
		enableRowSelection: true,
		enableRowHeaderSelection: false,
		multiSelect: false,
		enableColumnMenus: false,
		enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER,
		enableVerticalScrollbar: uiGridConstants.scrollbars.ALWAYS,
		columnDefs: [
			{
				displayName: $translate('COLUMN_ID'),
				field: 'id',
				type: 'number',
				enableSorting: true,
				width: 75,
				cellTemplate:
					'<div class="ui-grid-cell-contents order-ui-grid-cell-contents">{{ row.entity.id }}</div>'
			},
			{
				displayName: $translate('COLUMN_CREATION_DATE'),
				field: 'creationDate',
				type: 'date',
				cellFilter: 'date:grid.appScope.dateTimeFormat',
				enableSorting: true,
				sort: { direction: 'desc', priority: 0 },
				width: 175,
				cellTemplate:
					'<div class="ui-grid-cell-contents order-ui-grid-cell-contents">{{ row.entity.creationDate | date : grid.appScope.dateTimeFormat }}</div>'
			},
			{
				displayName: $translate('COLUMN_TABLE'),
				field: 'table.identifier',
				type: 'string',
				enableSorting: true,
				filterHeaderTemplate: 'ui-grid/ui-grid-filter-app',
				cellTemplate:
					'<div class="ui-grid-cell-contents order-ui-grid-cell-contents">{{ row.entity.table.identifier }}</div>'
			},
			{
				displayName: $translate('COLUMN_TYPE'),
				field: 'type',
				type: 'string',
				enableSorting: true,
				filterHeaderTemplate: 'ui-grid/ui-grid-filter-app',
				width: 200,
				cellTemplate:
					'<div ng-show="row.entity.type == \'ORDER\'" class="ui-grid-cell-contents order-ui-grid-cell-contents">{{\'ENUM_ORDER_TYPE_ORDER\' | translate}}</div>' +
					'<div ng-show="row.entity.type == \'CALL_WAITER\'" class="ui-grid-cell-contents order-ui-grid-cell-contents">{{\'ENUM_ORDER_TYPE_CALL_WAITER\' | translate}}</div>' +
					'<div ng-show="row.entity.type == \'REQUEST_BILL\'" class="ui-grid-cell-contents order-ui-grid-cell-contents">{{\'ENUM_ORDER_TYPE_REQUEST_BILL\' | translate}}</div>'					
			},
			{
				displayName: $translate('COLUMN_STATUS'),
				field: 'status',
				type: 'string',
				enableSorting: true,
				filterHeaderTemplate: 'ui-grid/ui-grid-filter-app',
				width: 125,
				cellTemplate:
					'<div ng-show="row.entity.status == \'PENDING\'" class="ui-grid-cell-contents order-ui-grid-cell-contents"><span class="badge badge-warning order-status-badge">{{\'ENUM_ORDER_STATUS_PENDING\' | translate}}</span></div>' +
					'<div ng-show="row.entity.status == \'CLOSED\'" class="ui-grid-cell-contents order-ui-grid-cell-contents"><span class="badge badge-primary order-status-badge">{{\'ENUM_ORDER_STATUS_CLOSED\' | translate}}</span></div>' +
					'<div ng-show="row.entity.status == \'CANCELLED\'" class="ui-grid-cell-contents order-ui-grid-cell-contents"><span class="badge badge-danger order-status-badge">{{\'ENUM_ORDER_STATUS_CANCELLED\' | translate}}</span></div>'					
			}
		],
		onRegisterApi: function(gridApi) {
			$scope.gridApi = gridApi;
			
			$scope.gridApi.selection.on.rowSelectionChanged($scope, function(row) {
				if (row.isSelected) {
					if (row.entity.type == 'ORDER') {
						$scope.showOrder(row.entity);
					} else {
						$scope.selectedOrder = null;
					}
				} else {
					$scope.selectedOrder = null;
				}
			});
			
			$scope.gridApi.core.on.rowsRendered($scope, function(b, f, i) {
				var visibleRows = $scope.gridApi.core.getVisibleRows($scope.gridApi.grid).length;
				var newHeight = (visibleRows * $scope.rowHeight) + 38;	
				
				if (visibleRows == 0) {
					newHeight = 35;
				} else if (visibleRows <= 10) {					
					newHeight = (visibleRows * $scope.rowHeight) + 38;
				} else {
					newHeight = 538;
				}
				
				angular.element(document.getElementsByClassName('grid')[0]).css('height', newHeight + 'px');
			});
		}
	};
	
	$scope.addOrder = function(order) {
		$scope.gridOptions.data.unshift(order);
//		new Audio("sounds/beep.mp3").play();
	}
	
	$scope.showOrder = function(order) {
		$scope.selectedOrder = order;
	};
	
	$scope.closeOrder = function() {
		OrdersService.setStatus($scope.selectedOrder.id, "CLOSED")
			.success(function(data, status) {
				$scope.selectedOrder.status = "CLOSED";
				$scope.selectedOrder = null;
				$scope.gridApi.grid.refresh();
			})
			.error(function(data, status) {
				toaster.error({ title: $translate('MSG_SAVING_DATA_FAILURE'), body: data.message });
			});
	};
	
	$scope.cancelOrder = function() {
		OrdersService.setStatus($scope.selectedOrder.id, "CANCELLED")
			.success(function(data, status) {
				$scope.selectedOrder.status = "CANCELLED";
				$scope.selectedOrder = null;	
				$scope.gridApi.grid.refresh();
			})
			.error(function(data, status) {
				toaster.error({ title: $translate('MSG_SAVING_DATA_FAILURE'), body: data.message });
			});
	};
	
	$scope.getOrders = function() {
		var uiGridResource = { "pagination" : { "page" : 0, "size" : 10000 }, "sort" : [{name: "creationDate", priority: 0, direction: "desc"}], "filter" : null };
		
		OrdersService.page(uiGridResource)
			.success(function(data, status, headers, config) {
				$scope.gridOptions.data = data.data;
				$scope.gridOptions.totalItems = data.total;
				
				// open socket to receive subsequent orders
				$scope.connect();
			})
			.error(function(data, status, headers, config) {
				toaster.error({ title: $translate('MSG_LOADING_DATA_FAILURE'), body: data.message });
			});
	};
	
	$scope.filterReset = function() {
		$scope.selectedStatus = undefined;
		
		$scope.gridApi.grid.columns[4].filters[0] = { term: null };
		$scope.gridOptions.enableFiltering = true;
		$scope.gridApi.grid.refresh();
	};
	
	$scope.filterChanged = function() {
		$scope.selectedOrder = null;
		$scope.gridApi.selection.clearSelectedRows();
		
		if ($scope.selectedStatus) {
			$scope.gridApi.grid.columns[4].filters[0] = { term: $scope.selectedStatus.value };
			$scope.gridOptions.enableFiltering = true;
			$scope.gridApi.grid.refresh();
		} else {
			$scope.gridApi.grid.columns[4].filters[0] = { term: null };
			$scope.gridOptions.enableFiltering = true;
			$scope.gridApi.grid.refresh();
		}
	};
	
	$scope.logout = function() {
		$http.post('/logout', {})
			.success(function() {
				$window.location.href = '';
			})
			.error(function(data) {
				$log.error(data);
			});
	};
	
	// initial load
	$scope.getOrders();
	$scope.selectedOrder = null;
	
	$scope.statuses = [
		{ value : "PENDING", label : $translate('ENUM_ORDER_STATUS_PENDING') },
		{ value : "CLOSED", label : $translate('ENUM_ORDER_STATUS_CLOSED') },
		{ value : "CANCELLED", label : $translate('ENUM_ORDER_STATUS_CANCELLED') }
	];
	$scope.selectedStatus = null;
	
	// ========================================================================
	//	SOCKET
	// ========================================================================
	
	var stompClient = null;
	
	$scope.connect = function() {
		if (stompClient != null) { return; }
		
		var socket = new SockJS($rootScope.principal.properties['stompEndpoint']);
	    stompClient = Stomp.over(socket);
	    stompClient.debug = null;
	    stompClient.connect({}, function (frame) {
	        stompClient.subscribe('/user/queue/send', handleMessage);
	    });
	};
	
	$scope.disconnect = function() {
		if (stompClient == null) { return; }
		
		stompClient.disconnect(function() {
			stompClient = null;
		});
	};
	
	function handleMessage(message) {				
		$timeout(function() {
			var msg = JSON.parse(message.body);
			if (msg.type == "ORDER") {
				$scope.addOrder(JSON.parse(msg.content));
			} else if (msg.type == "CANCEL") {				
				var order = JSON.parse(msg.content);
				$.each($scope.gridOptions.data, function(index, value) {
					if (value.id == order.id) {
						value.status = order.status;
					}
				});
			} else if (msg.type == "CALL_WAITER") {
				$scope.addOrder(JSON.parse(msg.content));
			} else if (msg.type == "REQUEST_BILL") {
				$scope.addOrder(JSON.parse(msg.content));
			}
		}, 500);
	}
	
	$scope.$on('$destroy', function() {
		$scope.disconnect();
	});
};
