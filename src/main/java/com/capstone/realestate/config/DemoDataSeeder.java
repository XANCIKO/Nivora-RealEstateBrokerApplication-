package com.capstone.realestate.config;

import com.capstone.realestate.entity.Broker;
import com.capstone.realestate.entity.Customer;
import com.capstone.realestate.entity.Property;
import com.capstone.realestate.entity.User;
import com.capstone.realestate.repository.BrokerRepository;
import com.capstone.realestate.repository.CustomerRepository;
import com.capstone.realestate.repository.PropertyRepository;
import com.capstone.realestate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DemoDataSeeder {

    private static final String BROKER_EMAIL = "broker.demo@nivora.local";
    private static final String CUSTOMER_EMAIL = "customer.demo@nivora.local";
    private static final String DEMO_PASSWORD = "Demo@123";

    private final UserRepository userRepository;
    private final BrokerRepository brokerRepository;
    private final CustomerRepository customerRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedDemoData(@Value("${app.demo.seed.enabled:true}") boolean demoSeedEnabled) {
        return args -> {
            if (!demoSeedEnabled) {
                log.info("Demo data seeding disabled.");
                return;
            }

            User brokerUser = userRepository.findByEmailIgnoreCase(BROKER_EMAIL)
                    .orElseGet(() -> userRepository.save(User.builder()
                            .email(BROKER_EMAIL)
                            .password(passwordEncoder.encode(DEMO_PASSWORD))
                            .role("BROKER")
                            .mobile("9876543210")
                            .city("Chennai")
                            .build()));

            Broker broker = brokerRepository.findByUser_UserId(brokerUser.getUserId())
                    .orElseGet(() -> brokerRepository.save(Broker.builder()
                            .broName("Demo Broker")
                            .user(brokerUser)
                            .build()));

            User customerUser = userRepository.findByEmailIgnoreCase(CUSTOMER_EMAIL)
                    .orElseGet(() -> userRepository.save(User.builder()
                            .email(CUSTOMER_EMAIL)
                            .password(passwordEncoder.encode(DEMO_PASSWORD))
                            .role("CUSTOMER")
                            .mobile("9123456780")
                            .city("Bengaluru")
                            .build()));

            customerRepository.findByUser_UserId(customerUser.getUserId())
                    .orElseGet(() -> customerRepository.save(Customer.builder()
                            .custName("Demo Customer")
                            .user(customerUser)
                            .build()));

            if (!propertyRepository.findByBroker_BroId(broker.getBroId()).isEmpty()) {
                log.info("Demo broker already has properties. Skipping property seed.");
                return;
            }

            propertyRepository.saveAll(List.of(
                    buildProperty(broker, "Luxury Villa", "SALE", 12500000, 3200, "Ocean Crest Villa", "ECR Beach Road", "Chennai", List.of(
                            "/uploads/property-images/demo-coastal-villa.svg",
                            "/uploads/property-images/demo-lakeview-house.svg"
                    )),
                    buildProperty(broker, "Studio Apartment", "RENT", 28000, 720, "Metro Nest Studio", "100 Feet Road", "Bengaluru", List.of(
                            "/uploads/property-images/demo-studio-flat.svg",
                            "/uploads/property-images/demo-urban-loft.svg"
                    )),
                    buildProperty(broker, "Garden Home", "SALE", 8900000, 2400, "Maple Garden Home", "MG Road Extension", "Coimbatore", List.of(
                            "/uploads/property-images/demo-garden-home.svg",
                            "/uploads/property-images/demo-coastal-villa.svg"
                    )),
                    buildProperty(broker, "Skyline Penthouse", "SALE", 15800000, 2800, "Skyline Crown", "Financial District", "Hyderabad", List.of(
                            "/uploads/property-images/demo-skyline-suite.svg",
                            "/uploads/property-images/demo-urban-loft.svg"
                    )),
                    buildProperty(broker, "Lakeview House", "RENT", 52000, 2100, "Bluewater Residency", "Lake View Street", "Pune", List.of(
                            "/uploads/property-images/demo-lakeview-house.svg",
                            "/uploads/property-images/demo-garden-home.svg"
                    )),
                    buildProperty(broker, "Modern Loft", "RENT", 36000, 980, "Cityline Loft", "Residency Road", "Mumbai", List.of(
                            "/uploads/property-images/demo-urban-loft.svg",
                            "/uploads/property-images/demo-skyline-suite.svg"
                    ))
            ));

            log.info("Seeded demo broker, customer, and 6 demo properties.");
        };
    }

    private Property buildProperty(Broker broker,
                                   String configuration,
                                   String offerType,
                                   double offerCost,
                                   double areaSqft,
                                   String address,
                                   String street,
                                   String city,
                                   List<String> imageUrls) {
        return Property.builder()
                .configuration(configuration)
                .offerType(offerType)
                .offerCost(offerCost)
                .areaSqft(areaSqft)
                .address(address)
                .street(street)
                .city(city)
                .status(true)
                .imageUrls(imageUrls)
                .broker(broker)
                .build();
    }
}