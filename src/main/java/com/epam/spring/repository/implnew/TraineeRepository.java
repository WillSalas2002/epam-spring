package com.epam.spring.repository.implnew;

import com.epam.spring.model.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    @Query("SELECT t FROM Trainee t LEFT JOIN FETCH t.trainings WHERE t.user.username = :username")
    Optional<Trainee> findByUsername(@Param("username") String username);

    @Query("DELETE FROM Trainee t WHERE t.user.username = :username")
    void deleteByUsername(@Param("username") String username);
}
