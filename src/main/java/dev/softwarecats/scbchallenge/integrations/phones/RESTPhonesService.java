package dev.softwarecats.scbchallenge.integrations.phones;

import dev.softwarecats.scbchallenge.integrations.phones.rest.PhonesResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RESTPhonesService implements PhonesService {

    private final WebClient webClient;
    private final String path;

    public RESTPhonesService(@Value("${phones.url}") String url,
                             @Value("${phones.path}") String path) {
        log.info("Creating REST client for {} and path {}", url, path);
        webClient = WebClient.create(url);
        this.path = path;
    }

    @Override
    public Mono<List<String>> loadPhones(Integer userId) {
        return webClient.get()
                .uri(builder -> {
                    final URI build = builder.path(path).build(userId);
                    log.debug("Requesting uri: {}", build);
                    return build;
                })
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .flatMap(response -> response.toEntity(PhonesResponse.class))
                .handle((ResponseEntity<PhonesResponse> entity, SynchronousSink<PhonesResponse> sink) ->
                        Optional.ofNullable(entity.getBody()).ifPresentOrElse(sink::next, sink::complete))
                .map(PhonesResponse::getPhones)
                .defaultIfEmpty(List.of())
                .onErrorResume(err -> Mono.just(List.of()))
                .timeout(Duration.ofSeconds(5), Mono.just(List.of()))
                .subscribeOn(Schedulers.newParallel("Phones loader", 5));
    }
}
