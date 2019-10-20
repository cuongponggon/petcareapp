package com.project.petcareapp.repository;

import com.project.petcareapp.model.Appointment;
import com.project.petcareapp.model.AppointmentSubcriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {
        Appointment findByName(String name);
        Appointment findByToken(String token);
        Appointment findAppointmentById(int id);

        @Query("SELECT ap FROM AppointmentSubcriber ap WHERE ap.appointmentGroupContact.appointment.id = :appointmentId AND ap.subcriberEmail= :subcriberEmail")
        AppointmentSubcriber findMailByAppointmentId(@Param("appointmentId") int appointmentId, @Param("subcriberEmail") String subcriberEmail);


        List<Appointment>findAppointmentByAccount_idAndAutomationIsFalseOrderByCreatedTimeDesc(int accountId);

        Appointment findAppointmentByName(String name);

        List<Appointment> findAppointmentByAccount_idOrderByCreatedTimeDesc(int accountId);



}
