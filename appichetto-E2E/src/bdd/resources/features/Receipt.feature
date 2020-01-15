Feature: Receipt features
  Specifications of the behavior of create, update and remove a Receipt on Appichetto

  Scenario: An user add an item to an already existent receipt
    Given The database contains user
      | name     | password |
      | Giuseppe | Gpsw     |
      | Federico | Fpsw     |
      | Pasquale | Ppsw     |
      | Checco   | Cpsw     |
    And The user "Giuseppe" has a receipt shared with "Federico"
    And Application start
    And "Login" view shows
    And User "Giuseppe" is logged
    When Click "Show History" button
    And Select the receipt
    And Click "Update receipt" button
    And Add new item
      | name  | price | quantity | owners            |
      | pasta |   3.0 |        3 | Giuseppe Federico |
    And Click "Save Receipt" button
    And Click "Back" button
    And Click "Log Out" button
    And "Login" view shows
    And User "Federico" is logged
    And Click "Pay Receipt" button
    Then debt increased of 4.5

  Scenario: Create new Receipt
    Given The database contains user
      | name     | password |
      | Giuseppe | Gpsw     |
      | Federico | Fpsw     |
      | Pasquale | Ppsw     |
      | Checco   | Cpsw     |
    And Application start
    And "Login" view shows
    And User "Giuseppe" is logged
    And Click "Create Receipt" button
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
    And Application start
    And "Login" view shows
    And User "Giuseppe" is logged
    And Click "Create Receipt" button
    When Add new item
      | name  | price | quantity | owners                            |
      | pizza |   2.5 |        3 | Giuseppe Federico Pasquale        |
      | birra |     1 |        4 | Giuseppe Federico Pasquale Checco |
    And Click "Save Receipt" button
    And Click "Show History" button
    Then "Receipts list" contains
      | Receipt [description= null items [pizza, EUR: 2.5, x3, birra, EUR: 1.0, x4]] |
