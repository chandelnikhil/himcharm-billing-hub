package org.himcharm.controllers;

import jakarta.validation.Valid;
import org.himcharm.dtos.ApiResponse;
import org.himcharm.dtos.CreateManualCampaignRequest;
import org.himcharm.dtos.ManualCampaignStartedResponse;
import org.himcharm.services.ManualCampaignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manual-campaigns")
public class CreateManualCampaignController {

    private final ManualCampaignService manualCampaignService;

    public CreateManualCampaignController(ManualCampaignService manualCampaignService) {
        this.manualCampaignService = manualCampaignService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> startCampaign(
            @Valid @RequestBody CreateManualCampaignRequest request
    ) {
        Long campaignId = manualCampaignService.start(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(
                ApiResponse.success(
                        HttpStatus.ACCEPTED.value(),
                        "Manual campaign started",
                        new ManualCampaignStartedResponse(campaignId)
                )
        );
    }
}
