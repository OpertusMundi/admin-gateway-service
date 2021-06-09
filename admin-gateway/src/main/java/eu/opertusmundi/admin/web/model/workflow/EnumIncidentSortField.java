package eu.opertusmundi.admin.web.model.workflow;

import lombok.Getter;

public enum EnumIncidentSortField {
    BUSINESS_KEY("ex.business_key_"),
    PROCESS_DEFINITION("def.name_"),
    REPORTED_ON("i.incident_timestamp_"),
    TASK_NAME("et.topic_name_"),
    TASK_WORKER("et.worker_id_"),
    ;

    @Getter
    private String field;

    private EnumIncidentSortField(String field) {
        this.field = field;
    }

    public static EnumIncidentSortField fromValue(String field) {
        for (final EnumIncidentSortField e : EnumIncidentSortField.values()) {
            if (e.getField().equals(field)) {
                return e;
            }
        }

        throw new IllegalArgumentException(String.format(
            "Value [%s] is not a valid member of enum [EnumWorkflowInstanceSortField]", field
        ));
    }

}
