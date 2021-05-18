package com.sellit.api.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "service_appointments")
public class ServiceAppointment extends BaseEntity{
    @Column(name="service_delivered_on", nullable = false)
    Date serviceDeliveredOn;
    @Column(name="service_start_time", nullable = false)
    Date serviceStartTime;
    @Column(name="service_end_time", nullable = false)
    Date serviceEndTime;
    @OneToOne
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JoinColumn(name="service_delivery_offer_id", referencedColumnName = "id", unique = true)
    ServiceDeliveryOffer serviceDeliveryOffer;
    @OneToMany(mappedBy = "serviceAppointment")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<ProviderReviewLog> providerReviewLogs = new ArrayList<>();
}
