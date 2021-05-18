package com.sellit.api.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NewProviderReviewEventListener implements ApplicationListener<NewProviderReviewEvent> {
    @Override
    public void onApplicationEvent(NewProviderReviewEvent newProviderReviewEvent) {
        log.info("Updating The overall rating for provider : {}", newProviderReviewEvent.getProviderUuid());
        //TODO:Complete this by updating the overall provider rating
    }
}
