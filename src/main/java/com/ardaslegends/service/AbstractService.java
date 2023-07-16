package com.ardaslegends.service;

import com.ardaslegends.domain.AbstractDomainObject;
import com.ardaslegends.presentation.discord.config.BotProperties;
import com.ardaslegends.presentation.discord.utils.ALColor;
import com.ardaslegends.service.exceptions.ServiceException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.javacord.api.entity.channel.TextChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.PersistenceException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public abstract class AbstractService<T extends AbstractDomainObject, R extends JpaRepository<T, ?>>{
    private TextChannel errorChannel;

    @Getter
    private ExecutorService executorService;

    /***
     * This method was specifically made for Query Operations of JPA Repository Classes.
     * Wraps the passed method into a try catch block, catching PersistenceException and proper logging
     * @param identifier the identifier, also the parameter of the passed function
     * @param func function that will be used in the operation
     * @return equals the passed function's return type
     */
    public <G, A> A secureFind(G identifier, Function<G, A> func) {
        if (func == null) {
            log.warn("SecureFind Function parameter is null!");
            throw ServiceException.passedNullFunction();
        }
        try {
            return func.apply(identifier);
        } catch (Exception pEx) {
            log.warn("Encountered Database Error while searching for entity, parameter [{}]", identifier);
            recordMessageInErrorChannel(pEx);
            throw ServiceException.secureFindFailed(identifier, pEx);
        }
    }

    public <A> A secureFind(Supplier<A> func) {
        if (func == null) {
            log.warn("SecureFind Function parameter is null!");
            throw ServiceException.passedNullFunction();
        }
        try {
            return func.get();
        } catch (Exception pEx) {
            log.warn("Encountered Database Error while searching for entity, parameter [{}]", func);
            recordMessageInErrorChannel(pEx);
            throw ServiceException.secureFindFailed(func, pEx);
        }
    }

    public <G, T, A> A secureFind(G identifier, T other, BiFunction<G, T, A> func) {
        if (func == null) {
            log.warn("SecureFind Function parameter is null!");
            throw ServiceException.passedNullFunction();
        }
        try {
            return func.apply(identifier, other);
        } catch (Exception pEx) {
            log.warn("Encountered Database Error while searching for entity, parameter [{}]", identifier);
            recordMessageInErrorChannel(pEx);
            throw ServiceException.secureFindFailed(identifier, pEx);
        }
    }

    public T secureSave(T entity, R repository) {
       try {
            return repository.save(entity);
       } catch (Exception pEx) {
            log.warn("Encountered Database Error while saving entity [{}]", entity);
            recordMessageInErrorChannel(pEx);
            throw ServiceException.cannotSaveEntity(entity, pEx);
       }
    }

    public List<T> secureSaveAll(Collection<T> entities, R repository) {
        try {
            return repository.saveAll(entities);
        } catch (Exception pEx) {
            log.warn("Encountered Database Error while saving entity [{}]", entities);
            recordMessageInErrorChannel(pEx);
            throw ServiceException.cannotSaveEntity(entities, pEx);
        }
    }

    public void secureDelete(T entity, R repository) {
        try {
            repository.delete(entity);
        } catch (Exception pEx) {
            log.warn("Encountered Database Error while deleting entity[{}]", entity);
            recordMessageInErrorChannel(pEx);
            throw ServiceException.cannotSaveEntity(entity, pEx);
        }
    }

    public <G> G secureJoin(CompletableFuture<G> completableFuture) {
        Objects.requireNonNull(completableFuture);
        try {
            return completableFuture.join();
        } catch (Exception ex) {
            log.warn("Unexpected exception in join [{}]", ex.getMessage());
            recordMessageInErrorChannel(ex);
            throw ServiceException.joinException(ex);
        }
    }

    protected void recordMessageInErrorChannel(Throwable throwable) {
        val embed = new EmbedBuilder()
                .setTitle(throwable.getClass().getSimpleName())
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
