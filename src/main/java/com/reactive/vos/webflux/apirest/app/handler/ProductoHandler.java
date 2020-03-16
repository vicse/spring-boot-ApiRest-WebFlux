package com.reactive.vos.webflux.apirest.app.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import org.springframework.validation.Validator;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.vos.webflux.apirest.app.models.documents.Producto;
import com.reactive.vos.webflux.apirest.app.models.services.ProductoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ProductoHandler {
	
	@Autowired
	private ProductoService service;

	@Qualifier("webFluxValidator")
	@Autowired
	private Validator validator;
	
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
				.body(fromValue(p))
				.switchIfEmpty(ServerResponse.notFound().build()));
	}

	public Mono<ServerResponse> crear(ServerRequest request) {

		Mono<Producto> producto = request.bodyToMono(Producto.class);

		return producto.flatMap(p -> {

			Errors errors = new BeanPropertyBindingResult(p, Producto.class.getName());
			validator.validate(p, errors);

			if (errors.hasErrors()) {
				return Flux.fromIterable(errors.getFieldErrors())
						.map(fieldError -> "El campo " + fieldError.getField() + " " + fieldError.getDefaultMessage())
						.collectList()
						.flatMap(list -> ServerResponse.badRequest().body(fromValue(list)));
			} else {
				if (p.getCreatedAt() == null) {
					p.setCreatedAt(new Date());
				}
				return service.save(p).flatMap(pGuardadoDB -> ServerResponse.
						created(URI.create("/api/v2/productos/".concat(pGuardadoDB.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(pGuardadoDB)));
			}

		});

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

	public Mono<ServerResponse> eliminar (ServerRequest request) {

		String id = request.pathVariable("id");
		Mono<Producto> productoDB = service.findById(id);

		return productoDB.flatMap(p -> service.delete(p)
				.then(ServerResponse.noContent().build()))
				.switchIfEmpty(ServerResponse.notFound().build());
	}

}
