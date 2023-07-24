package com.ardaslegends.repository.rpchar;

import com.ardaslegends.domain.RPChar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RpcharRepository extends JpaRepository<RPChar, Long>, RpcharRepositoryCustom {
}
