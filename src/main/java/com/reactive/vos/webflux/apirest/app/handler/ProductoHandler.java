package com.reactive.vos.webflux.apirest.app.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.vos.webflux.apirest.app.models.documents.Producto;
import com.reactive.vos.webflux.apirest.app.models.services.ProductoService;

import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

@Component
public class ProductoHandler {
	
	@Autowired
	private ProductoService service;
	
	public Mono<ServerResponse> listar(ServerRequest request) {
		return ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.findAll(), Producto.class);
	}

	public Mono<ServerResponse> ver(ServerRequest request) {

		String id = request.pathVariable("id");
		return service.findById(id)
				.flatMap( p -> ServerResponse.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(p))
				.switchIfEmpty(ServerResponse.notFound().build()));
	}

	public Mono<ServerResponse> crear(ServerRequest request) {

		Mono<Producto> producto = request.bodyToMono(Producto.class);

		return producto.flatMap(p -> {
			if (p.getCreatedAt() == null) {
				p.setCreatedAt(new Date());
			}

			return service.save(p);
		}).flatMap(p -> ServerResponse.
				created(URI.create("/api/v2/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(p)));

	}

	public Mono<ServerResponse> editar(ServerRequest request) {

		Mono<Producto> producto = request.bodyToMono(Producto.class);
		String id = request.pathVariable("id");

		Mono<Producto> productoDB = service.findById(id);

		return productoDB.zipWith(producto, (db, req) -> {
			db.setNombre(req.getNombre());
			db.setPrecio(req.getPrecio());
			return db;
		}).flatMap(p -> ServerResponse.created(URI.create("/api/v2/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.save(p), Producto.class))
		.switchIfEmpty(ServerResponse.notFound().build());
	}

}
