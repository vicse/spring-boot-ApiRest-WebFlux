package com.reactive.vos.webflux.apirest.app.models.dao;


import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactive.vos.webflux.apirest.app.models.documents.Producto;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String>{
	
	

}
