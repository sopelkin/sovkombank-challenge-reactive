package dev.softwarecats.scbchallenge;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.softwarecats.scbchallenge.integrations.users.SOAPUsersService;
import dev.softwarecats.scbchallenge.wsdl.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public ObjectFactory objectFactory() {
        return new ObjectFactory();
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("dev.softwarecats.scbchallenge.wsdl");
        return marshaller;
    }

    @Bean
    public SOAPUsersService soapUsersService(Jaxb2Marshaller marshaller, @Value("${users.url}") String url) {
        SOAPUsersService client = new SOAPUsersService(url);
        client.setDefaultUri(url);
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer customizer() {
        return builder -> builder.serializationInclusion(JsonInclude.Include.NON_NULL);
    }
}
