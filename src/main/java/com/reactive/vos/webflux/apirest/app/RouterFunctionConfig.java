package com.reactive.vos.webflux.apirest.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.springframework.web.reactive.function.server.RouterFunction;

import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.vos.webflux.apirest.app.handler.ProductoHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class RouterFunctionConfig {
	

	@Bean
	public RouterFunction<ServerResponse> routes(ProductoHandler handler) {
		
		return route(GET("/api/v2/productos"), handler::listar)
				.andRoute(GET("/api/v2/productos/{id}"), handler::ver)
				.andRoute(POST("/api/v2/productos"), handler::crear)
				.andRoute(PUT("/api/v2/productos/{id}"), handler::editar)
				.andRoute(DELETE("/api/v2/productos/{id}"), handler::eliminar);
	}

}
