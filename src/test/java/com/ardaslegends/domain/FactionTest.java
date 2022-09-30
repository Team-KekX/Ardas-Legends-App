package com.ardaslegends.domain;

import com.ardaslegends.service.exceptions.FactionServiceException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
public class FactionTest {

    @Test
    void ensureAddFoodToStockpileWorksProperly() {
        log.debug("Testing if faction.addFoodToStockpile works properly");

        Faction faction = Faction.builder().name("Kek").foodStockpile(0).build();

        int amountToAdd = 10;

        assertThat(faction.getFoodStockpile()).isEqualTo(0);
        faction.addFoodToStockpile(amountToAdd);
        assertThat((faction.getFoodStockpile())).isEqualTo(0 + amountToAdd);

        log.info("Test passed: addFoodToStockpile works properly with correct values");
    }

    @Test
    void ensureAddFoodToStockpileThrowsSeWhenAmountIsNegative() {
        log.debug("Testing if addFoodToStockpile throws Se when add amount is negative");
        Faction faction = new Faction();

        var result = assertThrows(FactionServiceException.class, () -> faction.addFoodToStockpile(-5));

        assertThat(result.getMessage()).isEqualTo(FactionServiceException.negativeStockpileAddNotSupported().getMessage());
        log.info("Test passed: addToFoodStockpile properly throws Se when adding negative amount");
    }

    @Test
    void ensureSubtractFoodToStockpileWorksProperly() {
        log.debug("Testing if faction.subtractFoodToStockpile works properly");

        Faction faction = Faction.builder().name("Kek").foodStockpile(20).build();

        int amountToRemove = 10;

        assertThat(faction.getFoodStockpile()).isEqualTo(20);
        faction.subtractFoodFromStockpile(amountToRemove);
        assertThat((faction.getFoodStockpile())).isEqualTo(20 - amountToRemove);

        log.info("Test passed: subtractFoodToStockpile works properly with correct values");
    }

    @Test
    void ensureSubtractFoodToStockpileThrowsSeWhenAmountIsNegative() {
        log.debug("Testing if subtractFoodToStockpile throws Se when remove amount is positive");
        Faction faction = new Faction();

        var result = assertThrows(FactionServiceException.class, () -> faction.subtractFoodFromStockpile(-5));

        assertThat(result.getMessage()).isEqualTo(FactionServiceException.negativeStockpileSubtractNotSupported().getMessage());
        log.info("Test passed: subtractFoodStockpile properly throws Se when removing positive amount");
    }

    @Test
    void ensureSubtractFoodFromStockpileThrowsSeWhenSubtractionWouldPushStockpileIntoNegative() {
        log.debug("Testing if subtractFoodToStockpile throws Se when subtraction would push stockpile into negative");
        Faction faction = new Faction();
        faction.setFoodStockpile(0);

        var result = assertThrows(FactionServiceException.class, () -> faction.subtractFoodFromStockpile(10));

        assertThat(result.getMessage()).isEqualTo(FactionServiceException.notEnoughFoodInStockpile(faction.toString(), faction.getFoodStockpile(), 10).getMessage());
        log.info("Test passed: subtractFoodStockpile properly throws Se when subtraction would push stockpile into negative");
    }
}
