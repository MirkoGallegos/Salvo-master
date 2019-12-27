package com.mirkoexample.Salvo.repositories;

import com.mirkoexample.Salvo.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GameRepository extends JpaRepository <Game, Long> {

}
