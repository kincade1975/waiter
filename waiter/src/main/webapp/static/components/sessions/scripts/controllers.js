angular.module('App')
	.controller('SessionsOverviewController', SessionsOverviewController);

// ========================================================================
//	OVERVIEW CONTROLLER
// ========================================================================
function SessionsOverviewController($rootScope, $scope, $state, $log, $timeout, $interval, $filter, $localStorage, toaster, uiGridConstants, SessionsService) {
	
	$scope.gridOptions = {
		rowHeight: $rootScope.uiGridRowHeight,
		paginationPageSize: $rootScope.uiGridPageSize,
		paginationPageSizes: $rootScope.uiGridPageSizes,
		enableFiltering: true,
		useExternalFiltering: false,
		useExternalSorting: false,
		useExternalPagination: false,
		enableColumnMenus: false,
		enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER,
		enableVerticalScrollbar: uiGridConstants.scrollbars.NEVER,
		enableGridMenu: false,
		columnDefs: [
			{
				displayName: 'Session ID',
				field: 'id',
				type: 'string',
				cellTooltip: false, 
				enableSorting: true,
				enableFiltering: true,
				enableHiding: false,
				filterHeaderTemplate: 'ui-grid/ui-grid-filter-app'
			}, 
			{
				displayName: 'User',
				field: 'username',
				type: 'string',
				cellTooltip: false, 
				enableSorting: true,
				enableFiltering: true,
				enableHiding: false,
				filterHeaderTemplate: 'ui-grid/ui-grid-filter-app'
			},
			{
				displayName: 'Max inactive interval',
				field: 'maxInactiveIntervalInSeconds',
				type: 'number',
				cellTooltip: false, 
				enableSorting: false,
				enableFiltering: false,
				enableHiding: false,
				width: 150,
				visible: false
			},
			{
				displayName: 'Expired',
				field: 'expired',
				type: 'boolean',
				cellTooltip: false, 
				enableSorting: true,
				enableFiltering: true,
				enableHiding: false,
				filter: {
					type: uiGridConstants.filter.SELECT,
					disableCancelFilterButton: true,
					selectOptions: [
						{ value: 'true', label: ('Yes') },
						{ value: 'false', label: ('No') }]
				},
				width: 100,
				cellTemplate:
					'<div ng-show="row.entity.expired" class="ui-grid-cell-contents" style="text-align: center;">' + 
						'<span class="fa fa-check-square-o"></span>' +
					'</div>' + 
					'<div ng-show="!row.entity.mvno" class="ui-grid-cell-contents" style="text-align: center;">' +
						'<span class="fa fa-square-o"></span>' +
					'</div>',
				visible: false
			},
			{
				displayName: 'Created',
				field: 'creationDate',
				sort: { direction: 'desc', priority: 0 },
				type: 'date',
				cellFilter: 'date:grid.appScope.dateTimeFormat',
				cellTooltip: false, 
				enableSorting: true,
				enableFiltering: false,
				filterHeaderTemplate: 
					'<div class="ui-grid-filter-container" ng-repeat="colFilter in col.filters">' +
						'<div class="input-group">' +
							'<input id="filterCreationDate" date-range-picker options="grid.appScope.dpOptions" class="form-control date-picker ui-grid-filter-datepicker" type="text" readonly="readonly" style="background: white;" ng-model="grid.appScope.dpCreationDate" />' +
							'<span class="input-group-addon ui-grid-filter-datepicker-span" ng-click="grid.appScope.clearDateFilter(\'filterCreationDate\')"><i class="ui-grid-icon-cancel ui-grid-filter-datepicker-i"></i></span>' +
						'</div>' + 
					'</div>',
				enableHiding: false,
				width: 175
			},
			{
				displayName: 'Last accessed',
				field: 'lastModifiedDate',
				type: 'date',
				cellFilter: 'date:grid.appScope.dateTimeFormat',
				cellTooltip: false, 
				enableSorting: true,
				enableFiltering: false,
				filterHeaderTemplate: 
					'<div class="ui-grid-filter-container" ng-repeat="colFilter in col.filters">' +
						'<div class="input-group">' +
							'<input id="filterLastModifiedDate" date-range-picker options="grid.appScope.dpOptions" class="form-control date-picker ui-grid-filter-datepicker" type="text" readonly="readonly" style="background: white;" ng-model="grid.appScope.dpLastModifiedDate" />' +
							'<span class="input-group-addon ui-grid-filter-datepicker-span" ng-click="grid.appScope.clearDateFilter(\'filterLastModifiedDate\')"><i class="ui-grid-icon-cancel ui-grid-filter-datepicker-i"></i></span>' +
						'</div>' + 
					'</div>',
				enableHiding: false,
				width: 175
			},
			{
				displayName: 'Duration',
				field: 'duration',
				type: 'string',
				cellTooltip: false, 
				enableSorting: true,
				enableFiltering: false,
				enableHiding: false,
				width: 100,
				filterHeaderTemplate: 'ui-grid/ui-grid-filter-app'
			},
		],
		onRegisterApi: function(gridApi) {
			$scope.gridApi = gridApi;
			
			// register filter changed handler
			$scope.gridApi.core.on.filterChanged($scope, function() {
				$rootScope.saveGridState($state.current.name, $scope.gridApi);
			});
			
			// register sort changed handler 
			$scope.gridApi.core.on.sortChanged($scope, function() {
				$rootScope.saveGridState($state.current.name, $scope.gridApi);
			});
			
			// register column visibility changed handler
			$scope.gridApi.core.on.columnVisibilityChanged($scope, function() {
				$rootScope.saveGridState($state.current.name, $scope.gridApi);
			});
			
			$scope.gridApi.core.on.rowsRendered($scope, function(b, f, i) {
				var newHeight = ($scope.gridApi.core.getVisibleRows($scope.gridApi.grid).length * $rootScope.uiGridRowHeight) 
					+ (($scope.gridOptions.totalItems == 0) ? 91 : $rootScope.uiGridHeightCorrectionFactor);
				angular.element(document.getElementsByClassName('grid')[0]).css('height', newHeight + 'px');
			});				
		}
	};
	
	// date picker options
	var rangesJSON = {};
	rangesJSON['Today'] = [moment(), moment()];
	rangesJSON['Yesterday'] = [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
	rangesJSON['Last 7 days'] = [moment().subtract(6, 'days'), moment()],
	rangesJSON['Last 30 days'] = [moment().subtract(29, 'days'), moment()],
	rangesJSON['This month'] = [moment().startOf('month'), moment().endOf('month')],
	rangesJSON['Last month'] = [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
	
	$scope.dpOptions = {
		opens: 'left',
		format: $rootScope.dateFormat.toUpperCase(),
		ranges: rangesJSON,
		locale: { 
			customRangeLabel: 'Custom range' 
		}, 
		showDropdowns: true,
		eventHandlers: {
			'apply.daterangepicker': function(ev, picker) {
				$scope.applyDateFilter();
			}
		}
	};

	$scope.clearDateFilter = function(field) {
		$('#' + field).val(null);
		$scope.applyDateFilter();	
	};

	$scope.applyDateFilter = function() {
		setTimeout(function () {
			$scope.$apply(function() {
				$scope.gridApi.core.raise.filterChanged();
			});
		}, 100);
	};
	
	$scope.findActiveSessions = function() {
		SessionsService.findActiveSessions()
			.success(function(data, status, headers, config) {
				$scope.gridOptions.data = data;
				$scope.gridOptions.totalItems = data.length;
				
				$timeout(function() {
					if ($state.current.name != undefined 
							&& $localStorage.gridStates != undefined 
							&& $localStorage.gridStates[$state.current.name] != undefined 
							&& $scope.gridApi != undefined && $scope.gridApi.saveState != undefined) {
						$rootScope.restoreGridState($state.current.name, $scope.gridApi);
					}
				}, 500);
			})
			.error(function(data, status, headers, config) {
				toaster.error({ title: "Getting active sessions failed", body: data.message });
			});
	};
	
	// initial load
	$timeout(function() {
		if ($state.current.name != undefined 
				&& $localStorage.gridStates != undefined 
				&& $localStorage.gridStates[$state.current.name] != undefined 
				&& $scope.gridApi != undefined && $scope.gridApi.saveState != undefined) {
			$log.info("Restoring grid state...");
			$rootScope.restoreGridState($state.current.name, $scope.gridApi);
		}
		
		$scope.findActiveSessions();
	}, 500);
	
	// store the interval promise in this variable
	var promise;
	
	// starts the interval
	$scope.start = function() {
		// stops any running interval to avoid two intervals running at the same time
		$scope.stop(); 
		
		// store the interval promise
		promise = $interval(function() { 
			$scope.findActiveSessions();
	    }, 5000);
    }
	
	// stops the interval
	$scope.stop = function() {
		$interval.cancel(promise);
	};

	// starting the interval by default
	$scope.start();
	
	// stops the interval when the scope is destroyed, this usually happens when a route is changed and the $scope gets destroyed
	// the destruction of the scope does not guarantee the stopping of any intervals, you must be responsible for stopping it when the scope is destroyed
	$scope.$on('$destroy', function() {
		$scope.stop();
	});
};