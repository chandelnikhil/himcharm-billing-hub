package org.himcharm.repositories;

import org.himcharm.entities.WhatsAppMessage;
import org.himcharm.enums.WhatsAppMessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface WhatsAppMessageRepository extends JpaRepository<WhatsAppMessage, Long> {

    @EntityGraph(attributePaths = "customer")
    @Query("""
            SELECT message
            FROM WhatsAppMessage message
            WHERE message.messageType IN :automatedTypes
              AND (:campaignType IS NULL OR message.messageType = :campaignType)
              AND (:fromDateTime IS NULL OR message.createdAt >= :fromDateTime)
              AND (:toDateTime IS NULL OR message.createdAt < :toDateTime)
            """)
    Page<WhatsAppMessage> findAutomatedCampaignMessages(
            @Param("automatedTypes") Collection<WhatsAppMessageType> automatedTypes,
            @Param("campaignType") WhatsAppMessageType campaignType,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"customer", "manualCampaign"})
    @Query("""
            SELECT message
            FROM WhatsAppMessage message
            WHERE message.messageType = :messageType
              AND (:campaignId IS NULL OR message.manualCampaign.id = :campaignId)
              AND (:fromDateTime IS NULL OR message.createdAt >= :fromDateTime)
              AND (:toDateTime IS NULL OR message.createdAt < :toDateTime)
            """)
    Page<WhatsAppMessage> findManualCampaignMessages(
            @Param("messageType") WhatsAppMessageType messageType,
            @Param("campaignId") Long campaignId,
            @Param("fromDateTime") LocalDateTime fromDateTime,
            @Param("toDateTime") LocalDateTime toDateTime,
            Pageable pageable
    );
}
