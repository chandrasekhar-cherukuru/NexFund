package com.chakri.fundly.repo;

import com.chakri.fundly.model.Users;
import com.chakri.fundly.model.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<Users, Integer> {

    // ✅ Find by username
    Optional<Users> findByUsername(String username);

    // ✅ Find by email
    Optional<Users> findByEmail(String email);

    // ✅ Find by provider ID (for OAuth2)
    Optional<Users> findByProviderIdAndAuthProvider(String providerId, AuthProvider authProvider);
}
