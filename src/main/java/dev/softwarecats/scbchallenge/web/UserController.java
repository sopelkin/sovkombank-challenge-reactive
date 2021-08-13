package dev.softwarecats.scbchallenge.web;

import dev.softwarecats.scbchallenge.exceptions.InteractionException;
import dev.softwarecats.scbchallenge.exceptions.TimeoutException;
import dev.softwarecats.scbchallenge.integrations.phones.PhonesService;
import dev.softwarecats.scbchallenge.integrations.users.UsersService;
import dev.softwarecats.scbchallenge.wsdl.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UsersService usersService;
    private final PhonesService phonesService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserResponse> loadUser(@PathVariable("userId") Integer userId) {
        Mono<User> userMono = usersService.loadUser(userId);
        Mono<List<String>> phonesMono = phonesService.loadPhones(userId);

        return Mono.zip(userMono, phonesMono, this::buildResponse);
    }

    public UserResponse buildResponse(User user, List<String> phones) {
        log.debug("Building response for user {} and phones {}", user, phones);
        final UserResponse userResponse = new UserResponse(0,
                String.join(" ", ObjectUtils.nullSafeToString(user.getFirstName()), ObjectUtils.nullSafeToString(user.getLastName())),
                phones.stream().filter(Objects::nonNull).findFirst().orElse(null));
        log.debug("Response {}", userResponse);
        return userResponse;
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler({InteractionException.class,})
    public Mono<UserResponse> handleInteractionException(Exception e) {
        log.info("Handle failure");
        return Mono.just(new UserResponse(2, null, null));
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(TimeoutException.class)
    public Mono<UserResponse> handleTimeoutException(TimeoutException e) {
        log.info("Handle timeout");
        return Mono.just(new UserResponse(1, null, null));
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception.class)
    public Mono<UserResponse> handleUnknownException(Exception e) {
        log.info("Got unknown exception", e);
        return Mono.just(new UserResponse(1, null, null));
    }
}
