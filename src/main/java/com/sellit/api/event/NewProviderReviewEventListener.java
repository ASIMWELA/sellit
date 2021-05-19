package com.sellit.api.event;

import com.sellit.api.Entity.Provider;
import com.sellit.api.Entity.ProviderRating;
import com.sellit.api.Entity.ProviderReviewLog;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.repository.ProviderRatingRepository;
import com.sellit.api.repository.ProviderReviewLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@Slf4j
public class NewProviderReviewEventListener implements ApplicationListener<NewProviderReviewEvent> {
    final private ProviderReviewLogRepository providerReviewLogRepository;
    final private ProviderRatingRepository providerRatingRepository;

    public NewProviderReviewEventListener(ProviderReviewLogRepository providerReviewLogRepository, ProviderRatingRepository providerRatingRepository) {
        this.providerReviewLogRepository = providerReviewLogRepository;
        this.providerRatingRepository = providerRatingRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(NewProviderReviewEvent newProviderReviewEvent) {
        log.info("Updating The overall rating for service : {}", newProviderReviewEvent.getProverReviewLogUuid());

        ProviderReviewLog providerReviewLog = providerReviewLogRepository.findByUuid(newProviderReviewEvent.getProverReviewLogUuid()).orElseThrow(
                ()->new EntityNotFoundException("No review log found with the provided identifier")
        );

        Provider provider = providerReviewLog.getServiceAppointment().getServiceDeliveryOffer().getServiceProvider().getProvider();

        ProviderRating providerRating = provider.getProviderRating();

        ProviderRating overallProviderRating = new ProviderRating();

        overallProviderRating.setAvgPriceRating((providerReviewLog.getAvgPriceRating()+providerRating.getAvgPriceRating())/2.0);
        overallProviderRating.setAvgCommunicationRating((providerReviewLog.getAvgCommunicationRating()+providerRating.getAvgCommunicationRating())/2.0);
        overallProviderRating.setAvgProfessionalismRating((providerReviewLog.getAvgProfessionalismRating()+providerRating.getAvgProficiencyRating())/2.0);
        overallProviderRating.setAvgPunctualityRating((providerReviewLog.getAvgPunctualityRating()+providerRating.getAvgPunctualityRating())/2.0);
        overallProviderRating.setAvgProficiencyRating((providerReviewLog.getAvgProficiencyRating()+providerRating.getAvgProficiencyRating())/2.0);

        // Get the average rating
       double overallScore = ((providerReviewLog.getAvgPriceRating()+providerRating.getAvgPriceRating())+(providerReviewLog.getAvgCommunicationRating()+providerRating.getAvgCommunicationRating())
               +(providerReviewLog.getAvgProfessionalismRating()+providerRating.getAvgProficiencyRating())
               +(providerReviewLog.getAvgPunctualityRating()+providerRating.getAvgPunctualityRating())
               +(providerReviewLog.getAvgProficiencyRating()+providerRating.getAvgProficiencyRating()))/5.0;
        overallProviderRating.setOverallRating(overallScore);
        overallProviderRating.setUpdatedOn(new Date());
        provider.setProviderRating(overallProviderRating);
        providerRatingRepository.save(overallProviderRating);
    }
}
