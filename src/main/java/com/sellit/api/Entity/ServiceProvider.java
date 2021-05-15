package com.sellit.api.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "service_providers")
public class ServiceProvider extends BaseEntity {
    @Column(name="billing_rate_per_Hour", length = 100)
    double billingRatePerHour;
    @Column(name="experience_in_months")
    int experienceInMonths;
    @Lob
    @Column(name="service_offering_description")
    String serviceOfferingDescription;
    @ManyToOne
    @JoinColumn(name = "service_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    Service service;
    @ManyToOne
    @JoinColumn(name = "provider_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    Provider provider;

    @OneToMany(mappedBy = "serviceProvider")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<ServiceDeliveryOffer> serviceDeliveryOffers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceProvider that = (ServiceProvider) o;
        return Double.compare(that.billingRatePerHour, billingRatePerHour) == 0 && experienceInMonths == that.experienceInMonths && serviceOfferingDescription.equals(that.serviceOfferingDescription) && service.equals(that.service) && provider.equals(that.provider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billingRatePerHour,
                experienceInMonths,
                serviceOfferingDescription,
                service, provider);
    }
}
