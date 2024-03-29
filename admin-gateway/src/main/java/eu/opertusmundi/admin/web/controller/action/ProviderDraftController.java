package eu.opertusmundi.admin.web.controller.action;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import eu.opertusmundi.common.model.BaseResponse;
import eu.opertusmundi.common.model.EnumSortingOrder;
import eu.opertusmundi.common.model.RestResponse;
import eu.opertusmundi.common.model.asset.AssetDraftDto;
import eu.opertusmundi.common.model.asset.AssetDraftReviewCommandDto;
import eu.opertusmundi.common.model.asset.EnumProviderAssetDraftSortField;
import eu.opertusmundi.common.model.asset.EnumProviderAssetDraftStatus;
import eu.opertusmundi.common.model.catalogue.client.CatalogueClientCollectionResponse;
import eu.opertusmundi.common.model.catalogue.client.CatalogueItemDto;

@RequestMapping(path = "/action", produces = "application/json")
@Secured({"ROLE_USER"})
public interface ProviderDraftController {

    /**
     * Search catalogue draft items
     *
     * @param status
     * @param providerKey
     * @param pageIndex
     * @param pageSize
     * @param orderBy
     * @param order
     *
     * @return An instance of {@link CatalogueClientCollectionResponse} class
     */
    @GetMapping(value = "/provider/drafts")
    RestResponse<?> findAllDraft(
        @RequestParam(name = "status", required = false) Set<EnumProviderAssetDraftStatus> status,
        @RequestParam(name = "provider", required = false) UUID providerKey,
        @RequestParam(name = "page", defaultValue = "0") int pageIndex,
        @RequestParam(name = "size", defaultValue = "10") int pageSize,
        @RequestParam(name = "orderBy", defaultValue = "name") EnumProviderAssetDraftSortField orderBy,
        @RequestParam(name = "order", defaultValue = "ASC") EnumSortingOrder order
    );

    /**
     * Get a single catalogue draft item
     *
     * @param providerKey The provider unique key
     * @param draftKey The item unique key
     * @return A response with a result of type {@link CatalogueItemDto}
     */
    @GetMapping(value = "/provider/{providerKey}/drafts/{draftKey}")
    RestResponse<AssetDraftDto> findOneDraft(
        @PathVariable UUID providerKey,
        @PathVariable UUID draftKey
    );

    /**
     * Review draft
     *
     * @param providerKey The provider unique key
     * @param draftKey The item unique key
     * @param command The status update command
     * @return
     */
    @PutMapping(value = "/provider/{providerKey}/drafts/{draftKey}")
    BaseResponse reviewDraft(
        @PathVariable UUID providerKey,
        @PathVariable UUID draftKey,
        @RequestBody AssetDraftReviewCommandDto command
    );

    /**
     * Download custom contract file
     *
     * @param draftKey
     *            Draft unique key
     *
     * @return The requested file
     */
    @GetMapping(value = "/provider/{providerKey}/drafts/{draftKey}/contract", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<StreamingResponseBody> getContract(@PathVariable UUID draftKey, HttpServletResponse response) throws IOException;

    /**
     * Download an contract annex file
     *
     * @param draftKey Draft unique key
     * @param resourceKey Annex resource unique key
     *
     * @return The requested file
     */
    @GetMapping(value = "/provider/{providerKey}/drafts/{draftKey}/contract/annexes/{annexKey}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    ResponseEntity<StreamingResponseBody> getContractAnnex(
        @PathVariable UUID draftKey, @PathVariable String annexKey, HttpServletResponse response
    ) throws IOException;

    /**
     * Delete catalogue draft item
     *
     * @param providerKey The provider unique key
     * @param draftKey The draft unique key
     * @return
     */
    @DeleteMapping(value = "/provider/{providerKey}/drafts/{draftKey}")
    BaseResponse deleteDraft(@PathVariable UUID providerKey, @PathVariable UUID draftKey);

}