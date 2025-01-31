package com.epam.spring.repository.implnew;

import com.epam.spring.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {

    @Query("SELECT t FROM Trainer t LEFT JOIN FETCH t.trainings WHERE t.user.username = :username")
    Optional<Trainer> findByUsername(@Param("username") String username);

    @Query("""
            SELECT DISTINCT t
            FROM Trainer t
                     JOIN User u ON t.user.id = u.id
            WHERE t.id NOT IN (SELECT tr.id
                               FROM Training trn
                                        JOIN Trainer tr ON tr.id = trn.trainer.id
                                        JOIN Trainee tn ON tn.id = trn.trainee.id
                                        JOIN User tu ON tu.id = tn.user.id
                               WHERE tu.username =: username)
            """)
    List<Trainer> findUnassignedTrainersByTraineeUsername(@Param("username") String username);
}
