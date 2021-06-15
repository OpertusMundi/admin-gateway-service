package eu.opertusmundi.admin.web.model.account.market;

import lombok.Getter;

public enum EnumMarketpalceAccountSortField {
    EMAIL("email"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    ;

    @Getter
    private String value;

    private EnumMarketpalceAccountSortField(String value) {
        this.value = value;
    }

    public static EnumMarketpalceAccountSortField fromValue(String value) {
        for (final EnumMarketpalceAccountSortField e : EnumMarketpalceAccountSortField.values()) {
            if (e.getValue().equals(value)) {
                return e;
            }
        }

        throw new IllegalArgumentException(String.format(
            "Value [%s] is not a valid member of enum [EnumMarketpalceAccountSortField]", value
        ));
    }

}
