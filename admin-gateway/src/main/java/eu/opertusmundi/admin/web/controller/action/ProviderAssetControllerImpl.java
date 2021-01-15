package eu.opertusmundi.admin.web.controller.action;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import eu.opertusmundi.common.model.BaseResponse;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.asset.AssetDraftDto;
import eu.opertusmundi.common.model.asset.AssetDraftReviewCommandDto;
import eu.opertusmundi.common.model.asset.EnumProviderAssetDraftSortField;
import eu.opertusmundi.common.model.asset.EnumProviderAssetDraftStatus;
import eu.opertusmundi.common.model.dto.EnumSortingOrder;
import eu.opertusmundi.common.service.AssetDraftException;
import eu.opertusmundi.common.service.ProviderAssetService;

@RestController
public class ProviderAssetControllerImpl implements ProviderAssetController {

    private static final Logger logger = LoggerFactory.getLogger(ProviderAssetControllerImpl.class);

    @Autowired
    private ProviderAssetService providerAssetService;

    @Override
    public RestResponse<?> findAllDraft(
        Set<EnumProviderAssetDraftStatus> status, UUID providerKey, int pageIndex, int pageSize,
        EnumProviderAssetDraftSortField orderBy, EnumSortingOrder order
    ) {
        try {
            final PageResultDto<AssetDraftDto> result = this.providerAssetService.findAllDraft(
                providerKey, status, pageIndex, pageSize, orderBy, order
            );

            return RestResponse.result(result);
        } catch (final AssetDraftException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        } catch (final Exception ex) {
            logger.error("[Catalogue] Operation has failed", ex);

            return RestResponse.failure();
        }
    }

    @Override
    public RestResponse<AssetDraftDto> findOneDraft(UUID providerKey, UUID draftKey) {
        try {
            final AssetDraftDto draft = this.providerAssetService.findOneDraft(providerKey, draftKey);

            if(draft ==null) {
                return RestResponse.notFound();
            }

            return RestResponse.result(draft);
        } catch (final AssetDraftException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        } catch (final Exception ex) {
            logger.error("[Catalogue] Operation has failed", ex);

            return RestResponse.failure();
        }
    }

    @Override
    public BaseResponse reviewDraft(UUID providerKey, UUID draftKey, AssetDraftReviewCommandDto command) {
        try {
            command.setAssetKey(draftKey);
            command.setPublisherKey(providerKey);

            if (command.isRejected()) {
                this.providerAssetService.rejectHelpDesk(providerKey, draftKey, command.getReason());
            } else {
                this.providerAssetService.acceptHelpDesk(providerKey, draftKey);
            }

            return RestResponse.success();
        } catch (final AssetDraftException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        } catch (final Exception ex) {
            logger.error("[Catalogue] Operation has failed", ex);
        }

        return RestResponse.failure();
    }

}
