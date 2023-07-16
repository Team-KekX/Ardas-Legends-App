package com.ardaslegends.repository;

import com.ardaslegends.domain.ProductionSite;
import com.ardaslegends.domain.ProductionSiteType;
import com.ardaslegends.domain.Resource;
import com.ardaslegends.domain.ResourceType;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest(properties = {"spring.sql.init.mode=never"})
public class ProductionSiteRepositoryTest {

    @Autowired
    ProductionSiteRepository productionSiteRepository;

    @Autowired
    ResourceRepository resourceRepository;

    @BeforeEach
    void setup() {

        Resource resource = Resource.builder().resourceName("Oak").resourceType(ResourceType.MINERAL).minecraftItemId("3423").build();

        resource = resourceRepository.save(resource);

        ProductionSite productionSite = ProductionSite.builder().producedResource(resource).type(ProductionSiteType.LUMBER_CAMP).amountProduced(100).build();

        productionSiteRepository.save(productionSite);
    }

    @Test
    void ensurefindProductionSiteByTypeAndProducedResourceNameWorksProperly() {
        val prodSite = productionSiteRepository.findProductionSiteByTypeAndProducedResource_ResourceName(ProductionSiteType.LUMBER_CAMP, "Oak");

        assertThat(prodSite).isPresent();
        assertThat(prodSite.get().getProducedResource().getResourceName()).isEqualTo("Oak");
        assertThat(prodSite.get().getProducedResource().getResourceType()).isEqualTo(ResourceType.MINERAL);
    }

}
