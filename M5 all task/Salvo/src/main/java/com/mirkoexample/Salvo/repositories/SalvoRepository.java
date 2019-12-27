package com.mirkoexample.Salvo.repositories;

import com.mirkoexample.Salvo.models.Salvo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface SalvoRepository extends JpaRepository <Salvo, Long>{
}
