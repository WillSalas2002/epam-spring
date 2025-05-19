Feature: Trainer creation

  Scenario: Successfully create a trainer with valid input
    Given a valid create trainer request
    When the trainer service creates the trainer
    Then the response should contain the generated username and password


  Scenario: Trainer with given username exists
    Given a trainer exists with username "Simon.Kim"
    When I fetch the trainer profile with username "Simon.Kim"
    Then the trainer profile should be returned successfully

  Scenario: Trainer with given username does not exist
    Given no trainer exists with username "unknown"
    When I fetch the trainer profile with username "unknown"
    Then a ResourceNotFoundException should be thrown in trainer service


  Scenario: Trainer and training type exist and profile is updated successfully
    Given a trainer exists with username "Simon.Kim" when update
    And a training type exists with id 101
    When I update the trainer profile with valid data
    Then the trainer profile should be updated successfully

  Scenario: Trainer with given username does not exist
    Given no trainer exists with username "unknown" when update
    When I update the trainer profile with that username
    Then a ResourceNotFoundException should be thrown in trainer service

  Scenario: Training type with given id does not exist
    Given a trainer exists with username "Simon.Kim" when update
    And no training type exists with id 999
    When I update the trainer profile with that training type id
    Then a ResourceNotFoundException should be thrown in trainer service