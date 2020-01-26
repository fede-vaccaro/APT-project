Feature: Payment handling feature

  Scenario: An user has a debt with another user, then after one paid it the other can verify.
    Given The database contains user
      | name     | password |
      | Giuseppe | Gpsw     |
      | Federico | Fpsw     |
    And The user "Giuseppe" has a receipt shared with "Federico"
    And Application start
    And "Login" view shows
    And User "Federico" is logged
    When Click "Pay Receipt" button
    And Write the import and pay
    Then A message is shown saying there are no more receipts to pay
    And Click "Back" button
    And Click "Log Out" button
    And User "Giuseppe" is logged
    And Click "Show History" button
    And Select the receipt
    And The debt with "Federico" is paid

  Scenario: The first user pay his debt, then the second user change the receipt and has to refund the first user
    Given The database contains user
      | name     | password |
      | Giuseppe | Gpsw     |
      | Federico | Fpsw     |
    And The user "Giuseppe" has a receipt shared with "Federico"
    And Application start
    And "Login" view shows
    And User "Federico" is logged
    When Click "Pay Receipt" button
    And Write the import and pay
    And Click "Back" button
    And Click "Log Out" button
    And User "Giuseppe" is logged
    And Click "Show History" button
    And Select the receipt
    And Click "Update receipt" button
    And Edit the items discharging "Federico"
    And Click "Save Receipt" button
    And Click "Back" button
    Then Click "Pay Receipt" button
    And Has a refund receipt with the amount already paid with "Federico"

  Scenario: The user delete itself, then the other does not find the related receipts
    Given The database contains user
      | name     | password |
      | Giuseppe | Gpsw     |
      | Federico | Fpsw     |
    And The user "Giuseppe" has a receipt shared with "Federico"
    And Application start
    And "Login" view shows
    And User "Giuseppe" is logged
		And Click "User panel" button
		And Click "Remove user" button
		And Click "Yes" button
    When User "Federico" is logged
    And Click "Pay Receipt" button
    Then A message is shown saying there are no more receipts to pay
