package com.reactive.vos.webflux.apirest.app;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.reactive.vos.webflux.apirest.app.models.dao.ProductoDao;
import com.reactive.vos.webflux.apirest.app.models.documents.Producto;

import reactor.core.publisher.Flux;



@SpringBootApplication
public class SpringBootWebfluxApiRestApplication implements CommandLineRunner {
	
	@Autowired
	private ProductoDao dao;
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApiRestApplication.class);


	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApiRestApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
mongoTemplate.dropCollection("productos").subscribe();
		
		Flux.just(new Producto("TV plasma 1", 1232.00),
				new Producto("Mesa Portable", 1232.00),
				new Producto("Mouse", 1232.00),
				new Producto("Readmi note 7", 1232.00),
				new Producto("Cargador portatil", 1232.00),
				new Producto("Mochila", 1232.00)
				)
		.flatMap(producto -> {
			producto.setCreatedAt(new Date());
			return dao.save(producto);
		})
		.subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNombre() ));
		
		
		
	}

}
