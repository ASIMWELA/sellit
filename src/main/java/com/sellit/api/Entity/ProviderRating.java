package com.sellit.api.Entity;


import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.Size;
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
    @Size(max = 5, message = "avgPunctualityRating cannot only be greater than 5")
    double avgPunctualityRating;
    @Column(name = "avg_proficiency_rating",length = 10)
    @Size(max = 5, message = "avgProficiencyRating cannot be greater than 5")
    double avgProficiencyRating;
    @Column(name = "avg_professionalism_rating", length = 10)
    @Size(max = 5, message = "avgProfessionalismRating cannot be greater than 5")
    double avgProfessionalismRating;
    @Column(name = "avg_communication_rating", length = 10)
    @Size( max = 5,  message = "avgCommunicationRating cannot be greater than 5")
    double avgCommunicationRating;
    @Column(name = "avg_price_rating", length = 10)
    @Size(max = 5,  message = "avgPriceRating cannot be greater than 5")
    double avgPriceRating;
    @Column(name = "overall_rating", length = 10)
    @Size(max = 5,  message = "overallRating cannot be greater than 5")
    double overallRating;
    @Column(name = "updated_on", length = 10)
    Date updatedOn;
    @OneToOne
    @JoinColumn(name="provider_id", referencedColumnName = "id", unique = true, nullable = false)
    Provider provider;
}
