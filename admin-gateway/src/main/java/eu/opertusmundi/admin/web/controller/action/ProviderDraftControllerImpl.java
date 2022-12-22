package eu.opertusmundi.admin.web.controller.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import eu.opertusmundi.common.model.BaseResponse;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.PageResultDto;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.asset.AssetDraftDto;
import eu.opertusmundi.common.model.asset.AssetDraftReviewCommandDto;
import eu.opertusmundi.common.model.asset.EnumProviderAssetDraftSortField;
import eu.opertusmundi.common.model.asset.EnumProviderAssetDraftStatus;
import eu.opertusmundi.common.service.AssetDraftException;
import eu.opertusmundi.common.service.ProviderAssetService;

@RestController
public class ProviderDraftControllerImpl extends BaseController implements ProviderDraftController {

    private static final Logger logger = LoggerFactory.getLogger(ProviderDraftControllerImpl.class);

    @Autowired
    private ProviderAssetService providerAssetService;

    @Override
    public RestResponse<?> findAllDraft(
        Set<EnumProviderAssetDraftStatus> status, UUID providerKey, int pageIndex, int pageSize,
        EnumProviderAssetDraftSortField orderBy, EnumSortingOrder order
    ) {
        try {
            final PageResultDto<AssetDraftDto> result = this.providerAssetService.findAllDraft(
                providerKey, providerKey, status, null, null, null, pageIndex, pageSize, orderBy, order
            );

			return RestResponse.result(result);
		} catch (final AssetDraftException ex) {
			return RestResponse.error(ex.getCode(), ex.getMessage());
		} catch (final Exception ex) {
			logger.error("Operation has failed", ex);

			return RestResponse.failure();
		}
	}

    @Override
    public RestResponse<AssetDraftDto> findOneDraft(UUID providerKey, UUID draftKey) {
        try {
            final AssetDraftDto draft = this.providerAssetService.findOneDraft(providerKey, providerKey, draftKey, false);

			if (draft == null) {
				return RestResponse.notFound();
			}

            return RestResponse.result(draft);
        } catch (final AssetDraftException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        } catch (final Exception ex) {
            logger.error("Operation has failed", ex);

            return RestResponse.failure();
        }
    }

    @Override
    public BaseResponse reviewDraft(UUID providerKey, UUID draftKey, AssetDraftReviewCommandDto command) {
        try {
            command.setDraftKey(draftKey);
            command.setOwnerKey(providerKey);
            command.setPublisherKey(providerKey);
            command.setReviewerKey(currentUserKey());

            this.providerAssetService.reviewHelpDesk(command);

            return RestResponse.success();
        } catch (final AssetDraftException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        } catch (final Exception ex) {
            logger.error("Operation has failed", ex);
        }

        return RestResponse.failure();
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getContract(UUID draftKey, HttpServletResponse response) throws IOException {
        final AssetDraftDto    draft        = this.providerAssetService.findOneDraft(draftKey);
        final UUID             publisherKey = draft.getPublisher().getKey();
        // We set publisher key to the owner key value. Helpdesk account can
        // review any draft
        final Path path = this.providerAssetService.resolveDraftContractPath(publisherKey, publisherKey, draftKey);
        final File file = path.toFile();

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.setHeader("Content-Disposition", String.format("attachment; filename=%s", file.getName()));
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", Long.toString(file.length()));

        final StreamingResponseBody stream = out -> {
            try (InputStream inputStream = new FileInputStream(file)) {
                IOUtils.copyLarge(inputStream, out);
            }
        };

        return new ResponseEntity<StreamingResponseBody>(stream, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> getContractAnnex(
        UUID draftKey, String annexKey, HttpServletResponse response
    ) throws IOException {
        final AssetDraftDto    draft        = this.providerAssetService.findOneDraft(draftKey);
        final UUID             publisherKey = draft.getPublisher().getKey();
        // We set publisher key to the owner key value. Helpdesk account can
        // review any draft
        final Path path = this.providerAssetService.resolveDraftContractAnnexPath(publisherKey, publisherKey, draftKey, annexKey);
        final File file = path.toFile();

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.setHeader("Content-Disposition", String.format("attachment; filename=%s", file.getName()));
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", Long.toString(file.length()));

        final StreamingResponseBody stream = out -> {
            try (InputStream inputStream = new FileInputStream(file)) {
                IOUtils.copyLarge(inputStream, out);
            }
        };

        return new ResponseEntity<StreamingResponseBody>(stream, HttpStatus.OK);
    }
    
    @Override
    public BaseResponse deleteDraft(UUID providerKey, UUID draftKey) {
        try {
            this.providerAssetService.deleteDraft(providerKey, providerKey, draftKey);

            return RestResponse.success();
        } catch (final AssetDraftException ex) {
            return RestResponse.error(ex.getCode(), ex.getMessage());
        } catch (final Exception ex) {
            logger.error("Operation has failed", ex);
        }

        return RestResponse.failure();
    }
}
