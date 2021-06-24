package eu.opertusmundi.admin.web.model.account.market;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

import eu.opertusmundi.common.model.account.AccountDto;
import eu.opertusmundi.common.model.account.AccountProfileDto;
import eu.opertusmundi.common.model.account.CustomerDraftIndividualDto;
import eu.opertusmundi.common.model.account.CustomerDraftProfessionalDto;
import eu.opertusmundi.common.model.account.CustomerIndividualDto;
import eu.opertusmundi.common.model.account.CustomerProfessionalDto;
import eu.opertusmundi.common.model.account.EnumActivationStatus;
import eu.opertusmundi.common.model.account.EnumKycLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MarketplaceAccountSummaryDto {

    private EnumActivationStatus accountStatus;
    private ZonedDateTime        activatedAt;
    private boolean              consumer;
    private BigDecimal           consumerFunds;
    private EnumKycLevel         consumerKycLevel;
    private String               consumerName;
    private boolean              consumerUpdatePending;
    private String               email;
    private boolean              emailVerified;
    private byte[]               image;
    private String               imageMimeType;
    private UUID                 key;
    private String               locale;
    private boolean              provider;
    private BigDecimal           providerFunds;
    private EnumKycLevel         providerKycLevel;
    private String               providerName;
    private boolean              providerUpdatePending;
    private ZonedDateTime        registeredOn;
    private String               userName;

    public static MarketplaceAccountSummaryDto from(AccountDto a) {
        final AccountProfileDto              profile = a.getProfile();
        final AccountProfileDto.ConsumerData c       = profile.getConsumer();
        final AccountProfileDto.ProviderData p       = profile.getProvider();

        final MarketplaceAccountSummaryDto r = new MarketplaceAccountSummaryDto();

        r.setAccountStatus(a.getActivationStatus());
        r.setActivatedAt(a.getActivatedAt());
        r.setConsumer(c.isRegistered());
        if (r.isConsumer()) {
            r.setConsumerFunds(c.getCurrent().getWalletFunds());
            r.setConsumerKycLevel(c.getCurrent().getKycLevel());
            switch (c.getCurrent().getType()) {
                case INDIVIDUAL :
                    r.setConsumerName(((CustomerIndividualDto) c.getCurrent()).getFullName());
                    break;
                case PROFESSIONAL :
                    r.setConsumerName(((CustomerProfessionalDto) c.getCurrent()).getName());
                    break;
                default :
                    // No action
            }
        } else if (c.getDraft() != null) {
            switch (c.getDraft().getType()) {
                case INDIVIDUAL :
                    r.setConsumerName(((CustomerDraftIndividualDto) c.getDraft()).getFullName());
                    break;
                case PROFESSIONAL :
                    r.setConsumerName(((CustomerDraftProfessionalDto) c.getDraft()).getName());
                    break;
                default :
                    // No action
            }
        }
        r.setConsumerUpdatePending(c.getDraft() != null);
        r.setEmail(a.getEmail());
        r.setEmailVerified(a.isEmailVerified());
        r.setImage(profile.getImage());
        r.setImageMimeType(profile.getImageMimeType());
        r.setKey(a.getKey());
        r.setLocale(profile.getLocale());
        r.setProvider(p.isRegistered());
        if (r.isProvider()) {
            r.setProviderFunds(p.getCurrent().getWalletFunds());
            r.setProviderKycLevel(p.getCurrent().getKycLevel());
            r.setProviderName(p.getCurrent().getName());
        } else if (p.getDraft() != null) {
            r.setProviderName(p.getDraft().getName());
        }
        r.setProviderUpdatePending(p.getDraft() != null);
        r.setRegisteredOn(a.getRegisteredAt());
        r.setUserName(a.getUsername());

        return r;
    }

}
