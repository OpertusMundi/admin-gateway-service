package eu.opertusmundi.admin.web.model.workflow;

import lombok.Getter;

public enum EnumProcessInstanceHistorySortField {
    BUSINESS_KEY("hist.business_key_"),
    COMPLETED_ON("hist.end_time_"), 
    PROCESS_DEFINITION("def.name_"),
    STARTED_ON("hist.start_time_"),
    ;

    @Getter
    private String field;

    private EnumProcessInstanceHistorySortField(String field) {
        this.field = field;
    }

    public static EnumProcessInstanceHistorySortField fromValue(String field) {
        for (final EnumProcessInstanceHistorySortField e : EnumProcessInstanceHistorySortField.values()) {
            if (e.getField().equals(field)) {
                return e;
            }
        }

        throw new IllegalArgumentException(String.format(
            "Value [%s] is not a valid member of enum [EnumProcessInstanceHistorySortField]", field
        ));
    }

}
