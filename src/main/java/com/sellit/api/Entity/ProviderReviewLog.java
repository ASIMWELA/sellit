package com.sellit.api.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "provider_review_logs")
public class ProviderReviewLog  extends BaseEntity {
    @Column(name = "punctuality_rating", length = 10, nullable = false)
    @PositiveOrZero
    double avgPunctualityRating;
    @Column(name = "proficiency_rating",length = 10, nullable = false)
    @PositiveOrZero
    double avgProficiencyRating;
    @Column(name = "professionalism_rating", length = 10, nullable = false)
    @PositiveOrZero
    double avgProfessionalismRating;
    @Column(name = "communication_rating", length = 10, nullable = false)
    @PositiveOrZero
    double avgCommunicationRating;
    @Column(name = "price_rating", length = 10, nullable = false)
    @PositiveOrZero
    double avgPriceRating;
    @Column(name="review", nullable = false, length = 1500)
    @NotEmpty(message = "review cannot be empty")
    String review;
    @Column(name = "overall_rating", length = 10, nullable = false)
    @PositiveOrZero
    double overallRating;
    @Column(name="review_date", nullable = false)
    Date reviewDate;
    @ManyToOne(targetEntity = ServiceAppointment.class)
    @JoinColumn(name="service_appointment_id")
    @JsonIgnore
    ServiceAppointment serviceAppointment;
}
