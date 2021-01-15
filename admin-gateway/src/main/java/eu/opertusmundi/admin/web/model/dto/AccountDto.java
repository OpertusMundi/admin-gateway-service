package eu.opertusmundi.admin.web.model.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class AccountDto extends AccountBaseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private ZonedDateTime createdOn;
    private boolean       emailVerified;
    private ZonedDateTime emailVerifiedOn;
    private Integer       id;
    private UUID          key;
    private ZonedDateTime modifiedOn;

}
