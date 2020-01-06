Feature: Receipt features
  Specifications of the behavior of create, update and remove a Receipt on Appichetto

  Scenario: Create new Receipt
    Given The database contains user
      | name     | password |
      | Giuseppe | Gpsw     |
      | Federico | Fpsw     |
      | Pasquale | Ppsw     |
      | Checco   | Cpsw     |
    And "Login" view shows
    And User "Giuseppe" is logged
    And Click "Create Receipt" button on homepage
    When Add new item
      | name  | price | quantity | owners                            |
      | pizza |   2.2 |        3 | Giuseppe Federico Pasquale        |
      | birra |  0.89 |        4 | Giuseppe Federico Pasquale Checco |
    Then "Items list" contains
      | pizza, EUR: 2.2, x3  |
      | birra, EUR: 0.89, x4 |

  Scenario: Save new Receipt
    Given The database contains user
      | name     | password |
      | Giuseppe | Gpsw     |
      | Federico | Fpsw     |
      | Pasquale | Ppsw     |
      | Checco   | Cpsw     |
    And "Login" view shows
    And User "Giuseppe" is logged
    And Click "Create Receipt" button on homepage
    When Add new item
      | name  | price | quantity | owners                            |
      | pizza |   2.5 |        3 | Giuseppe Federico Pasquale        |
      | birra |     1 |        4 | Giuseppe Federico Pasquale Checco |
    And Click "Save Receipt" button
    And Click "Show History" button on homepage
    Then "Receipts list" contains
      | Receipt [description= null items [pizza, EUR: 2.5, x3, birra, EUR: 1.0, x4]] |

  Scenario: Update item in Receipt
    Given The database contains user
      | name     | password |
      | Giuseppe | Gpsw     |
      | Federico | Fpsw     |
      | Pasquale | Ppsw     |
      | Checco   | Cpsw     |
    And The database contains receipt of "Giuseppe" with
      | name  | price | quantity | owners                            |
      | birra |     1 |        4 | Giuseppe Federico Pasquale Checco |
    And "Login" view shows
    And User "Giuseppe" is logged
