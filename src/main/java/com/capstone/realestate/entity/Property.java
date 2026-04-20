package com.capstone.realestate.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int propId;

    private String configuration;
    private String offerType;
    private double offerCost;
    private double areaSqft;
    private String address;
    private String street;
    private String city;

    @Column(nullable = false)
    private boolean status = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "property_images", joinColumns = @JoinColumn(name = "prop_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "broker_id")
    @JsonBackReference
    private Broker broker;

    @JsonProperty("brokerId")
    public Integer getBrokerId() {
        return broker != null ? broker.getBroId() : null;
    }
}
