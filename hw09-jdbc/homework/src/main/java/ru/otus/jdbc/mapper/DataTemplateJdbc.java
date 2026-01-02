package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;

/** Сохраняет объект в базу, читает объект из базы */
@SuppressWarnings({"java:S1068", "java:S3011", "java:S112"})
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    var constructor = entityClassMetaData.getConstructor();
                    var instance = constructor.newInstance();
                    for (Field field : entityClassMetaData.getAllFields()) {
                        field.setAccessible(true);
                        field.set(instance, rs.getObject(field.getName()));
                    }
                    return instance;
                }
                return null;
            } catch (SQLException e) {
                throw new DataTemplateException(e);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor
                .executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
                    List<T> clientList = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            var constructor = entityClassMetaData.getConstructor();
                            var instance = constructor.newInstance();
                            for (Field field : entityClassMetaData.getAllFields()) {
                                field.setAccessible(true);
                                field.set(instance, rs.getObject(field.getName()));
                            }
                            clientList.add(instance);
                        }
                        return clientList;
                    } catch (SQLException e) {
                        throw new DataTemplateException(e);
                    } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseThrow(() -> new RuntimeException("Unexpected error"));
    }

    @Override
    public long insert(Connection connection, T object) {
        try {
            List<Object> fields = new ArrayList<>();
            for (Field field : entityClassMetaData.getFieldsWithoutId()) {
                field.setAccessible(true);
                fields.add(field.get(object));
            }
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(), fields);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    @Override
    public void update(Connection connection, T client) {
        try {
            var fields = entityClassMetaData.getFieldsWithoutId().stream()
                    .map(f -> (Object) f.getName())
                    .toList();
            fields.addLast(entityClassMetaData.getIdField().getName());
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), fields);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }
}
