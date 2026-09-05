package org.himcharm.services;

import org.himcharm.entities.Customer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Service
public class CampaignBatchExecutor {

    private static final int BATCH_SIZE = 50;

    private final ThreadPoolTaskExecutor taskExecutor;

    public CampaignBatchExecutor(
            @Qualifier("automatedCampaignTaskExecutor") ThreadPoolTaskExecutor taskExecutor
    ) {
        this.taskExecutor = taskExecutor;
    }

    public List<Future<?>> submit(List<Customer> customers, Consumer<Customer> messageSender) {
        List<Future<?>> results = new ArrayList<>();

        for (int start = 0; start < customers.size(); start += BATCH_SIZE) {
            int end = Math.min(start + BATCH_SIZE, customers.size());
            List<Customer> batch = List.copyOf(customers.subList(start, end));
            results.add(taskExecutor.submit(() -> batch.forEach(messageSender)));
        }

        return results;
    }
}
