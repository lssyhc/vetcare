package com.lima.vetcare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "veterinarians")
@DiscriminatorValue("VETERINARIAN")
public class Veterinarian extends User {

    @NotNull(message = "Spesialisasi wajib diisi")
    @Size(min = 2, max = 100, message = "Spesialisasi harus terdiri dari 2 sampai 100 karakter")
    @Column(nullable = false, length = 100)
    private String specialization;

    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VeterinarianSchedule> schedules = new ArrayList<>();

    @OneToMany(mappedBy = "veterinarian", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments = new ArrayList<>();

    public Veterinarian() {
        super();
    }

    public Veterinarian(String email, String password, String name, String specialization) {
        super(email, password, name);
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public List<VeterinarianSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<VeterinarianSchedule> schedules) {
        this.schedules = schedules;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    public void addSchedule(VeterinarianSchedule schedule) {
        schedules.add(schedule);
        schedule.setVeterinarian(this);
    }

    public void removeSchedule(VeterinarianSchedule schedule) {
        schedules.remove(schedule);
        schedule.setVeterinarian(null);
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        appointment.setVeterinarian(this);
    }

    public void removeAppointment(Appointment appointment) {
        appointments.remove(appointment);
        appointment.setVeterinarian(null);
    }

    @Override
    public String getUserType() {
        return "VETERINARIAN";
    }

    @Override
    public String toString() {
        return "Veterinarian{" +
                "id=" + getId() +
                ", email='" + getEmail() + '\'' +
                ", name='" + getName() + '\'' +
                ", specialization='" + specialization + '\'' +
                ", schedulesCount=" + schedules.size() +
                ", appointmentsCount=" + appointments.size() +
                '}';
    }
}