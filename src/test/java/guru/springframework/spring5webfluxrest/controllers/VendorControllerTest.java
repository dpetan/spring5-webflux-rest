package guru.springframework.spring5webfluxrest.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class VendorControllerTest {

    VendorRepository vendorRepository;
    VendorController vendorController;
    WebTestClient webTestClient;

    @Before
    public void setUp() throws Exception {
        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void list() {
        given(vendorRepository.findAll())
                  .willReturn(
                          Flux.just(Vendor.builder().firstName("John").lastName("Doe").build(),
                                    Vendor.builder().firstName("Danijel").lastName("Petanovic").build()));

        webTestClient.get().uri("/api/v1/vendors")
                     .exchange()
                     .expectBodyList(Vendor.class)
                     .hasSize(2);
    }

    @Test
    public void findById() {
        given(vendorRepository.findById("someid"))
                  .willReturn(Mono.just(Vendor.builder().firstName("John").lastName("Doe").build()));

        webTestClient.get().uri("/api/v1/vendors/someid").exchange().expectBody(Vendor.class);
    }

    @Test
    public void create() {
        given(vendorRepository.saveAll(any(Publisher.class)))
                  .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToSave = Mono.just(Vendor.builder().firstName("Danijel").lastName("Petanovic").build());

        webTestClient.post().uri("/api/v1/vendors")
                     .body(vendorMonoToSave, Vendor.class)
                     .exchange()
                     .expectStatus().isCreated();
    }

    @Test
    public void update() {
        given(vendorRepository.save(any(Vendor.class)))
                  .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().firstName("Danijel").lastName("Petanovic").build());

        webTestClient.put().uri("/api/v1/vendors/lalala")
                     .body(vendorMonoToUpdate, Vendor.class)
                     .exchange()
                     .expectStatus().isOk();
    }

    @Test
    public void patchWithChanges() {
        given(vendorRepository.findById(anyString()))
                  .willReturn(Mono.just(Vendor.builder().build()));

        given(vendorRepository.save(any(Vendor.class)))
                  .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().firstName("Danijel").lastName("Petanovic").build());

        webTestClient.patch().uri("/api/v1/vendors/lalala")
                     .body(vendorMonoToUpdate, Vendor.class)
                     .exchange()
                     .expectStatus().isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    public void patchNoChanges() {
        given(vendorRepository.findById(anyString()))
                  .willReturn(Mono.just(Vendor.builder().build()));

        given(vendorRepository.save(any(Vendor.class)))
                  .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().build());

        webTestClient.patch().uri("/api/v1/vendors/lalala")
                     .body(vendorMonoToUpdate, Vendor.class)
                     .exchange()
                     .expectStatus().isOk();

        verify(vendorRepository, never()).save(any());
    }
}
