package org.himcharm.services;

import org.himcharm.dtos.CreateManualCampaignRequest;
import org.himcharm.entities.ManualCampaign;
import org.himcharm.enums.WhatsAppMessageType;
import org.himcharm.repositories.CustomerRepository;
import org.himcharm.repositories.ManualCampaignRepository;
import org.himcharm.whatsapp.WhatsAppService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;

@Service
public class ManualCampaignService {

    private final CustomerRepository customerRepository;
    private final ManualCampaignRepository campaignRepository;
    private final CampaignBatchExecutor batchExecutor;
    private final CampaignMessageSender messageSender;
    private final WhatsAppService whatsAppService;
    private final Clock applicationClock;

    public ManualCampaignService(
            CustomerRepository customerRepository,
            ManualCampaignRepository campaignRepository,
            CampaignBatchExecutor batchExecutor,
            CampaignMessageSender messageSender,
            WhatsAppService whatsAppService,
            Clock applicationClock
    ) {
        this.customerRepository = customerRepository;
        this.campaignRepository = campaignRepository;
        this.batchExecutor = batchExecutor;
        this.messageSender = messageSender;
        this.whatsAppService = whatsAppService;
        this.applicationClock = applicationClock;
    }

    public Long start(CreateManualCampaignRequest request) {
        ManualCampaign campaign = campaignRepository.save(ManualCampaign.builder()
                .type(WhatsAppMessageType.MANUAL_CAMAPIGN)
                .startDate(LocalDate.now(applicationClock))
                .endDate(request.validUpTo())
                .build());

        String festivalName = request.festivalName().trim();
        String offerPercentage = formatPercentage(request.offerPercentage());
        batchExecutor.submit(customerRepository.findAll(), customer -> messageSender.send(
                customer,
                WhatsAppMessageType.MANUAL_CAMAPIGN,
                whatsAppService.getFestivalTemplateName(),
                campaign,
                phoneNumber -> whatsAppService.sendManualCampaignMessage(
                        phoneNumber,
                        customer.getName(),
                        festivalName,
                        offerPercentage,
                        request.validUpTo()
                )
        ));

        return campaign.getId();
    }

    private String formatPercentage(BigDecimal percentage) {
        return percentage.stripTrailingZeros().toPlainString() + "%";
    }
}
