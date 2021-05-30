package com.sellit.api.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class AppointmentEvent extends ApplicationEvent {
    String appointmentUuid;
    public AppointmentEvent(Object source) {
        super(source);
    }
    public AppointmentEvent(String appointmentUuid) {
        super(appointmentUuid);
        this.appointmentUuid = appointmentUuid;
    }
}
