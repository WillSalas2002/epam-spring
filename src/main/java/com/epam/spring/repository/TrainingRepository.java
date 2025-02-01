package com.epam.spring.repository;

import com.epam.spring.model.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {

    @Query("""
                SELECT t FROM Training t
                WHERE t.trainer.user.username = :trainerUsername
                AND (:fromDate IS NULL OR t.date >= :fromDate)
                AND (:toDate IS NULL OR t.date <= :toDate)
                AND (:traineeUsername IS NULL OR t.trainee.user.username LIKE %:traineeUsername%)
            """)
    List<Training> findTrainerTrainings(@Param("trainerUsername") String trainerUsername,
                                        @Param("fromDate") LocalDate fromDate,
                                        @Param("toDate") LocalDate toDate,
                                        @Param("traineeUsername") String traineeUsername);

    @Query("""
                SELECT t FROM Training t
                WHERE t.trainee.user.username = :traineeUsername
                AND (:fromDate IS NULL OR t.date >= :fromDate)
                AND (:toDate IS NULL OR t.date <= :toDate)
                AND (:trainerUsername IS NULL OR t.trainer.user.username LIKE %:trainerUsername%)
                AND (:trainingTypeName IS NULL OR t.trainingType.trainingTypeName LIKE %:trainingTypeName%)
            """)
    List<Training> findTraineeTrainings(@Param("traineeUsername") String traineeUsername,
                                        @Param("fromDate") LocalDate fromDate,
                                        @Param("toDate") LocalDate toDate,
                                        @Param("trainerUsername") String trainerUsername,
                                        @Param("trainingTypeName") String trainingTypeName);

}
