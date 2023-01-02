package eu.opertusmundi.admin.web.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import eu.opertusmundi.admin.web.service.BpmEngineService;
import eu.opertusmundi.common.model.workflow.EnumWorkflow;

/**
 * {@link ApplicationRunner} used to validate workflow deployments
 */
@Component
public class WorkflowDeploymentValidationRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(WorkflowDeploymentValidationRunner.class);

    private final BpmEngineService bpmEngineService;

    public WorkflowDeploymentValidationRunner(BpmEngineService bpmEngineService) {
        this.bpmEngineService = bpmEngineService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        this.bpmEngineService.getProcessDefinitions().stream()
            .filter(def -> EnumWorkflow.fromKey(def.getKey()) == null)
            .forEach(def -> logger.warn(
                "Could not map workflow definition to workflow enum value [key={}, name={}, version={}]", 
                def.getKey(), def.getName(), def.getVersion()
            ));
    }

}
