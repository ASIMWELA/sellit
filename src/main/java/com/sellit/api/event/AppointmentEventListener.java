package com.sellit.api.event;

import com.sellit.api.Entity.ServiceAppointment;
import com.sellit.api.Entity.ServiceRequest;
import com.sellit.api.Entity.User;
import com.sellit.api.Entity.UserAddress;
import com.sellit.api.exception.EntityNotFoundException;
import com.sellit.api.repository.ServiceAppointmentRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class AppointmentEventListener implements ApplicationListener<AppointmentEvent> {

    //renamed

    final JavaMailSender javaMailSender;
    ServiceAppointmentRepository serviceAppointmentRepository;

    public AppointmentEventListener( JavaMailSender javaMailSender, ServiceAppointmentRepository serviceAppointmentRepository) {
        this.javaMailSender = javaMailSender;
        this.serviceAppointmentRepository = serviceAppointmentRepository;
    }

    @Override
    @Transactional
    public void onApplicationEvent(AppointmentEvent appointmentEvent) {
        ServiceAppointment appointment = serviceAppointmentRepository.findByUuid(appointmentEvent.getAppointmentUuid()).orElseThrow(
                ()->new EntityNotFoundException("No appointment found with the given identifier")
        );

        ServiceRequest request = appointment.getServiceDeliveryOffer().getServiceRequest();
        String providerEmail = appointment.getServiceDeliveryOffer().getServiceProvider().getProvider().getUser().getEmail();
        User user = appointment.getServiceDeliveryOffer().getServiceRequest().getUser();
        UserAddress address = user.getAddress();
        String message = "Your request got approved. You have an appointment \nwith: "+
               user.getFirstName() + "  " + user.getLastName() + " on "+ "\n"+
               appointment.getServiceStartTime()+" \n\nCUSTOMER CONTACT DETAILS\n"+
               "Email : "+ user.getEmail() +"\nMobile number : "+ user.getMobileNumber()+"\n\n"+
               "ADDRESS\nCity : " + address.getCity()+" \nStreet : " + address.getStreet()+"\n"+
               "Region : "+ address.getRegion()+"\n"+"General Location desc : "+address.getLocationDescription();
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        String appName = "sell-services";
        simpleMailMessage.setFrom(appName);
        simpleMailMessage.setTo(providerEmail);
        simpleMailMessage.setSubject("Appointment Details of : \n"+ request.getRequirementDescription()+" Request");
        simpleMailMessage.setText(message);
        log.info("Sending email to "+providerEmail);
        javaMailSender.send(simpleMailMessage);
    }
}
