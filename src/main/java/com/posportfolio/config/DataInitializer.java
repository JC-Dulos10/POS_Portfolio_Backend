package com.posportfolio.config;

import com.posportfolio.model.ProductEntity;
import com.posportfolio.model.Role;
import com.posportfolio.model.UserEntity;
import com.posportfolio.repository.ProductRepository;
import com.posportfolio.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ProductRepository productRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByUsername("admin")) {
            UserEntity admin = new UserEntity("admin", passwordEncoder.encode("admin123"), Role.ROLE_ADMIN);
            userRepository.save(admin);
            System.out.println("Created default admin user: admin / admin123");
        }

        if (productRepository.count() == 0) {
            productRepository.save(new ProductEntity("Coffee", 3.99, 50));
            productRepository.save(new ProductEntity("Tea", 2.99, 60));
            productRepository.save(new ProductEntity("Sandwich", 5.99, 30));
            System.out.println("Created default product inventory");
        }
    }
}
