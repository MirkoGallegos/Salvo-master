package com.mirkoexample.Salvo.repositories;

import com.mirkoexample.Salvo.models.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player,Long>{
    Player findByUserName(@Param("name") String name);
}
