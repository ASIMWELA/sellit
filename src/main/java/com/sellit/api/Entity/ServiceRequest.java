package com.sellit.api.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "service_requests")
public class ServiceRequest extends BaseEntity {
    @Column(name="requirement_description", length = 2500, nullable = false)
    @NotEmpty(message = "requirementDescription cannot be empty")
    @Size(min=10, max = 2500, message = "requirementDescription should be between 10 and 2500 characters")
    String requirementDescription;
    @Column(name="required_on", nullable = false)
    @NonNull
    Date requiredOn;
    @Column(name="expected_start_time", nullable = false)
    @NonNull

    //TODO: change it to string datatype in the schema model
    Date expectedStartTime;
    @Column(name="expected_tentative_effort_required_in_hours", length = 100, nullable = false)
    Long expectedTentativeEffortRequiredInHours;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    User user;
    @ManyToOne
    @JoinColumn(name = "service_id")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonIgnore
    Service service;
    @OneToMany(mappedBy = "serviceRequest")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnore
    List<ServiceDeliveryOffer> serviceDeliveryOffers;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceRequest that = (ServiceRequest) o;
        return requirementDescription.equals(that.requirementDescription) && requiredOn.equals(that.requiredOn) && expectedStartTime.equals(that.expectedStartTime) && Objects.equals(expectedTentativeEffortRequiredInHours, that.expectedTentativeEffortRequiredInHours) && user.equals(that.user) && service.equals(that.service);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requirementDescription,
                requiredOn, expectedStartTime,
                expectedTentativeEffortRequiredInHours,
                user, service);
    }
}
