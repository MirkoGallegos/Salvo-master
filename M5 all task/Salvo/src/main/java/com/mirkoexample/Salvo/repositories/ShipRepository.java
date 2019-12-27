package com.mirkoexample.Salvo.repositories;

import com.mirkoexample.Salvo.models.Ship ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ShipRepository extends JpaRepository<Ship ,Long>{
}
