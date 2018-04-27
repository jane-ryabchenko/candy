<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
  <title>Comparison report</title>
  <style>

  </style>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
  <link href="<c:url value='/static/css/app.css' />" rel="stylesheet"></link>
</head>
<body ng-app="candyApp" class="ng-cloak">
<div class="generic-container" ng-controller="ReportController as ctrl">
  <div class="panel panel-default">
    <!-- Default panel contents -->
    <div class="panel-heading"><span class="lead">Comparison report</span></div>
    <%--<div class="tablecontainer">--%>
      <%--<table class="table table-hover">--%>
        <%--<thead>--%>
        <%--<tr>--%>
          <%--<th>Name</th>--%>
          <%--<th>Diff %</th>--%>
        <%--</tr>--%>
        <%--</thead>--%>
        <%--<tbody>--%>
        <%--<tr ng-repeat="comp in ctrl.report.comparisonList">--%>
          <%--<td><span ng-bind="comp.name"></span></td>--%>
          <%--<td><span ng-bind="comp.diffPercentage"></span></td>--%>
        <%--</tr>--%>
        <%--</tbody>--%>
      <%--</table>--%>
    <%--</div>--%>
      <div ng-repeat="comp in ctrl.report.comparisonList" class="comparison">
          <div><img ng-src="api/images/{{comp.originImageId}}"/></div>
          <div><img ng-src="api/images/{{comp.actualImageId}}"/></div>
      </div>
  </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.4.4/angular.js"></script>
<script src="<c:url value='/static/js/app.js?t='/><%= new java.util.Date().getTime() %>"></script>
<script src="<c:url value='/static/js/service/report_service.js?t='/><%= new java.util.Date().getTime() %>"></script>
<script>
  'use strict';

  App.controller('ReportController', ['$scope', 'ReportService', function ($scope, ReportService) {
    var self = this;
    self.report = {};

    fetchReport('<c:out value="${reportId}"/>');

    function fetchReport(id) {
      ReportService.fetchReport(id)
          .then(
              function (r) {
                self.report = r;
              },
              function (errResponse) {
                console.error('Error while fetching report: ' + errResponse);
              }
          );
    }
  }]);
</script>

</body>
</html>