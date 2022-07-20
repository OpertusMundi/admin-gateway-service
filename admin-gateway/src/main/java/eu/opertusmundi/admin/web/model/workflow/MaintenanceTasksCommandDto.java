package eu.opertusmundi.admin.web.model.workflow;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaintenanceTasksCommandDto {

    private Boolean deleteOrphanFileSystemEntries;
    private Boolean removeOrphanCatalogueItems;
    private Boolean resizeImages;

}
