package com.sellit.api.Entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "provider_review_logs")
public class ProviderReviewLog  extends BaseEntity {
    @Column(name = "punctuality_rating", length = 10)
    double avgPunctualityRating;
    @Column(name = "proficiency_rating",length = 10)
    double avgProficiencyRating;
    @Column(name = "professionalism_rating", length = 10)
    double avgProfessionalismRating;
    @Column(name = "communication_rating", length = 10)
    double avgCommunicationRating;
    @Column(name = "price_rating", length = 10)
    double avgPriceRating;
    @Lob
    @Column(name="review")
    String review;
    @Column(name = "overall_rating", length = 10)
    double overallRating;
    @Column(name="review_date")
    Date reviewDate;
    @ManyToOne(targetEntity = ServiceAppointment.class)
    @JoinColumn(name="service_appointment_id")
    ServiceAppointment serviceAppointment;


}
