#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template
@tag
Feature: Payment handling feature

  @tag1
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

  @tag2
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

  @tag3
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
