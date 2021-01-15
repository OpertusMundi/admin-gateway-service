package eu.opertusmundi.admin.web.model.dto;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProfileCommandDto {

    @NotEmpty
    private String firstName;

    @JsonIgnore
    private Integer id;

    private byte[] image;

    private String imageMimeType;

    @NotEmpty
    private String lastName;

    @NotEmpty
    private String locale;

    @NotEmpty
    private String mobile;

    private String phone;

}
