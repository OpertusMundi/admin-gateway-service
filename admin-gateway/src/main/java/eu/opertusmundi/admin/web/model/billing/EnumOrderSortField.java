package eu.opertusmundi.admin.web.model.billing;

import lombok.Getter;

public enum EnumOrderSortField {
    CREATED_ON("createdOn"),
    MODIFIED_ON("statusUpdatedOn"),
    STATUS("status"),
    REFERENCE_NUMBER("referenceNumber"),
    ;

    @Getter
    private String value;

    private EnumOrderSortField(String value) {
        this.value = value;
    }

    public static EnumOrderSortField fromValue(String value) {
        for (final EnumOrderSortField e : EnumOrderSortField.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }

        throw new IllegalArgumentException(String.format("Value [%s] is not a valid member of enum [EnumOrderSortField]", value));
    }

}