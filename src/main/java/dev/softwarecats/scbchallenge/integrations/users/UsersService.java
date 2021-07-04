package dev.softwarecats.scbchallenge.integrations.users;

import dev.softwarecats.scbchallenge.wsdl.User;
import reactor.core.publisher.Mono;

public interface UsersService {
    Mono<User> loadUser(Integer userId);
}
