package org.himcharm.repositories;

import org.himcharm.entities.WhatsAppMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WhatsAppMessageRepository extends JpaRepository<WhatsAppMessage, Long>,
        JpaSpecificationExecutor<WhatsAppMessage> {

    Optional<WhatsAppMessage> findByWhatsAppMessageId(String whatsAppMessageId);

    @Override
    @EntityGraph(attributePaths = {"customer", "manualCampaign"})
    Page<WhatsAppMessage> findAll(
            Specification<WhatsAppMessage> specification,
            Pageable pageable
    );
}
