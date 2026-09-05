package org.himcharm.schedulers;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.himcharm.entities.Customer;
import org.himcharm.entities.WhatsAppMessage;
import org.himcharm.enums.WhatsAppMessageStatus;
import org.himcharm.enums.WhatsAppMessageType;
import org.himcharm.enums.WhatsAppTemplateCategory;
import org.himcharm.repositories.CustomerRepository;
import org.himcharm.repositories.WhatsAppMessageRepository;
import org.himcharm.whatsapp.WhatsAppClientException;
import org.himcharm.whatsapp.WhatsAppService;
import org.himcharm.whatsapp.dto.WhatsAppMessageResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

@Slf4j
@Component
public class BirthdayAnniversaryCampaigns {

    private static final int BATCH_SIZE = 50;

    private final CustomerRepository customerRepository;
    private final WhatsAppMessageRepository whatsAppMessageRepository;
    private final WhatsAppService whatsAppService;
    private final Clock applicationClock;
    private final ThreadPoolTaskExecutor automatedCampaignTaskExecutor;
    private final String birthdayOff;
    private final String anniversaryOff;

    public BirthdayAnniversaryCampaigns(
            CustomerRepository customerRepository,
            WhatsAppMessageRepository whatsAppMessageRepository,
            WhatsAppService whatsAppService,
            Clock applicationClock,
            @Value("${campaign.off.birthday}") String birthdayOff,
            @Value("${campaign.off.anniversay}") String anniversaryOff,
            @Qualifier("automatedCampaignTaskExecutor") ThreadPoolTaskExecutor automatedCampaignTaskExecutor
    ) {
        this.customerRepository = customerRepository;
        this.whatsAppMessageRepository = whatsAppMessageRepository;
        this.whatsAppService = whatsAppService;
        this.applicationClock = applicationClock;
        this.automatedCampaignTaskExecutor = automatedCampaignTaskExecutor;
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
        List<Future<?>> batchResults = submitBatches(customers, eventDate.plusDays(1), campaign);
        waitForAllBatches(batchResults, campaignName);
        log.info("Finished processing all {} campaign batches for {}", campaignName, eventDate);
    }

    private List<Future<?>> submitBatches(
            List<Customer> customers,
            LocalDate offerValidUntil,
            Campaign campaign
    ) {
        List<Future<?>> batchResults = new ArrayList<>();

        for (int start = 0; start < customers.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, customers.size());
            List<Customer> batch = List.copyOf(customers.subList(start, end));
            batchResults.add(automatedCampaignTaskExecutor.submit(
                    () -> processBatch(batch, offerValidUntil, campaign)
            ));
        }

        return batchResults;
    }

    private void processBatch(
            List<Customer> customers,
            LocalDate offerValidUntil,
            Campaign campaign
    ) {
        log.info("Processing {} campaign batch containing {} customer(s)",
                campaign.name(), customers.size());
        customers.forEach(customer -> sendCampaignMessage(customer, offerValidUntil, campaign));
    }

    private void sendCampaignMessage(
            Customer customer,
            LocalDate offerValidUntil,
            Campaign campaign
    ) {
        WhatsAppMessage message = WhatsAppMessage.builder()
                .customer(customer)
                .messageType(campaign.messageType())
                .phoneNumber(customer.getPhone())
                .templateName(campaign.templateName())
                .templateCategory(WhatsAppTemplateCategory.MARKETING)
                .templateLanguage(whatsAppService.getInvoiceTemplateLanguage())
                .build();

        try {
            String normalizedPhoneNumber = whatsAppService.normalizePhoneNumber(customer.getPhone());
            message.setPhoneNumber(normalizedPhoneNumber);
            WhatsAppMessageResponse response = whatsAppService.sendAutomatedCampaignMessage(
                    normalizedPhoneNumber,
                    customer.getName(),
                    campaign.name().equalsIgnoreCase("birthday") ? birthdayOff : anniversaryOff,
                    offerValidUntil,
                    campaign.templateName(),
                    campaign.imageUrl()
            );

            message.setWhatsAppMessageId(firstMessageId(response));
            message.setStatus(WhatsAppMessageStatus.SENT);
            message.setSentAt(LocalDateTime.now(applicationClock));
            log.info("{} campaign message sent to customer: {}", campaign.name(), customer.getName());
        } catch (WhatsAppClientException exception) {
            markMessageFailed(message, exception.getErrorCode(), exception.getMessage());
            log.warn("{} campaign message failed for customer {}: {}",
                    campaign.name(), customer.getName(), exception.getMessage());
        } catch (RuntimeException exception) {
            markMessageFailed(message, exception.getClass().getSimpleName(), exception.getMessage());
            log.warn("Unable to send {} campaign message to customer {}: {}",
                    campaign.name(), customer.getName(), exception.getMessage());
        }

        whatsAppMessageRepository.save(message);
    }

    private String firstMessageId(WhatsAppMessageResponse response) {
        if (response.messages() == null || response.messages().isEmpty()) {
            return null;
        }
        return response.messages().getFirst().id();
    }

    private void markMessageFailed(WhatsAppMessage message, String errorCode, String errorMessage) {
        message.setStatus(WhatsAppMessageStatus.FAILED);
        message.setErrorCode(errorCode == null ? "UNKNOWN" : errorCode);
        message.setErrorMessage(errorMessage == null ? "Unknown WhatsApp error" : errorMessage);
        message.setFailedAt(LocalDateTime.now(applicationClock));
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
