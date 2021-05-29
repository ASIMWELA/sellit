package com.sellit.api.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "services")
public class Service extends BaseEntity {
    @Column(name="service_name", unique = true, nullable = false)
    @NotEmpty(message = "serviceName cannot be empty")
    @Size(min=2, message = "serviceName should have at least 2 characters")
    String serviceName;
    @ManyToOne(targetEntity = ServiceCategory.class, cascade = CascadeType.ALL)
    @JoinColumn(name="service_category_id", nullable = false)
    ServiceCategory serviceCategory;
    @OneToMany(mappedBy = "service")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnore
    List<ServiceProvider> serviceProviders;
    @OneToMany(mappedBy = "service")
    @LazyCollection(LazyCollectionOption.TRUE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<ServiceRequest> serviceRequests;
}
