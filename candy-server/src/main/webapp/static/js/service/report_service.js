'use strict';

App.factory('ReportService', ['$http', '$q', function ($http, $q) {

  var REST_SERVICE_URI = 'http://localhost:8080/api/reports/';

  return {
    fetchReport: fetchReport
  };

  function fetchReport(id) {
    var deferred = $q.defer();
    $http.get(REST_SERVICE_URI + id)
        .then(
            function (response) {
              deferred.resolve(response.data);
            },
            function (errResponse) {
              console.error('Error while fetching report');
              deferred.reject(errResponse);
            }
        );
    return deferred.promise;
  }
}]);
