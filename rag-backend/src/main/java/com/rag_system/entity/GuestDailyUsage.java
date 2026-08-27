package com.rag_system.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "\"GuestDailyUsage\"")
public class GuestDailyUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "\"guestId\"")
    private String guestId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "count")
    private Integer count;
}
