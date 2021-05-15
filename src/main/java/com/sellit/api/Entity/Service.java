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
@Table(name = "services")
public class Service extends BaseEntity {
    @Column(name="service_name", unique = true)
    String serviceName;
    @ManyToOne(targetEntity = ServiceCategory.class)
    @JoinColumn(name="service_category_id")
    private ServiceCategory serviceCategory;
    @OneToMany(mappedBy = "service")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<ServiceProvider> serviceProviders;
    @OneToMany(mappedBy = "service")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<ServiceRequest> serviceRequests;
}
