package org.himcharm.repositories;

import org.himcharm.entities.ManualCampaign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManualCampaignRepository extends JpaRepository<ManualCampaign, Long> {
}
