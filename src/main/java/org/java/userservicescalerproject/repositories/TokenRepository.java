package org.java.userservicescalerproject.repositories;

import org.java.userservicescalerproject.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    Token save(Token token);

    Optional<Token> findByTokenValueAndDeletedAndExpiryAtGreaterThan(String token, boolean deleted, Date expiryAt);
    @Modifying
    @Transactional
    @Query(value = "update token set deleted=:deleted where token_value=:token", nativeQuery = true)
    Integer updateTokenValueAndDeleted(String token, Boolean deleted);
}
