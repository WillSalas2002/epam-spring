Feature: TraineeService component tests

  Scenario: Create a new trainee successfully
    Given a create trainee request with first name "John" and last name "Doe"
    When the service creates the trainee
    Then the response should contain a non-null username and password

  Scenario: Fetch an existing trainee profile
    Given a trainee with username "John.Doe", firstname "John" and lastname "Doe" exists
    When the service fetches the profile for username "John.Doe"
    Then the response should contain the correct trainee information


  Scenario: Successfully update an existing trainee profile
    Given a trainee with username "John.Doe" exists
    And the update request has new first name "John" and last name "Doe"
    When the service updates the trainee profile
    Then the trainee should be saved with updated data
    And the update response should contain the updated data

  Scenario: Fail to update trainee profile when trainee does not exist
    Given no trainee with username "ghost_user" exists
    When the service tries to update the profile of unknown trainee
    Then a ResourceNotFoundException should be thrown

  Scenario: Delete an existing trainee by username
    Given a trainee with username "John.Doe" exists in the system
    When the service deletes the trainee with username "John.Doe"
    Then the trainee and related user should be removed from the repository


  Scenario: Successfully update trainee's trainer list
    Given a trainee with username "John.Doe" exists and has training with ID 100
    And a trainer with username "Simon.Kim" exists and has specialization "Strength"
    And the update request maps training 100 to trainer "Simon.Kim"
    When the service updates the trainee trainer list
    Then the trainee should be saved with updated trainer info
    And the response should include trainer "Simon.Kim" with specialization "Strength"

  Scenario: Fail to update when trainee not found
    Given no trainee with username "unknown" exists when update list
    When the service tries to update trainer list for unknown trainee
    Then a ResourceNotFoundException should be thrown

  Scenario: Fail to update when trainer not found
    Given a trainee with username "John.Doe" exists and has training with ID 101
    And no trainer with username "ghost_trainer" exists
    And the update request maps training 101 to trainer "ghost_trainer"
    When the service updates the trainee trainer list
    Then a NoSuchElementException should be thrown