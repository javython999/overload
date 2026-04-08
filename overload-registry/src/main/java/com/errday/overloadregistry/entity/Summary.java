package com.errday.overloadregistry.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Summary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String summaryData;

    @CreatedDate
    private LocalDateTime createAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id")
    private Load load;

    public JsonNode getSummaryDataAsJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readTree(this.summaryData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert summaryData to JsonNode", e);
        }
    }
}
