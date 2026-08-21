package gr.aueb.cf.helpdesk.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 36)
    private String uuid;

    @CreatedDate
    @Column(name  = "created_at", nullable = false, updatable = false, columnDefinition = "DATETIME")
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME" )
    private Instant updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    @PrePersist
    protected void generateUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }
}
