Feature: Appichetto Application Frame
  Specifications of the behavior of Appichetto

  Scenario: Log in error because not signed
    When "Login" view shows
    And Write "Giuseppe" in "Username" text box
    And Write "Gpsw" in "Password" text box
    And Click "Log-in" button
    Then The view contain the following message "User not signed in yet"

  Scenario: Log in success
    Given The database contains user
      | name     | password |
      | Giuseppe | Gpsw     |
    And "Login" view shows
    When Write "Giuseppe" in "Username" text box
    And Write "Gpsw" in "Password" text box
    And Click "Log-in" button
    Then "Homepage" view shown


