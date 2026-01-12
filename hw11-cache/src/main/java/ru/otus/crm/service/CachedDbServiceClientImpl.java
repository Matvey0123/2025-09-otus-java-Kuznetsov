package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

public class CachedDbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(CachedDbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final HwCache<String, Client> clientCache;

    public CachedDbServiceClientImpl(
            TransactionManager transactionManager,
            DataTemplate<Client> clientDataTemplate,
            HwCache<String, Client> clientCache) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        this.clientCache = clientCache;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                var savedClient = clientDataTemplate.insert(session, clientCloned);
                clientCache.put(generateCacheKey(savedClient.getId()), savedClient);
                log.info("created client: {}", clientCloned);
                return savedClient;
            }
            var savedClient = clientDataTemplate.update(session, clientCloned);
            var cacheKey = generateCacheKey(clientCloned.getId());
            clientCache.remove(cacheKey);
            clientCache.put(cacheKey, savedClient);
            log.info("updated client: {}", savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        var cacheKey = generateCacheKey(id);
        var client = clientCache.get(cacheKey);
        if (client != null) {
            log.info("client: {}", client);
            return Optional.of(client);
        }
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            clientOptional.ifPresent(c -> clientCache.put(cacheKey, c));
            log.info("client: {}", clientOptional);
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            clientList.forEach(c -> clientCache.put(generateCacheKey(c.getId()), c));
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }

    private static String generateCacheKey(Long clientId) {
        return Client.class.getSimpleName() + "_" + clientId;
    }
}
