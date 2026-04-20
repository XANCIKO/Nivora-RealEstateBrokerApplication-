package com.capstone.realestate.repository;

import com.capstone.realestate.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Integer> {

    List<Property> findByBroker_BroId(int broId);

    @Query("SELECT p FROM Property p WHERE (:config IS NULL OR p.configuration = :config) " +
           "AND (:offer IS NULL OR p.offerType = :offer) " +
            "AND (:city IS NULL OR TRIM(:city) = '' OR LOWER(p.city) LIKE LOWER(CONCAT(:city, '%'))) " +
           "AND (:minCost = 0 OR p.offerCost >= :minCost) " +
           "AND (:maxCost = 0 OR p.offerCost <= :maxCost)")
    List<Property> findByCriteria(
            @Param("config") String config,
            @Param("offer") String offer,
            @Param("city") String city,
            @Param("minCost") double minCost,
            @Param("maxCost") double maxCost
    );
}
