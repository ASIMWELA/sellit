package com.sellit.api.Entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Entity
@Table(name = "service_categories")
@Builder
public class ServiceCategory extends BaseEntity {
    @Column(name="service_category_name", unique = true, length = 50, nullable = false)
    @NonNull
    String serviceCategoryName;
    @OneToMany(mappedBy = "serviceCategory", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<Service> services = new ArrayList<>();
}
