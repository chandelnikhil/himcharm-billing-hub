package org.himcharm.services;

import org.himcharm.dtos.CreateManualCampaignRequest;
import org.himcharm.entities.Customer;
import org.himcharm.entities.ManualCampaign;
import org.himcharm.enums.WhatsAppMessageType;
import org.himcharm.enums.CampaignImageType;
import org.himcharm.repositories.CustomerRepository;
import org.himcharm.repositories.ManualCampaignRepository;
import org.himcharm.whatsapp.WhatsAppService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

@Service
public class ManualCampaignService {

    private final CustomerRepository customerRepository;
    private final ManualCampaignRepository campaignRepository;
    private final CampaignBatchExecutor batchExecutor;
    private final CampaignMessageSender messageSender;
    private final WhatsAppService whatsAppService;
    private final CampaignImageService campaignImageService;
    private final StoreService storeService;
    private final Clock applicationClock;

    public ManualCampaignService(
            CustomerRepository customerRepository,
            ManualCampaignRepository campaignRepository,
            CampaignBatchExecutor batchExecutor,
            CampaignMessageSender messageSender,
            WhatsAppService whatsAppService,
            CampaignImageService campaignImageService,
            StoreService storeService,
            Clock applicationClock
    ) {
        this.customerRepository = customerRepository;
        this.campaignRepository = campaignRepository;
        this.batchExecutor = batchExecutor;
        this.messageSender = messageSender;
        this.whatsAppService = whatsAppService;
        this.campaignImageService = campaignImageService;
        this.storeService = storeService;
        this.applicationClock = applicationClock;
    }

    public Long start(CreateManualCampaignRequest request) {
        List<Customer> customers;
        if (request.storeId() == null) {
            customers = customerRepository.findAll();
        } else {
            storeService.getStoreById(request.storeId());
            customers = customerRepository.findDistinctByStores_Id(request.storeId());
        }

        ManualCampaign campaign = campaignRepository.save(ManualCampaign.builder()
                .type(WhatsAppMessageType.MANUAL_CAMAPIGN)
                .startDate(LocalDate.now(applicationClock))
                .endDate(request.validUpTo())
                .build());

        String festivalName = request.festivalName().trim();
        String offerPercentage = formatPercentage(request.offerPercentage());
        batchExecutor.submit(customers, customer -> messageSender.send(
                customer,
                WhatsAppMessageType.MANUAL_CAMAPIGN,
                whatsAppService.getFestivalTemplateName(),
                campaign,
                phoneNumber -> whatsAppService.sendManualCampaignMessage(
                        phoneNumber,
                        festivalName,
                        customer.getName(),
                        offerPercentage,
                        request.validUpTo(),
                        campaignImageService.getCampaignImageUrl(CampaignImageType.FESTIVAL)
                )
        ));

        return campaign.getId();
    }

    private String formatPercentage(BigDecimal percentage) {
        return percentage.stripTrailingZeros().toPlainString() + "%";
    }
}
