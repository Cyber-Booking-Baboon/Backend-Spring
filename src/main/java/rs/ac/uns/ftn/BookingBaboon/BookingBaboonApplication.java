package rs.ac.uns.ftn.BookingBaboon;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(
		name = "Keycloak"
		, openIdConnectUrl = "http://localhost:8180/realms/CyberBookingBaboon/.well-known/openid-configuration"
		, scheme = "bearer"
		, type = SecuritySchemeType.OPENIDCONNECT
		, in = SecuritySchemeIn.HEADER
)
public class BookingBaboonApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingBaboonApplication.class, args);
	}

}
