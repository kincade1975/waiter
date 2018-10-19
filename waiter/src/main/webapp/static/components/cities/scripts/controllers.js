angular.module('App')
	.controller('CitiesOverviewController', CitiesOverviewController)
	.controller('CitiesEditController', CitiesEditController);

// ========================================================================
//	OVERVIEW CONTROLLER
// ========================================================================
function CitiesOverviewController($rootScope, $scope, $state, $log, $timeout, $filter, $localStorage, toaster, uiGridConstants, CitiesService) {
	var $translate = $filter('translate');
	
	$scope.stateRestoreInProgress = false;
	$scope.clearAllFiltersInProgress = false;
	
	$scope.gridOptions = {
			rowHeight: $rootScope.uiGridRowHeight,
			paginationPageSize: $rootScope.uiGridPageSize,
			paginationPageSizes: $rootScope.uiGridPageSizes,
			enableFiltering: true,
			useExternalFiltering: true,
			useExternalSorting: true,
			useExternalPagination: true,
			enableColumnMenus: false,
			enableHorizontalScrollbar: uiGridConstants.scrollbars.NEVER,
			enableVerticalScrollbar: uiGridConstants.scrollbars.NEVER,
			enableGridMenu: true,
			columnDefs: [
				{
					displayName: $translate('COLUMN_ID'),
					field: 'id',
					type: 'number',
					cellTooltip: false, 
					enableSorting: true,
					enableFiltering: true,
					filterHeaderTemplate: 
						'<div class="ui-grid-filter-container" ng-repeat="colFilter in col.filters">' +
							'<input type="number" min="0" class="ui-grid-filter-input ui-grid-filter-input-0 ng-dirty ng-valid-parse ng-touched ui-grid-filter-input-port" ng-model="colFilter.term" ng-attr-placeholder="{{colFilter.placeholder || \'\'}}" aria-label="Filter for column" placeholder="">' + 
						'</div>', 
					enableHiding: true,
					width: 50
				}, 
				{
					displayName: $translate('COLUMN_NAME'),
					field: 'name',
					type: 'string',
					cellTooltip: false, 
					enableSorting: true,
					enableFiltering: true,
					enableHiding: false,
					filterHeaderTemplate: 'ui-grid/ui-grid-filter-app'
				},
				{
					displayName: $translate('COLUMN_POSTAL_CODE'),
					field: 'postalCode',
					type: 'string',
					cellTooltip: false, 
					enableSorting: true,
					enableFiltering: true,
					enableHiding: true,
					filterHeaderTemplate: 'ui-grid/ui-grid-filter-app',
					width: 150
				},
				{
					displayName: $translate('COLUMN_CREATION_DATE'),
					field: 'creationDate',
					type: 'date',
					cellFilter: 'date:grid.appScope.dateTimeFormat',
					cellTooltip: false, 
					enableSorting: true,
					enableFiltering: true,
					filterHeaderTemplate: 
						'<div class="ui-grid-filter-container" ng-repeat="colFilter in col.filters">' +
							'<div class="input-group">' +
								'<input id="filterCreationDate" date-range-picker options="grid.appScope.dpOptions" class="form-control date-picker ui-grid-filter-datepicker" type="text" readonly="readonly" style="background: white;" ng-model="grid.appScope.dpCreationDate" />' +
								'<span class="input-group-addon ui-grid-filter-datepicker-span" ng-click="grid.appScope.clearDateFilter(\'filterCreationDate\')"><i class="ui-grid-icon-cancel ui-grid-filter-datepicker-i"></i></span>' +
							'</div>' + 
						'</div>',
					enableHiding: true,
					width: 175
				},
				{
					displayName: $translate('COLUMN_LAST_MODIFIED_DATE'),
					field: 'lastModifiedDate',
					type: 'date',
					cellFilter: 'date:grid.appScope.dateTimeFormat',
					cellTooltip: false, 
					enableSorting: true,
					enableFiltering: true,
					filterHeaderTemplate: 
						'<div class="ui-grid-filter-container" ng-repeat="colFilter in col.filters">' +
							'<div class="input-group">' +
								'<input id="filterLastModifiedDate" date-range-picker options="grid.appScope.dpOptions" class="form-control date-picker ui-grid-filter-datepicker" type="text" readonly="readonly" style="background: white;" ng-model="grid.appScope.dpLastModifiedDate" />' +
								'<span class="input-group-addon ui-grid-filter-datepicker-span" ng-click="grid.appScope.clearDateFilter(\'filterLastModifiedDate\')"><i class="ui-grid-icon-cancel ui-grid-filter-datepicker-i"></i></span>' +
							'</div>' + 
						'</div>',
					enableHiding: true,
					width: 175
				},
				{
					name: ' ',
					type: 'string',
					cellTooltip: false, 
					enableSorting: false,
					enableFiltering: false,
					enableHiding: false,
					width: 64,
					cellTemplate:
						'<div style="padding-top: 1px">' +
							'<button uib-tooltip="{{\'TOOLTIP_EDIT\' | translate}}" tooltip-append-to-body="true" ng-click="grid.appScope.editEntity(row.entity)" class="app-grid-button btn-xs btn-white"><i class="fa fa-2x fa-edit"></i></button>' +
							'<button uib-tooltip="{{\'TOOLTIP_DELETE\' | translate}}" tooltip-append-to-body="true" ng-click="grid.appScope.deleteEntity(row.entity)" class="app-grid-button btn-xs btn-white"><i class="fa fa-2x fa-times"></i></button>' + 
						'</div>'
				}
			],
			onRegisterApi: function(gridApi) {
				$scope.gridApi = gridApi;
				
				// register pagination changed handler
				$scope.gridApi.pagination.on.paginationChanged($scope, function(currentPage, pageSize) {
					$scope.getPage(currentPage, pageSize);
				});
				
				// register sort changed handler 
				$scope.gridApi.core.on.sortChanged($scope, $scope.sortChanged);
				
				// register filter changed handler
				$scope.gridApi.core.on.filterChanged($scope, function() {
					var grid = this.grid;
					
					var filterArray = new Array();
					for (var i=0; i<grid.columns.length; i++) {
						var name = grid.columns[i].field;
						var term = grid.columns[i].filters[0].term;
						if (name && term) {
							filterArray.push({ "name" : name, "term" : term });
						}
					}
					
					// date filters (e.g. creationDate, lastModifiedDate)
					$rootScope.processDateFilters($('#filterCreationDate'), $('#filterLastModifiedDate'), filterArray);
					
					$scope.filterArray = filterArray;
					$scope.getPage($scope.gridApi.pagination.getPage(), $scope.gridOptions.paginationPageSize);
				});
				
				$scope.gridApi.core.on.rowsRendered($scope, function(b, f, i) {
					var newHeight = ($scope.gridApi.core.getVisibleRows($scope.gridApi.grid).length * $rootScope.uiGridRowHeight) 
						+ (($scope.gridOptions.totalItems == 0) ? $rootScope.uiGridHeightNoData : $rootScope.uiGridHeightCorrectionFactor);
					angular.element(document.getElementsByClassName('grid')[0]).css('height', newHeight + 'px');
				});
				
				$scope.gridApi.core.on.columnVisibilityChanged($scope, function() {
					$rootScope.saveGridState($state.current.name, $scope.gridApi, $('#filterCreationDate'), $('#filterLastModifiedDate'));
				});
				
				$scope.gridApi.grid.clearAllFilters = function() {
					$scope.clearAllFiltersInProgress = true;
					
					this.columns.forEach(function(column) {
						column.filters.forEach(function(filter) {
							filter.term = undefined;
						});
					});
					$scope.clearDateFilter('filterCreationDate');
					$scope.clearDateFilter('filterLastModifiedDate');
					
					$timeout(function() {
						$scope.clearAllFiltersInProgress = false;
						$scope.getPage($scope.gridApi.pagination.getPage(), $scope.gridOptions.paginationPageSize);
					}, 250);
				};
			}
		};
	
	$scope.getPage = function(page, size) {
		if ($scope.stateRestoreInProgress == true) {
			return;
		}
		
		if ($scope.clearAllFiltersInProgress == true) {
			return;
		}
		
		// save current state
		$rootScope.saveGridState($state.current.name, $scope.gridApi, $('#filterCreationDate'), $('#filterLastModifiedDate'));
		
		var uiGridResource = { "pagination" : { "page" : page - 1, "size" : size }, "sort" : $scope.sortArray, "filter" : $scope.filterArray };
		
		CitiesService.page(uiGridResource)
			.success(function(data, status, headers, config) {
				$scope.loading = false;
				$scope.gridOptions.data = data.data;
				$scope.gridOptions.totalItems = data.total;
			})
			.error(function(data, status, headers, config) {
				$scope.loading = false;
				toaster.error({ title: $translate('MSG_LOADING_DATA_FAILURE'), body: data.message });
			});
	};
	
	$scope.sortChanged = function(grid, sortColumns) {
		var sortArray = new Array();
		for (var i=0; i<sortColumns.length; i++) {
			sortArray.push({ "name" : sortColumns[i].field, "priority" : sortColumns[i].sort.priority, "direction" : sortColumns[i].sort.direction });
		}
		$scope.sortArray = sortArray;
		$scope.getPage($scope.gridApi.pagination.getPage(), $scope.gridOptions.paginationPageSize);
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
	
	/**
	 * Function clears filter of the given date range picker field.
	 */
	$scope.clearDateFilter = function(field) {
		$('#' + field).val(null);
		$scope.applyDateFilter();	
	};
	
	/**
	 * Function applies date filters.
	 */
	$scope.applyDateFilter = function() {
		setTimeout(function () {
			$scope.$apply(function() {
				$scope.gridApi.core.raise.filterChanged();
			});
		}, 100);
	};
	
	/**
	 * Function sets initial sorting.
	 */
	$scope.setInitialSorting = function() {
		if (!$scope.sortArray) {
			var sortArray = new Array();
			sortArray.push({ name: "name", priority: 0, direction: uiGridConstants.ASC });
			$scope.sortArray = sortArray;			
		}
	}
	
	/**
	 * Add city.
	 */
	$scope.addEntity = function (entity) {
		$state.go('settings.cities_create', { 'id' : 0 });
	}
	
	/**
	 * Edit city.
	 */
	$scope.editEntity = function (entity) {
		$state.go('settings.cities_edit', { 'id' : entity.id });
	}
	
	/**
	 * Delete city.
	 */
	$scope.deleteEntity = function(entity) {
		BootstrapDialog.show({
			type: BootstrapDialog.TYPE_DEFAULT,
            title: $translate('DLG_DELETE_ENTITY_TITLE'),
            message: $translate('DLG_DELETE_ENTITY_MSG'),
            buttons: [
				{
					label: $translate('BTN_NO'),
				    cssClass: 'btn-white',
				    action: function(dialog) {
				        dialog.close();
				    }
				},
            	{
            		label: $translate('BTN_YES'),
	                cssClass: 'btn-primary',
	                action: function(dialog) {
	                	CitiesService.delete(entity.id)
		    				.success(function(data, status) {
		    					toaster.success({ title: $translate('DLG_DELETE_ENTITY_MSG_SUCCESS') });
	    						$scope.getPage($scope.gridApi.pagination.getPage(), $scope.gridOptions.paginationPageSize);
		    				})
		    				.error(function(data, status) {
		    					toaster.error({ title: $translate('DLG_DELETE_ENTITY_MSG_FAILURE'), body: data.message });
		    				});
	        			dialog.close();
	                }	                
            	}
            ]
        });
	}
	
	// initial load
	$scope.loading = true;
	$timeout(function() {
		if ($state.current.name != undefined 
				&& $localStorage.gridStates != undefined 
				&& $localStorage.gridStates[$state.current.name] != undefined 
				&& $scope.gridApi != undefined && $scope.gridApi.saveState != undefined) {
			$scope.stateRestoreInProgress = true;
			$rootScope.restoreGridState($state.current.name, $scope.gridApi, $('#filterCreationDate'), $('#filterLastModifiedDate'));
			$scope.setInitialSorting();
			$timeout(function() {
				$scope.stateRestoreInProgress = false;
				$scope.applyDateFilter();
			}, 500);
		} else {
			$scope.setInitialSorting();	
			$scope.getPage($scope.gridApi.pagination.getPage(), $scope.gridOptions.paginationPageSize);
		}
	}, 500);
};

// ========================================================================
//	EDIT CONTROLLER
// ========================================================================
function CitiesEditController($rootScope, $scope, $state, $stateParams, $filter, toaster, CitiesService) {
	var $translate = $filter('translate');

	$scope.getEntity = function(id) {
		CitiesService.find(id)
			.success(function(data, status) {
				$scope.entity = data;
			})
			.error(function(data, status) {
				if (status == 404) {
					toaster.error({ title: $translate('MSG_LOADING_DATA_FAILURE'), body: data.message });
					$state.go('settings.cities_overview');
				} else {
					toaster.error({ title: $translate('MSG_LOADING_DATA_FAILURE'), body: data.message });
				}
			});
	};

	$scope.saveEntity = function() {
		CitiesService.save($scope.entity)
			.success(function(data, status, headers, config) {
				toaster.success({ title: $translate('MSG_SAVING_DATA_SUCCESS') });
				$scope.entity = data;
				$state.go('Cities.edit', { 'id' : data.id });
			})
			.error(function(data, status, headers, config) {
				toaster.error({ title: $translate('MSG_SAVING_DATA_FAILURE'), body: data.message });
			});		
	};

	$scope.back = function() {
		$state.go('settings.cities_overview');
	};
	
	// initial load
	$scope.entityId = $stateParams.id;
	$scope.getEntity($stateParams.id);
};
