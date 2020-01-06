Feature: AAA 
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
	And Click "Create Receipt" button on homepage 
	When Add new item 
		| name  | price | quantity | owners                            |
		| pizza |   2.5 |        3 | Giuseppe Federico Pasquale        |
		| birra |     1 |        4 | Giuseppe Federico Pasquale Checco |
	And Click "Save Receipt" button 
	And Click "Log Out" button 
	And "Login" view shows 
	And User "Federico" is logged 
	And Click "Pay Receipt" button on homepage 
	And Set "Giuseppe" in "User selection" 
#	Then "Receipts list" contains 
#		| ??? |
	Then "Items list" contains 
		| pizza, EUR: 2.5, x3 |
		| birra, EUR: 1.0, x4 |
	And debt to user is 3.50