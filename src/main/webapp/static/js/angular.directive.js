'use strict';
angular.module('formular', ['formular.directives']);
/* Controllers */
function stageController($scope) {
 //   $scope.password = 'password';
}

angular.module('formular.directives', [])
  .directive('article', [function() {
	console.log('angular directive');
//http://blog.brunoscopelliti.com/a-directive-to-manage-file-upload-in-an-angularjs-application
  return {
    restrict: 'E',
    templateUrl: statCommitId + '/template/uploader.html'
  };
}]);

