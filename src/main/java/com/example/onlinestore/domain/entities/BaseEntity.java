package com.example.onlinestore.domain.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseEntity {

    private static final String UUID_STRING_NAME = "uuid-string";
    private static final String UUID_GENERATOR_STRATEGY = "org.hibernate.id.UUIDGenerator";

    private String id;

    BaseEntity() {
    }

    @Id
    @GeneratedValue(generator = UUID_STRING_NAME)
    @GenericGenerator(name = UUID_STRING_NAME, strategy = UUID_GENERATOR_STRATEGY)
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
