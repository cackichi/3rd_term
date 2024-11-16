package com.example.hotelswebapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"services", "reservations", "reviewOfRooms"})
@Table(name = "rooms")
public class HotelRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ElementCollection
    @CollectionTable(name = "room_photos", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "photo_url")
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    @BatchSize(size = 10)
    private List<String> photos;

    private int amountOfSleepers;
    private String description;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(
            name = "room_services",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "service_id")
    )
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 10)
    private Set<Service> services = new HashSet<>();

    private String name;
    private int pricePerDay;

    @OneToMany(mappedBy = "hotelRoomEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 10)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "hotelRoomEntity", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @BatchSize(size = 10)
    private List<ReviewOfRoom> reviewOfRooms = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HotelRoomEntity that = (HotelRoomEntity) o;
        return id == that.id && amountOfSleepers == that.amountOfSleepers && pricePerDay == that.pricePerDay && Objects.equals(description, that.description) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amountOfSleepers, description, name, pricePerDay);
    }
}
