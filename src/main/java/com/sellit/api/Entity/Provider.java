package com.sellit.api.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "providers")
@Builder
public class Provider extends BaseEntity{
    @Column(name="is_individual", nullable = false, length = 10)
    boolean isIndividual;
    @Column(name="is_registered_office", nullable = false, length = 10)
    boolean isRegisteredOffice;
    @Column(name="office_address", length = 500)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String officeAddress;
    @Column(name="provider_description", length = 800)
    String providerDescription;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="user_id", referencedColumnName = "id", unique = true)
    User user;
    @OneToOne(mappedBy = "provider")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ProviderRating providerRating;
    @OneToMany(mappedBy = "provider", cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @LazyCollection(LazyCollectionOption.TRUE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<ServiceProvider> services;
}
