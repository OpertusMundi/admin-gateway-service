package eu.opertusmundi.admin.web.model.workflow;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifyProcessInstanceCommandDto {

    private List<String> startActivities;
    private List<String> cancelActivities;

}
