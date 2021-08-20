package eu.opertusmundi.admin.web.model.workflow;

import java.util.Date;

import org.camunda.bpm.engine.rest.dto.VariableValueDto;
import org.camunda.bpm.engine.rest.dto.history.HistoricVariableInstanceDto;

import lombok.Getter;

@Getter
public class VariableDto {

    private Date   createTime;
    private String errorMessage;
    private String name;
    private Date   removalTime;
    private String state;
    private String taskId;
    private String type;
    private Object value;

    public static VariableDto from(String name, VariableValueDto v) {
        final VariableDto result = new VariableDto();

        result.name  = name;
        result.type  = v.getType();
        result.value = v.getValue();

        return result;
    }

    public static VariableDto from(HistoricVariableInstanceDto v) {
        final VariableDto result = new VariableDto();

        result.createTime   = v.getCreateTime();
        result.errorMessage = v.getErrorMessage();
        result.name         = v.getName();
        result.removalTime  = v.getRemovalTime();
        result.state        = v.getState();
        result.type         = v.getType();
        result.value        = v.getValue();

        return result;
    }
}
