Feature: Training Management

  Scenario: Successfully create a training
    Given a valid create training request
    When the create method is called
    Then a training should be saved and a message should be sent to MQ

  Scenario: Fetch trainings for a trainee
    Given a valid trainee username and date range
    When the findTraineeTrainings method is called
    Then a list of trainee trainings should be returned

  Scenario: Fetch trainings for a trainer
    Given a valid trainer username and date range
    When the findTrainerTrainings method is called
    Then a list of trainer trainings should be returned
