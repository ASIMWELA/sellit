package com.sellit.api.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class NewProviderReviewEvent extends ApplicationEvent {
    private String providerUuid;
    public NewProviderReviewEvent(Object source, String providerUuid) {
        super(source);
        this.providerUuid = providerUuid;
    }
    public NewProviderReviewEvent(String providerUuid) {
        super(providerUuid);
        this.providerUuid = providerUuid;
    }
}
