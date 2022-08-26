package com.ardaslegends.service;

import com.ardaslegends.data.domain.*;
import com.ardaslegends.data.repository.ClaimBuildRepository;
import com.ardaslegends.data.repository.ProductionSiteRepository;
import com.ardaslegends.data.repository.RegionRepository;
import com.ardaslegends.data.service.ClaimBuildService;
import com.ardaslegends.data.service.FactionService;
import com.ardaslegends.data.service.dto.claimbuilds.DeleteClaimbuildDto;
import com.ardaslegends.data.service.dto.claimbuilds.UpdateClaimbuildOwnerDto;
import com.ardaslegends.data.service.exceptions.claimbuild.ClaimBuildServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
public class ClaimbuildServiceTest {

    private ClaimBuildService claimBuildService;
    private ClaimBuildRepository mockClaimbuildRepository;
    private RegionRepository mockRegionRepository;
    private ProductionSiteRepository mockProductionSiteRepository;
    private FactionService mockFactionService;

    private Faction faction;
    private Faction faction2;
    private ClaimBuild claimbuild;
    private ProductionSiteType productionSiteType;
    private ProductionSite productionSite;

    @BeforeEach
    void setup() {
        mockClaimbuildRepository = mock(ClaimBuildRepository.class);
        mockRegionRepository = mock(RegionRepository.class);
        mockFactionService = mock(FactionService.class);
        mockProductionSiteRepository = mock(ProductionSiteRepository.class);

        claimBuildService = new ClaimBuildService(mockClaimbuildRepository, mockRegionRepository, mockProductionSiteRepository, mockFactionService);

        faction = Faction.builder().name("Gondor").build();
        faction2 = Faction.builder().name("Mordor").build();

        productionSiteType = ProductionSiteType.FISHING_LODGE;
        productionSite = ProductionSite.builder().producedResource("Fish").type(productionSiteType).build();
        claimbuild = ClaimBuild.builder().name("Minas Tirith").ownedBy(faction2).build();

        when(mockClaimbuildRepository.findById(claimbuild.getName())).thenReturn(Optional.of(claimbuild));
        when(mockFactionService.getFactionByName(faction.getName())).thenReturn(faction);
        when(mockFactionService.getFactionByName(faction2.getName())).thenReturn(faction2);
        when(mockProductionSiteRepository.
                findProductionSiteByTypeAndProducedResource(productionSiteType, productionSite.getProducedResource()))
                .thenReturn(Optional.of(productionSite));
    }

    @Test
    void ensureSetOwnerFactionWorksProperly() {
        log.debug("Testing if setOwnerFaction works properly with correct values");

        UpdateClaimbuildOwnerDto dto = new UpdateClaimbuildOwnerDto(claimbuild.getName(), faction.getName());

        when(mockClaimbuildRepository.save(claimbuild)).thenReturn(claimbuild);

        log.debug("Calling setOwnerFaction, expecting no errors");
        var result = claimBuildService.setOwnerFaction(dto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(dto.claimbuildName());
        assertThat(result.getOwnedBy().getName()).isEqualTo(dto.newFaction());

        log.info("Test passed: setOwnerFaction works properly with correct values");
    }

    @Test
    void ensureDeleteClaimbuildWorksProperly() {
        log.debug("Testing if deleteClaimbuild works properly");

        DeleteClaimbuildDto dto = new DeleteClaimbuildDto(claimbuild.getName(), null, null);

        log.debug("Calling deleteClaimbuild, expecting no errors");
        var result = claimBuildService.deleteClaimbuild(dto);

        assertThat(result.getName()).isEqualTo(claimbuild.getName());
        log.info("Test passed: deleteClaimbuild works properly");
    }

    @Test
    void ensureGetClaimbuildByNameThrowsSeWhenPassedNameDoesNotHaveACb() {
        log.debug("Testing if getClaimbuildByName throws Se when passed name does not have a corresponding claimbuild in database");

        String name = "Kek";
        when(mockClaimbuildRepository.findById(name)).thenReturn(Optional.empty());

        log.debug("Calling getClaimbuildByName, expecting Se");
        var result = assertThrows(ClaimBuildServiceException.class, () -> claimBuildService.getClaimBuildByName(name));

        assertThat(result.getMessage()).isEqualTo(ClaimBuildServiceException.noCbWithName(name).getMessage());
        log.info("Test passed: getClaimbuildByName correctly throws Se when no cb entry with passed name in database");
    }

    @Test
    void ensureCreateProductionSitesFromStringWorks() {
        log.debug("Testing if createProductionSitesFromString works properly");

        String prodString = "Fishing Lodge:Fish:5";

        log.debug("Calling createProductionSitesFromString");
        List<ProductionClaimbuild> result = claimBuildService.createProductionSitesFromString(prodString, claimbuild);

        ProductionClaimbuild expected = ProductionClaimbuild.builder().claimbuild(claimbuild).productionSite(productionSite)
                .count(5L).build();

        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0).getClaimbuild()).isEqualTo(expected.getClaimbuild());
        assertThat(result.get(0).getProductionSite()).isEqualTo(expected.getProductionSite());
        assertThat(result.get(0).getCount()).isEqualTo(expected.getCount());
        log.info("Test passed: createProductionSitesFromString works properly");
    }

    @Test
    void ensureCreateProductionSitesFromStringThrowsCBSEWhenNoProdTypeFound() {
        log.debug("Testing if createProductionSitesFromString throws ClaimBuildServiceException when no ProductionSiteType found!");

        String str = "kekw Lodge";
        String prodString = str + ":Fish:5";

        log.debug("Calling createProductionSitesFromString");
        var result = assertThrows(ClaimBuildServiceException.class, () -> claimBuildService.createProductionSitesFromString(prodString, claimbuild));

        assertThat(result.getMessage()).isEqualTo(ClaimBuildServiceException.noProductionSiteTypeFound(str).getMessage());
        log.info("Test passed: createProductionSitesFromString throws ClaimBuildServiceException when no ProductionSiteType found!");
    }

    @Test
    void ensureCreateProductionSitesFromStringThrowsCBSEWhenNoProdSiteFound() {
        log.debug("Testing if createProductionSitesFromString throws ClaimBuildServiceException when no ProductionSite found!");

        String type = "Fishing Lodge";
        String resource = "Almond";
        String prodString = "%s:%s:5".formatted(type, resource);

        log.debug("Calling createProductionSitesFromString");
        var result = assertThrows(ClaimBuildServiceException.class, () -> claimBuildService.createProductionSitesFromString(prodString, claimbuild));

        assertThat(result.getMessage()).isEqualTo(ClaimBuildServiceException.noProductionSiteFound(productionSiteType.name(), resource).getMessage());
        log.info("Test passed: createProductionSitesFromString throws ClaimBuildServiceException when no ProductionSite found!");
    }

    @Test
    void ensureCreateProductionSitesFromStringThrowsCBSEWhenAmountNotANumber() {
        log.debug("Testing if createProductionSitesFromString throws ClaimBuildServiceException when inputted amount is not a number!");

        String prodString = "Fishing Lodge:Fish:awdada";

        log.debug("Calling createProductionSitesFromString");
        var result = assertThrows(ClaimBuildServiceException.class, () -> claimBuildService.createProductionSitesFromString(prodString, claimbuild));

        assertThat(result.getMessage()).isEqualTo(ClaimBuildServiceException.invalidProductionSiteString(prodString).getMessage());
        log.info("Test passed: createProductionSitesFromString throws ClaimBuildServiceException when inputted amount is not a number!");
    }
}
