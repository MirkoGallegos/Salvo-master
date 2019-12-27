package com.mirkoexample.Salvo.repositories;

import com.mirkoexample.Salvo.models.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface GamePlayerRepository extends JpaRepository <GamePlayer ,Long> {
}
