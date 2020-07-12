package com.reactive.vos.webflux.apirest.app.models.services;


import com.reactive.vos.webflux.apirest.app.models.documents.Producto;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {
	
	public Flux<Producto> findAll();
	
	public Flux<Producto> findAllWithNameUpperCase();
	
	public Flux<Producto> findAllWithNameUpperCaseRepeat();
	
	public Mono<Producto> findById(String id);
	
	public Mono<Producto> save(Producto producto);
	
	public Mono<Void> delete(Producto producto);

	public Mono<Producto> findByNombre(String nombre);
	
	
}
