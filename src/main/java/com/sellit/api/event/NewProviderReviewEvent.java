package com.sellit.api.event;

import lombok.*;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class NewProviderReviewEvent extends ApplicationEvent {
    private String proverReviewLogUuid;
    public NewProviderReviewEvent(Object source, String proverReviewLogUuid) {
        super(source);
        this.proverReviewLogUuid = proverReviewLogUuid;
    }
    public NewProviderReviewEvent(String proverReviewLogUuid) {
        super(proverReviewLogUuid);
        this.proverReviewLogUuid = proverReviewLogUuid;
    }
}
