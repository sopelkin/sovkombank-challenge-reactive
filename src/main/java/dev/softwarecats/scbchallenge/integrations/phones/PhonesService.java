package dev.softwarecats.scbchallenge.integrations.phones;

import reactor.core.publisher.Mono;

import java.util.List;

public interface PhonesService {
    Mono<List<String>> loadPhones(Integer userId);
}
