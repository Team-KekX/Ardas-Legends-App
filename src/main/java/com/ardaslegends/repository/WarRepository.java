package com.ardaslegends.repository;

import com.ardaslegends.domain.war.War;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WarRepository extends JpaRepository<War, Long> {
    public Optional<War> findByName(String name);
}
