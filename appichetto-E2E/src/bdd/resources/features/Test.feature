Feature: AAA 
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
	And Click "Log out" button 
	And "Login" view shows 
	And User "Federico" is logged 
	
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
	And Click "Log out" button 
	And "Login" view shows 
	And User "Federico" is logged 
	And Click "Pay Receipt" button on homepage 
	And Set "Giuseppe" in "User selection" 
	Then "Receipts list" contains 
		| ??? |
	And "Items list" contains 
		| pizza, EUR: 2.2, x3  |
		| birra, EUR: 0.89, x4 |