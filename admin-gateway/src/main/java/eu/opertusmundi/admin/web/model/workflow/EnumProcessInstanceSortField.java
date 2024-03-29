package eu.opertusmundi.admin.web.model.workflow;

import lombok.Getter;

public enum EnumProcessInstanceSortField {
    BUSINESS_KEY("ex.business_key_"),
    INCIDENT_COUNT("count(i.id_)"),
    PROCESS_DEFINITION("def.name_"),
    STARTED_ON("hist.start_time_"),
    TASK_COUNT("count(tk)"),
    ;

    @Getter
    private String field;

    private EnumProcessInstanceSortField(String field) {
        this.field = field;
    }

    public static EnumProcessInstanceSortField fromValue(String field) {
        for (final EnumProcessInstanceSortField e : EnumProcessInstanceSortField.values()) {
            if (e.getField().equals(field)) {
                return e;
            }
        }

        throw new IllegalArgumentException(String.format(
            "Value [%s] is not a valid member of enum [EnumProcessInstanceSortField]", field
        ));
    }

}
