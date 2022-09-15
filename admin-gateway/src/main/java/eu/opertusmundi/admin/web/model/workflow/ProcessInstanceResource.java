package eu.opertusmundi.admin.web.model.workflow;

import eu.opertusmundi.common.model.workflow.EnumProcessInstanceResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor(staticName = "of")
@Builder
@Getter
public final class ProcessInstanceResource {

    EnumProcessInstanceResource type;
    Object                      value;

}
