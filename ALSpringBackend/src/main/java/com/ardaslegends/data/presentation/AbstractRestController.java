package com.ardaslegends.data.presentation;

import com.ardaslegends.data.domain.AbstractDomainEntity;
import com.ardaslegends.data.presentation.exceptions.BadArgumentException;
import com.ardaslegends.data.presentation.exceptions.InternalServerException;
import com.ardaslegends.data.service.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public abstract class AbstractRestController {

    public <T> T wrappedServiceExecution(Supplier<T> supplier) {
        if(supplier == null) {
            log.warn("Null Supplier passed in wrappedServiceExecution");
            throw new InternalServerException("Passed Null SupplierFunction in wrappedServiceExecution", null);
        }
        try {
            return supplier.get();
        } catch (NullPointerException | IllegalArgumentException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BadArgumentException(e.getMessage(), e);
        } catch (ServiceException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new InternalServerException(e.getMessage(), e);
        }
    }
    public <T, G> T wrappedServiceExecution(G dto, Function<G, T> func) {
        if(func == null)
            throw new InternalServerException("Passed Null Function in wrappedServiceExecution", null);

        try {
            return func.apply(dto);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BadArgumentException(e.getMessage(), e);
        } catch (ServiceException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new InternalServerException(e.getMessage(), e);
        }
    }

    public <T extends AbstractDomainEntity, G, X> T wrappedServiceExecution(G dto, X otherParam, BiFunction<G, X, T> func) {
        if(func == null)
            throw new InternalServerException("Passed Null Function in wrappedServiceExecution", null);

        try {
            return func.apply(dto, otherParam);
        } catch (NullPointerException | IllegalArgumentException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new BadArgumentException(e.getMessage(), e);
        } catch (ServiceException e) {
            log.warn("Encountered exception while updating player: Type: {} - Msg: {}", e.getClass().getSimpleName(), e.getMessage());
            throw new InternalServerException(e.getMessage(), e);
        }
    }

}
