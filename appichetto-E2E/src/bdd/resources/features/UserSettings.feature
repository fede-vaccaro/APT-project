Feature: User settings feature 

Scenario: 
	An user change his username, the others see it updated
	Given   The database contains user 
		| name     | password |
		| Giuseppe | Gpsw     |
		| Federico | Fpsw     |
	And     The user "Giuseppe" has a receipt shared with "Federico" 
	And     Application start 
	And     "Login" view shows 
	And     User "Federico" is logged 
	When     Click "User panel" button 
	And     Write "Pasquale" in "newName" text box 
	And     Click "Update credential" button 
	And     Click "Back" button 
	And     Click "Log Out" button 
	And     User "Giuseppe" is logged 
	And     Click "Show History" button 
	And     Select the receipt 
	Then     There is an unpaid debt from "Pasquale" 
	And     Click "Back" button 
	And     Click "Create Receipt" button 
	And     There is "Pasquale" in the users list 
	
Scenario: 
	An user change his password 
	Given     The database contains user 
		| name     | password |
		| Federico | Fpsw     |
	And     Application start 
	And     "Login" view shows 
	And     User "Federico" is logged 
	When     Click "User panel" button 
	And     Write "newPsw" in "newPW" text box 
	And     Click "Update credential" button 
	And     Click "Back" button 
	And     Click "Log Out" button 
	And     Write "Federico" in "Username" text box 
	And     Write "newPsw" in "Password" text box 
	And     Click "Log-in" button 
	Then     "Homepage" view shown