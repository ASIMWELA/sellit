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
@Table(name = "provider_ratings")
public class ProviderRating extends BaseEntity {
    @Column(name = "avg_punctuality_rating", length = 10)
    double avgPunctualityRating;
    @Column(name = "avg_proficiency_rating",length = 10)
    double avgProficiencyRating;
    @Column(name = "avg_professionalism_rating", length = 10)
    double avgProfessionalismRating;
    @Column(name = "avg_communication_rating", length = 10)
    double avgCommunicationRating;
    @Column(name = "avg_price_rating", length = 10)
    double avgPriceRating;
    @Column(name = "overall_rating", length = 10)
    double overallRating;
    @Column(name = "updated_on", length = 10)
    Date updatedOn;
    @OneToOne
    @JoinColumn(name="provider_id", referencedColumnName = "id", unique = true)
    Provider provider;
}
