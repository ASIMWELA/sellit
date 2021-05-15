package com.sellit.api.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sellit.api.Enum.EServiceCategory;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "service_categories")
public class ServiceCategory extends BaseEntity {
    @Column(name="service_category_name", unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    @NaturalId
    EServiceCategory serviceCategoryName;
    @OneToMany(mappedBy = "serviceCategory")
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<Service> services = new ArrayList<>();
}
