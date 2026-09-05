package org.himcharm.schedulers;

import lombok.extern.slf4j.Slf4j;
import org.himcharm.entities.Customer;
import org.himcharm.enums.WhatsAppMessageType;
import org.himcharm.repositories.CustomerRepository;
import org.himcharm.services.CampaignBatchExecutor;
import org.himcharm.services.CampaignMessageSender;
import org.himcharm.whatsapp.WhatsAppService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

@Slf4j
@Component
public class BirthdayAnniversaryCampaigns {

    private final CustomerRepository customerRepository;
    private final WhatsAppService whatsAppService;
    private final CampaignBatchExecutor batchExecutor;
    private final CampaignMessageSender messageSender;
    private final Clock applicationClock;
    private final String birthdayOff;
    private final String anniversaryOff;

    public BirthdayAnniversaryCampaigns(
            CustomerRepository customerRepository,
            WhatsAppService whatsAppService,
            CampaignBatchExecutor batchExecutor,
            CampaignMessageSender messageSender,
            Clock applicationClock,
            @Value("${campaign.off.birthday}") String birthdayOff,
            @Value("${campaign.off.anniversay}") String anniversaryOff
    ) {
        this.customerRepository = customerRepository;
        this.whatsAppService = whatsAppService;
        this.batchExecutor = batchExecutor;
        this.messageSender = messageSender;
        this.applicationClock = applicationClock;
        this.birthdayOff = birthdayOff;
        this.anniversaryOff = anniversaryOff;
    }

    @Scheduled(
            cron = "${scheduler.automated-campaign.cron}",
            zone = "${scheduler.time-zone:Asia/Kolkata}"
    )
    public void sendBirthdayCampaign() {
        processCampaign(
                "birthday",
                WhatsAppMessageType.BIRTHDAY,
                whatsAppService.getBirthdayTemplateName(),
                whatsAppService.getBirthdayImageUrl(),
                customerRepository::findCustomersByBirthday
        );
    }

    @Scheduled(
            cron = "${scheduler.automated-campaign.cron}",
            zone = "${scheduler.time-zone:Asia/Kolkata}"
    )
    public void sendAnniversaryCampaign() {
        processCampaign(
                "anniversary",
                WhatsAppMessageType.ANNIVERSARY,
                whatsAppService.getAnniversaryTemplateName(),
                whatsAppService.getAnniversaryImageUrl(),
                customerRepository::findCustomersByAnniversary
        );
    }

    private void processCampaign(
            String campaignName,
            WhatsAppMessageType messageType,
            String templateName,
            String imageUrl,
            BiFunction<Integer, Integer, List<Customer>> customerFinder
    ) {
        LocalDate eventDate = LocalDate.now(applicationClock).plusDays(1);
        List<Customer> customers = customerFinder.apply(
                eventDate.getMonthValue(),
                eventDate.getDayOfMonth()
        );

        log.info("Found {} customer(s) for the {} campaign on {}",
                customers.size(), campaignName, eventDate);
        Campaign campaign = new Campaign(campaignName, messageType, templateName, imageUrl);
        LocalDate offerValidUntil = eventDate.plusDays(1);
        List<Future<?>> batchResults = batchExecutor.submit(customers, customer ->
                sendCampaignMessage(customer, offerValidUntil, campaign));
        waitForAllBatches(batchResults, campaignName);
        log.info("Finished processing all {} campaign batches for {}", campaignName, eventDate);
    }

    private void sendCampaignMessage(
            Customer customer,
            LocalDate offerValidUntil,
            Campaign campaign
    ) {
        messageSender.send(
                customer,
                campaign.messageType(),
                campaign.templateName(),
                null,
                phoneNumber -> whatsAppService.sendAutomatedCampaignMessage(
                    phoneNumber,
                    customer.getName(),
                    campaign.name().equalsIgnoreCase("birthday") ? birthdayOff : anniversaryOff,
                    offerValidUntil,
                    campaign.templateName(),
                    campaign.imageUrl()
                )
        );
    }

    private void waitForAllBatches(List<Future<?>> batchResults, String campaignName) {
        boolean interrupted = false;

        for (Future<?> batchResult : batchResults) {
            try {
                batchResult.get();
            } catch (InterruptedException exception) {
                interrupted = true;
                log.warn("Interrupted while waiting for a {} campaign batch", campaignName, exception);
            } catch (ExecutionException exception) {
                log.error("A {} campaign batch failed", campaignName, exception.getCause());
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }

    private record Campaign(
            String name,
            WhatsAppMessageType messageType,
            String templateName,
            String imageUrl
    ) {
    }

}
