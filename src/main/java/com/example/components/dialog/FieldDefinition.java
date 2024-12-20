package com.example.components.dialog;

public class FieldDefinition {
    private String fieldName;
    private String displayName;
    private String dataType;
    private Object value;

    public FieldDefinition(String fieldName, String displayName, String dataType, Object value) {
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.dataType = dataType;
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDataType() {
        return dataType;
    }

    public Object getValue() {
        return value;
    }
}
