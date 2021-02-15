package eu.opertusmundi.admin.web.model.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class ContractHistoryDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;

	@JsonIgnore
	private ContractDto parentId;
	
	@NotEmpty
	private String title;
	
	
	private String subtitle;
	
	private String state;
	
	private String version;
	
	@JsonIgnore
	private AccountDto account;
	

	private List<SectionHistoryDto> sections;
	
    private ZonedDateTime createdAt;
    
    private ZonedDateTime modifiedAt;
}
