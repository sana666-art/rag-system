package com.rag_system.repository;

import com.rag_system.dto.activeSubcriptionDTO.ActiveSubscriberRawDTO;
import com.rag_system.entity.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, Integer> {

    @Query(value = "SELECT * FROM NumberOfActiveSubscribers()", nativeQuery = true) //donot parse Q send directly
    List<ActiveSubscriberRawDTO> getActiveSubscribers_func();
}
