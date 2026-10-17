package com.example.bag.repository;

import com.example.bag.model.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, String> {

    @Override
    @Query("SELECT t FROM Trip t ORDER BY t.createdAt DESC NULLS LAST")
    List<Trip> findAll();
}