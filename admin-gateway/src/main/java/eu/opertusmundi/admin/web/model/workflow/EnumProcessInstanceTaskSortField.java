package eu.opertusmundi.admin.web.model.workflow;

import lombok.Getter;

public enum EnumProcessInstanceTaskSortField {
    BUSINESS_KEY("ex.business_key_"),
    INCIDENT_COUNT("count(i.id_)"),
    PROCESS_DEFINITION("def.name_"),
    STARTED_ON("hist.start_time_"),
    ;

    @Getter
    private String field;

    private EnumProcessInstanceTaskSortField(String field) {
        this.field = field;
    }

    public static EnumProcessInstanceTaskSortField fromValue(String field) {
        for (final EnumProcessInstanceTaskSortField e : EnumProcessInstanceTaskSortField.values()) {
            if (e.getField().equals(field)) {
                return e;
            }
        }

        throw new IllegalArgumentException(String.format(
            "Value [%s] is not a valid member of enum [EnumProcessInstanceSortField]", field
        ));
    }

}
