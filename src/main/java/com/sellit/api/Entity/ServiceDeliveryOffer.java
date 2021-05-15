package com.sellit.api.Entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "service_delivery_offers")
public class ServiceDeliveryOffer extends BaseEntity {

    @Column(name="discount_in_percent", length = 50)
    double discountInPercent;
    @Column(name="estimated_cost", length = 50)
    double estimatedCost;
    @Column(name="offer_submission_date")
    Date offerSubmissionDate;
    @Column(name="is_offer_accepted", length = 5)
    boolean isOfferAccepted;
    @OneToOne(mappedBy = "serviceDeliveryOffer")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    ServiceAppointment serviceAppointments ;
    @ManyToOne
    @JoinColumn(name = "service_request_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    ServiceRequest serviceRequest;
    @ManyToOne
    @JoinColumn(name = "service_provider_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    ServiceProvider serviceProvider;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceDeliveryOffer that = (ServiceDeliveryOffer) o;
        return Double.compare(that.discountInPercent,
                discountInPercent) == 0 && Double.compare(that.estimatedCost, estimatedCost) == 0
                && isOfferAccepted == that.isOfferAccepted
                && offerSubmissionDate.equals(that.offerSubmissionDate)
                && serviceAppointments.equals(that.serviceAppointments)
                && serviceRequest.equals(that.serviceRequest)
                && serviceProvider.equals(that.serviceProvider);
    }

    @Override
    public int hashCode() {
        return Objects.hash(discountInPercent, estimatedCost,
                offerSubmissionDate, isOfferAccepted,
                serviceAppointments, serviceRequest,
                serviceProvider);
    }
}
