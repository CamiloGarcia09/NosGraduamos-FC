package co.edu.uco.infrastructure.adapter.secondary.repository.postgresql;

import co.edu.uco.infrastructure.adapter.secondary.repository.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TokenPostgresSQLRepositoryAdapter extends JpaRepository<TokenEntity, UUID> {
}