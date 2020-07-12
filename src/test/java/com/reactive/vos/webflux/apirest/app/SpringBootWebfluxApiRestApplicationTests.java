package com.reactive.vos.webflux.apirest.app;

import com.reactive.vos.webflux.apirest.app.models.documents.Producto;
import com.reactive.vos.webflux.apirest.app.models.services.ProductoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class SpringBootWebfluxApiRestApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private ProductoService productoService;

	@Value("${config.base.endpoint}")
	private String url;

	@Test
	public void listTest() {

		client.get()
			.uri(url)
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBodyList(Producto.class)
			.consumeWith(listEntityExchangeResult -> {
				List<Producto> productos = listEntityExchangeResult.getResponseBody();
				productos.forEach(p -> {
					System.out.println(p.getNombre());
				});

				Assertions.assertTrue(productos.size()>0);
			});
			//.hasSize(6)
		;

	}

	@Test
	public void verTest() {

		Producto producto = productoService.findByNombre("TV plasma 1").block();

		client.get()
			.uri(url+ "/{id}", Collections.singletonMap("id", producto.getId()))
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.expectStatus().isOk()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody(Producto.class)
			.consumeWith(res -> {
				Producto product = res.getResponseBody();
				Assertions.assertNotNull(product.getId());
				Assertions.assertTrue(product.getId().length()> 0);
				Assertions.assertEquals("TV plasma 1", product.getNombre());
			});

	}

	@Test
	public void createTest() {

		Producto producto = new Producto("Mesa Comedor", 1000.00);

		client.post()
			.uri(url)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(producto), Producto.class)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.id").isNotEmpty()
			.jsonPath("$.nombre").isEqualTo("Mesa Comedor");
	}

	@Test
	public void create2Test() {

		Producto producto = new Producto("Mesa Comedor", 1000.00);

		client.post()
			.uri(url)
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(producto), Producto.class)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody(Producto.class)
			.consumeWith(response -> {
				Producto productoRes = response.getResponseBody();
				Assertions.assertNotNull(productoRes.getId());
				Assertions.assertEquals("Mesa Comedor", productoRes.getNombre());
			});
	}

	@Test
	public void editTest() {
		Producto producto = productoService.findByNombre("TV plasma 1").block();

		Producto productEdited = new Producto("TV plasma 2", 5000.00);

		client.put()
			.uri(url+"/{id}", Collections.singletonMap("id", producto.getId()))
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.body(Mono.just(productEdited), Producto.class)
			.exchange()
			.expectStatus().isCreated()
			.expectHeader().contentType(MediaType.APPLICATION_JSON)
			.expectBody()
			.jsonPath("$.id").isNotEmpty()
			.jsonPath("$.nombre").isEqualTo("TV plasma 2");
	}

	@Test
	public void deleteTest() {

		Producto producto = productoService.findByNombre("Mesa Portable").block();

		client.delete()
			.uri(url+"/{id}", Collections.singletonMap("id", producto.getId()))
			.exchange()
			.expectStatus()
			.isNoContent()
			.expectBody()
			.isEmpty();

/*		client.get()
				.uri("/api/v2/productos/{id}", Collections.singletonMap("id", producto.getId()))
				.exchange()
				.expectStatus()
				.isNotFound()
				.expectBody()
				.isEmpty();*/

	}


}
