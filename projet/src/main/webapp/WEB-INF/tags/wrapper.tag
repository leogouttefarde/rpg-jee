<%@tag description="Main template" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@attribute name="header" fragment="true" %>
<%@attribute name="footer" fragment="true" %>
<%@attribute name="title" fragment="true" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Gestion de RPG <jsp:invoke fragment="title" /></title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="favicon.ico" />
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/bootstrap-theme.min.css" rel="stylesheet">
        <link href="css/main.css" rel="stylesheet">

    </head>
    <body role="document">

    <!-- Fixed navbar -->
    <nav class="navbar navbar-inverse navbar-fixed-top">
      <div class="container">
        <div class="navbar-header">
          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href=".">RPG</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
          <c:choose>
          <c:when test="${sessionScope.user != null}">
          <ul class="nav navbar-nav">
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
              <span class="glyphicon glyphicon-user" aria-hidden="true"></span> Personnages <span class="caret"></span></a>
              <ul class="dropdown-menu">
                <li><a href="character?action=create">Créer un personnage</a></li>
                <li><a href="character?action=list">Liste des personnages</a></li>
                <li><a href="character?action=ownedList">Mes personnages</a></li>
              </ul>
            </li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
              <span class="glyphicon glyphicon-certificate" aria-hidden="true"></span> Parties <span class="caret"></span></a>
              <ul class="dropdown-menu">
                <li><a href="game?action=create">Proposer une partie</a></li>
                <li><a href="game?action=list">Liste des parties</a></li>
                <li><a href="game?action=myList">Mes parties</a></li>
              </ul>
            </li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
              <span class="glyphicon glyphicon-glass" aria-hidden="true"></span> MJ <span class="caret"></span></a>
              <ul class="dropdown-menu">
                <li class="dropdown-header">Gestion</li>
                <li><a href="game?action=leaderList">Parties menées</a></li>
                <li><a href="character?action=leaderList">Personnages menés</a></li>
                <li role="separator" class="divider"></li>
                <li class="dropdown-header">Validation</li>
                <li><a href="character?action=validationList">Personnages à valider</a></li>
                <li><a href="character?action=transferList">Transferts à valider</a></li>
                <li><a href="episode?action=validationList">Episodes à valider</a></li>
              </ul>
            </li>
          </ul>
          <div class="btn-group navbar-right navbar-btn">
            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            <span class="glyphicon glyphicon-map-marker" aria-hidden="true"></span> ${sessionScope.user.getPseudo()} <span class="caret"></span>
            </button>
            <ul class="dropdown-menu">
              <li><a href=".?logout"><span class="glyphicon glyphicon-off" aria-hidden="true"></span> Logout</a></li>
            </ul>
          </div>
          </c:when> 
          <c:otherwise>
          <div class="navbar-right">
              <a type="button" class="btn btn-default navbar-btn" href=".?login">Login</a>
          </div>
          </c:otherwise>
        </c:choose>
        </div><!--/.nav-collapse -->
      </div>
    </nav>

    <div class="container theme-showcase" role="main">
    <div class="text-center">

        <div class="page-header">
            <jsp:invoke fragment="header" />
        </div>

        <jsp:doBody/>

        <div class="page-footer">
            <jsp:invoke fragment="footer"/>
        </div>

        <!-- Load at last to reduce startup time -->
        <script src="js/jquery.min.js"></script>
        <script src="js/bootstrap.min.js"></script>
    </div>
    </div>
    </body>
</html>
