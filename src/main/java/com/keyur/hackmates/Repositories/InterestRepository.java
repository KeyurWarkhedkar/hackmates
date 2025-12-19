package com.keyur.hackmates.Repositories;

import com.keyur.hackmates.Entities.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestRepository extends JpaRepository<Interest, Integer> {
    List<Interest> findAllByInterestIn(List<String> names);
}
