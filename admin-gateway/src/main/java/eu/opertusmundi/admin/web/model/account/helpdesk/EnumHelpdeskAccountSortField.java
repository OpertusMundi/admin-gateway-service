package eu.opertusmundi.admin.web.model.account.helpdesk;

import lombok.Getter;

public enum EnumHelpdeskAccountSortField {
    EMAIL("email"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    ;

    @Getter
    private String value;

    private EnumHelpdeskAccountSortField(String value) {
        this.value = value;
    }

    public static EnumHelpdeskAccountSortField fromValue(String value) {
        for (final EnumHelpdeskAccountSortField e : EnumHelpdeskAccountSortField.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }

        throw new IllegalArgumentException(String.format(
            "Value [%s] is not a valid member of enum [EnumHelpdeskAccountSortField]", value
        ));
    }

}