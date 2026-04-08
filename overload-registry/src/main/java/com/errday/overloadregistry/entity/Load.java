package com.errday.overloadregistry.entity;

import com.errday.overloadregistry.enums.LoadStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String loadName;

    @Enumerated(EnumType.STRING)
    private LoadStatus status;

    @CreatedDate
    private LocalDateTime createAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AttacheFile> attacheFiles = new ArrayList<>();

    public void addAttacheFile(AttacheFile attacheFile) {
        attacheFiles.add(attacheFile);
        attacheFile.setLoad(this);
    }

    public boolean isCompleted() {
        return status == LoadStatus.COMPLETED;
    }

}
