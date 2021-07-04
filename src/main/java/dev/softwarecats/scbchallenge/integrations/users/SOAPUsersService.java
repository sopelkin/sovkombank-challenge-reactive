package dev.softwarecats.scbchallenge.integrations.users;

import dev.softwarecats.scbchallenge.exceptions.InteractionException;
import dev.softwarecats.scbchallenge.exceptions.TimeoutException;
import dev.softwarecats.scbchallenge.wsdl.GetUserRequest;
import dev.softwarecats.scbchallenge.wsdl.GetUserResponse;
import dev.softwarecats.scbchallenge.wsdl.ObjectFactory;
import dev.softwarecats.scbchallenge.wsdl.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Objects;

@Slf4j
public class SOAPUsersService extends WebServiceGatewaySupport implements UsersService {

    private final String url;
    @Autowired
    private ObjectFactory factory;

    public SOAPUsersService(String url) {
        log.info("Creating SOAP for " + url);
        this.url = url;
    }

    private User getUser(int userId) {
        log.debug("Load user {}", userId);
        final GetUserRequest request = factory.createGetUserRequest();
        request.setUserId(userId);
        final GetUserResponse response = (GetUserResponse) getWebServiceTemplate()
                .marshalSendAndReceive(url, request);
        final User user = response.getUser();
        log.debug("Got user info {}", user);
        return user;
    }

    @Override
    public Mono<User> loadUser(Integer userId) {
        return Mono.fromSupplier(() -> this.getUser(userId))
                .onErrorMap(e -> new InteractionException())
                .timeout(Duration.ofSeconds(5), Mono.error(new TimeoutException()))
                .subscribeOn(Schedulers.newParallel("User loader", 5));
    }
}
