package ru.otus.jdbc.mapper;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

    private final EntityClassMetaData<T> entityClassMetaData;

    public EntitySQLMetaDataImpl(EntityClassMetaData<T> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        return "select * from " + getTableName();
    }

    @Override
    public String getSelectByIdSql() {
        return "select " + getAllFieldsCommaSeparated() + " from "
                + getTableName() + " where "
                + getIdFieldName() + "  = ?";
    }

    @Override
    public String getInsertSql() {
        var fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        var questionMarks =
                IntStream.range(0, fieldsWithoutId.size()).mapToObj(i -> "?").collect(Collectors.joining(","));
        return "insert into " + getTableName() + "(" + getAllFieldsWithoutIdCommaSeparated(fieldsWithoutId)
                + ") values (" + questionMarks + ")";
    }

    @Override
    public String getUpdateSql() {
        var fields = entityClassMetaData.getFieldsWithoutId();
        var fieldsAsString = fields.stream().map(f -> f.getName() + " = ?").collect(Collectors.joining(", "));
        return "update " + getTableName() + "set " + fieldsAsString + " where " + getIdFieldName() + "  = ?";
    }

    private String getTableName() {
        return entityClassMetaData.getName();
    }

    private String getAllFieldsCommaSeparated() {
        var allFields = entityClassMetaData.getAllFields();
        return allFields.stream().map(Field::getName).collect(Collectors.joining(", "));
    }

    private String getIdFieldName() {
        return entityClassMetaData.getIdField().getName();
    }

    private String getAllFieldsWithoutIdCommaSeparated(List<Field> fieldsWithoutId) {
        return fieldsWithoutId.stream().map(Field::getName).collect(Collectors.joining(", "));
    }
}
