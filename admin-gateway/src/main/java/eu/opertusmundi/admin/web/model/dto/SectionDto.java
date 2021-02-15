package eu.opertusmundi.admin.web.model.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@Data
public class SectionDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	@NotNull
	private ContractDto contract;
	private Integer indent;
	private String index;
	@NotEmpty
	private String title;
	private boolean variable;
	private boolean optional;
	private boolean dynamic;
	private List<String> options;
	private List<String> styledOptions;
	private List<String> summary;
	private List<String> icons;
	private String descriptionOfChange;

}
