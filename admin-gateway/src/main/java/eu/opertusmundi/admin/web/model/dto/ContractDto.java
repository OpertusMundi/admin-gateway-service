package eu.opertusmundi.admin.web.model.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import eu.opertusmundi.admin.web.domain.SectionEntity;
import eu.opertusmundi.common.model.dto.AccountDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
@Getter
@Setter
public class ContractDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	@NotEmpty
	private String title;
	
	private String subtitle;
	
	private String state;
	
	private String version;
	
	@JsonIgnore
	private AccountDto account;
	

	private List<SectionDto> sections;
	
    private ZonedDateTime createdAt;
    
    private ZonedDateTime modifiedAt;
}
