package com.ardaslegends.repository;

import com.ardaslegends.domain.AbstractDomainObject;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.repository.exceptions.DataAccessException;
import com.ardaslegends.repository.exceptions.DataAccessNullPointerException;
import com.ardaslegends.service.exceptions.ServiceException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public abstract class AbstractDataAccess<T extends AbstractDomainObject, R extends JpaRepository<T, ?>> {

    private TextChannel errorChannel;

    @Getter
    private ExecutorService executorService;

    protected void requireParameterNonNull(Object parameter, String paramName, String methodName) {
        log.trace("Checking DataAccess parameter '{}' of function '{}' for null (value is '{}')", paramName, methodName, parameter);
        if(parameter == null) {
            log.warn("DataAccess layer parameter '{}' of method '{}' was null!", paramName, methodName);
            throw DataAccessNullPointerException.queryMethodParameterWasNull(paramName, methodName);
        }
    }

    /***
     * This method was specifically made for Query Operations of JPA Repository Classes.
     * Wraps the passed method into a try catch block, catching PersistenceException and proper logging
     * @param identifier the identifier, also the parameter of the passed function
     * @param func function that will be used in the operation
     * @return equals the passed function's return type
     */
    protected <G, A> A secureFind(G identifier, Function<G, A> func) {
        if (func == null) {
            log.warn("SecureFind Function parameter is null!");
            throw DataAccessNullPointerException.secureFindFunctionWasNull();
        }
        try {
            return func.apply(identifier);
        } catch (Exception exception) {
            log.warn("Encountered Database Error while searching for entity, parameter [{}]", identifier);
            recordMessageInErrorChannel(exception);
            throw DataAccessException.unexpectedDatabaseError(exception);
        }
    }

    protected <A> A secureFind(Supplier<A> func) {
        if (func == null) {
            log.warn("SecureFind Function parameter is null!");
            throw DataAccessNullPointerException.secureFindFunctionWasNull();
        }
        try {
            return func.get();
        } catch (Exception exception) {
            log.warn("Encountered Database Error while searching for entity");
            recordMessageInErrorChannel(exception);
            throw DataAccessException.unexpectedDatabaseError(exception);
        }
    }

    protected <G, T, A> A secureFind(G identifier, T other, BiFunction<G, T, A> func) {
        if (func == null) {
            log.warn("SecureFind Function parameter is null!");
            throw DataAccessNullPointerException.secureFindFunctionWasNull();
        }
        try {
            return func.apply(identifier, other);
        } catch (Exception exception) {
            log.warn("Encountered Database Error while searching for entity, parameter [{}]", identifier);
            recordMessageInErrorChannel(exception);
            throw DataAccessException.unexpectedDatabaseError(exception);
        }
    }

    protected T secureSave(T entity, R repository) {
        try {
            return repository.save(entity);
        } catch (Exception pEx) {
            log.warn("Encountered Database Error while saving entity [{}]", entity);
            recordMessageInErrorChannel(pEx);
            throw DataAccessException.couldNotSaveEntity(entity, pEx);
        }
    }

    protected List<T> secureSaveAll(Collection<T> entities, R repository) {
        try {
            return repository.saveAll(entities);
        } catch (Exception pEx) {
            log.warn("Encountered Database Error while saving entity [{}]", entities);
            recordMessageInErrorChannel(pEx);
            throw DataAccessException.couldNotSaveEntity(entities, pEx);
        }
    }

    protected void secureDelete(T entity, R repository) {
        try {
            repository.delete(entity);
        } catch (Exception pEx) {
            log.warn("Encountered Database Error while deleting entity[{}]", entity);
            recordMessageInErrorChannel(pEx);
            throw DataAccessException.couldNotDeleteEntity(entity, pEx);
        }
    }

    protected void recordMessageInErrorChannel(Throwable throwable) {
        val embed = new EmbedBuilder()
                .setTitle("Unexpected Error: " + throwable.getClass().getSimpleName())
                .setDescription(throwable.getMessage())
                .setTimestampToNow()
                .setColor(ALColor.RED);

        errorChannel.sendMessage(embed);
    }

    @Autowired
    public final void setErrorChannel(BotProperties properties) {
        this.errorChannel = properties.getErrorChannel();
    }

    @Autowired
    public final void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
