package com.ardaslegends.repository;

import com.ardaslegends.domain.Region;
import com.ardaslegends.repository.region.RegionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class RegionRepositoryTest {

    @Autowired
    private RegionRepository repository;

    @BeforeEach
    void testData() {
        Region r1 = new Region("1", "one", null, null, null, null);
        Region r2 = new Region("2", "two", null, null, null, null);
        Region r3 = new Region("3", "3", null, null, null, null);

        repository.saveAll(List.of(r1,r2,r3));
    }

    @Test
    void ensureFindByIdWorks() {

        var queriedRegion = repository.findById("1");

        assertThat(queriedRegion.isPresent()).isTrue();
        assertThat(queriedRegion.get().getId()).isEqualTo("1");

    }

    @Test
    void ensureFindAllWorks() {

        assertThat(repository.findAll().size()).isEqualTo(3);

    }

    @Test
    void ensureSavingWorks() {
        Region r1 = new Region("4", "one", null, null, null, null);

        assertThat(repository.findAll().size()).isEqualTo(3);

        repository.save(r1);

        assertThat(repository.findAll().size()).isEqualTo(4);
    }

    @Test
    void ensureDeleteByIdWorks() {

        assertThat(repository.findAll().size()).isEqualTo(3);

        repository.deleteById("1");

        assertThat(repository.findAll().size()).isEqualTo(2);
    }

}
