package com.bnm.personservice.repository.entity;

import jakarta.persistence.*;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.time.LocalDateTime;

@Entity
@RevisionEntity
@Table(name = "revinfo", schema = "person")
public class RevInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @RevisionNumber
    @Column(name = "rev")
    private long rev;

    @RevisionTimestamp
    @Column(name = "revtstmp")
    private LocalDateTime revtstmp;

    public long getRev() {
        return rev;
    }

    public void setRev(final long rev) {
        this.rev = rev;
    }

    public LocalDateTime getRevtstmp() {
        return revtstmp;
    }

    public void setRevtstmp(final LocalDateTime revtstmp) {
        this.revtstmp = revtstmp;
    }
} 