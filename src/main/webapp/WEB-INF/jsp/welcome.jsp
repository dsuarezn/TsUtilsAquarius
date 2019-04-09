<!DOCTYPE html>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en">
<head>

	<!-- Access the bootstrap Css like this, 
		Spring boot will handle the resource mapping automcatically -->
	<link rel="stylesheet" type="text/css" href="webjars/bootstrap/3.3.7/css/bootstrap.min.css" />

	<!-- 
	<spring:url value="/css/main.css" var="springCss" />
	<link href="${springCss}" rel="stylesheet" />
	 -->
	<c:url value="/css/main.css" var="jstlCss" />
	<link href="${jstlCss}" rel="stylesheet" />

</head>
<body>

	<nav class="navbar navbar-inverse">
		<div class="container">
			<div class="navbar-header">
				<a class="navbar-brand" href="#">Spring Boot</a>
			</div>
			<div id="navbar" class="collapse navbar-collapse">
				<ul class="nav navbar-nav">
					<li class="active"><a href="#">Home</a></li>
					<li><a href="#about">About</a></li>
				</ul>
			</div>
		</div>
	</nav>

	<div class="container">

		<div class="starter-template">
			<h1>Pagina prueba comparación de datos</h1>
			<h2>Hola: ${message}</h2>
		</div>
	<form action="/ejecutar">
		<label for="codEstacion">Código Estación:</label>
		<input type="text" name="codEstacion" />
		<br/>
		<label for="codVar">Código Variable:</label>
		<input type="text" name="codVar" />
		
		<button type="submit">Ejecutar</button>
	</form>
	
	<form action="/correa">		
		<button type="submit">Ruth Correa</button>
	</form>
	
	<form action="/msierra">		
		<button type="submit">Marcela</button>
	</form>
	
	<form action="/comparar">		
		<button type="submit">Comparar</button>
	</form>
	
	<form action="/actualizar">		
		<button type="submit">Actualizar</button>
	</form>
	
	<form action="/agregar">		
		<button type="submit">Agregar</button>
	</form>
	
	<form action="/borrar">		
		<button type="submit">Borrar</button>
	</form>
	
	<form action="/borradoCompleto">		
		<button type="submit">Generar archivos borrado completo</button>
	</form>
	
	<form action="/borrarIntervalo">		
		<button type="submit">Procesar archivos borrado completo</button>
	</form>
	
	<form action="/backup">		
		<button type="submit">Generar backup</button>
	</form>
	
	<form action="/generarArchivosCurvas">		
		<button type="submit">Generar archivos curvas</button>
	</form>
	
	</div>
	
	<script type="text/javascript" src="webjars/bootstrap/3.3.7/js/bootstrap.min.js"></script>

</body>

</html>