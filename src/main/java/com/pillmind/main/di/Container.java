package com.pillmind.main.di;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Container de Injeção de Dependências (IoC Container)
 * Implementa o padrão Singleton para componentes com escopo único
 * e permite registro de factories para criação sob demanda
 */
public class Container {
    private static final Logger logger = LoggerFactory.getLogger(Container.class);

    private final Map<String, Object> singletons = new HashMap<>();
    private final Map<String, Supplier<?>> factories = new HashMap<>();

    /**
     * Registra um singleton no container
     */
    public <T> void registerSingleton(String key, T instance) {
        logger.debug("Registrando singleton: {}", key);
        singletons.put(key, instance);
    }

    /**
     * Registra uma factory que será chamada cada vez que o componente for
     * solicitado
     */
    public <T> void registerFactory(String key, Supplier<T> factory) {
        logger.debug("Registrando factory: {}", key);
        factories.put(key, factory);
    }

    /**
     * Resolve uma dependência do container
     * Primeiro tenta encontrar um singleton, depois tenta a factory
     */
    @SuppressWarnings("unchecked")
    public <T> T resolve(String key) {
        // Verifica se é um singleton registrado
        if (singletons.containsKey(key)) {
            logger.debug("Resolvendo singleton: {}", key);
            return (T) singletons.get(key);
        }

        // Verifica se é uma factory registrada
        if (factories.containsKey(key)) {
            logger.debug("Resolvendo factory: {}", key);
            return (T) factories.get(key).get();
        }

        throw new ContainerException("Dependência não registrada: " + key);
    }

    /**
     * Resolve uma dependência do container com validação de tipo
     */
    @SuppressWarnings("unchecked")
    public <T> T resolve(String key, Class<T> type) {
        Object instance = resolve(key);

        if (!type.isInstance(instance)) {
            throw new ContainerException(
                    "Dependência " + key + " não é do tipo " + type.getSimpleName());
        }

        return (T) instance;
    }

    /**
     * Exceção específica para erro no container
     */
    public static class ContainerException extends RuntimeException {
        public ContainerException(String message) {
            super(message);
        }

        public ContainerException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
