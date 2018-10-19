angular.module('App')

.directive("sideNavigation", function($timeout) {
    return {
        restrict: 'A',
        link: function(scope, element) {
            $timeout(function(){
                element.metisMenu();
            });
        }
    };
})

.directive('parseFloat', function() {
	return {
		require: 'ngModel',
		link: function(scope, element, attrs, ngModel) {
			ngModel.$formatters.push(function(value) {
				return (value) ? parseFloat(value).toFixed(2) : '';
			});
		}
	};
})

.directive("icheck", function ($timeout) {
	return {
		restrict: 'A',
		require: 'ngModel',
		link: function ($scope, element, $attrs, ngModel) {
			return $timeout(function () {
				var value;
				value = $attrs['value'];

				$scope.$watch($attrs['ngModel'], function (newValue) {
					$(element).iCheck('update');
				})

				return $(element).iCheck({
					checkboxClass: 'icheckbox_square-green',
					radioClass: 'iradio_square-green'

				}).on('ifChanged', function (event) {
					if ($(element).attr('type') === 'checkbox' && $attrs['ngModel']) {
						$scope.$apply(function () {
							return ngModel.$setViewValue(event.target.checked);
						});
					}
					if ($(element).attr('type') === 'radio' && $attrs['ngModel']) {
						return $scope.$apply(function () {
							return ngModel.$setViewValue(value);
						});
					}
				});
			});
		}
	};
});