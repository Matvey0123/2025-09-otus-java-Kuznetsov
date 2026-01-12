package ru.otus.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.CachedDbServiceClientImpl;
import ru.otus.crm.service.DBServiceClient;

@SuppressWarnings({"java:S1604", "java:S1192", "java:S125", "java:S1144"})
public class DbServiceDemo {

    private static final Logger log = LoggerFactory.getLogger(DbServiceDemo.class);

    public static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    public static void main(String[] args) {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory =
                HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);
        ///
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        ///
        //        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
        //        doWithoutCache(dbServiceClient);

        var cache = new MyCache<String, Client>();
        cache.addListener(new HwListener<String, Client>() {
            @Override
            public void notify(String key, Client value, String action) {
                log.info("key:{}, value:{}, action: {}", key, value, action);
            }
        });
        var dbServiceClientCached = new CachedDbServiceClientImpl(transactionManager, clientTemplate, cache);
        doWithCache(dbServiceClientCached);
    }

    private static void doWithoutCache(DBServiceClient dbServiceClient) {
        dbServiceClient.saveClient(new Client("dbServiceFirst"));
        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));
        var start = System.currentTimeMillis();
        var clientSecondSelected = dbServiceClient
                .getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        var finish = System.currentTimeMillis();
        log.info("clientSecondSelected:{}", clientSecondSelected);
        log.info("Get client without cache took {} ms", finish - start);
        ///
        dbServiceClient.saveClient(new Client(clientSecondSelected.getId(), "dbServiceSecondUpdated"));
        var start2 = System.currentTimeMillis();
        var clientUpdated = dbServiceClient
                .getClient(clientSecondSelected.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
        log.info("clientUpdated:{}", clientUpdated);
        var finish2 = System.currentTimeMillis();
        log.info("Get client without cache took {} ms", finish2 - start2);

        log.info("All clients");
        dbServiceClient.findAll().forEach(client -> log.info("client:{}", client));
    }

    private static void doWithCache(DBServiceClient dbServiceClient) {
        dbServiceClient.saveClient(new Client("dbServiceFirst"));
        var clientSecond = dbServiceClient.saveClient(new Client("dbServiceSecond"));
        var start = System.currentTimeMillis();
        var clientSecondSelected = dbServiceClient
                .getClient(clientSecond.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecond.getId()));
        var finish = System.currentTimeMillis();
        log.info("clientSecondSelected:{}", clientSecondSelected);
        log.info("Get client with cache took {} ms", finish - start);
        ///
        dbServiceClient.saveClient(new Client(clientSecondSelected.getId(), "dbServiceSecondUpdated"));
        var start2 = System.currentTimeMillis();
        var clientUpdated = dbServiceClient
                .getClient(clientSecondSelected.getId())
                .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientSecondSelected.getId()));
        log.info("clientUpdated:{}", clientUpdated);
        var finish2 = System.currentTimeMillis();
        log.info("Get client with cache took {} ms", finish2 - start2);

        log.info("All clients");
        dbServiceClient.findAll().forEach(client -> log.info("client:{}", client));
    }
}
