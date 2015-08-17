<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="de" dir="ltr">
<head>
	<title>Tostao IT blog</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link href="<c:url value="/static/bootstrap-3.0.0/css/bootstrap.css" />" rel="stylesheet" type="text/css" />
	<link href="<c:url value="/static/bootstrap-3.0.0/css/bootstrap.min.css" />" rel="stylesheet" type="text/css" />
	<link href="<c:url value="/static/bootstrap-3.0.0/css/bootstrap-theme.css" />" rel="stylesheet" type="text/css" />
	<link href="<c:url value="/static/bootstrap-3.0.0/css/bootstrap-theme.min.css" />" rel="stylesheet" type="text/css" />
	<script src="<c:url value="/static/jquery-1.9.1/jquery.min.js" />"></script>
	<script src="<c:url value="/static/bootstrap-3.0.0/js/bootstrap.js" />"></script>
	<script src="<c:url value="/static/bootstrap-3.0.0/js/bootstrap.min.js" />"></script>
	<script src="<c:url value="/static/js/angular.directive.js" />"></script>
	<script src="<c:url value="/static/js/angular.min.js" />"></script>

</head>
<body>
	<h2>Hello Simple spring MVC!</h2>

	<div ng-app>
		<label>Name:</label> <input type="text" ng-model="yourName" placeholder="Enter a name here">
		<hr>
		<h1>Hello {{yourName}}!</h1>
	</div>

</body>
</html>