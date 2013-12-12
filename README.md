## CASE STUDY: Income & Expense Reminder

### Scenario
HVN group wants to build a web application that reports its members' income & expense.
Members of HVN group will input their monthly (daily) income & daily expense so that they will receive the report from the system on schedule.

### Requirements
1. To become member of the HVN group, user has to sign up.
2. After an individual member login:
	- He can browse his inputs (all income & expense) history on the system.
	- He may input his income & expense. The information should include: type (income/expense), value, date, note.
	- In case of mistake, he can update/delete those inputs.
	- He may set the schedule to receive report via email.
3. Report:
	- A file which is sent via email represents a member's income & expense history.
	- It contains the overall calculation of income over expense (minus if expense is greater than income).
4. Administrator:
	- Admin has the right of ordinary member as well.
	- Admin can monitor the members' information (name, sign up date, email...) but he cannot see their income/expense history.

### Plan
* Set up the development environment.
* Prepare the data model for the application.
* Create front-end project files.
* Organize the application front-end.
* Create controllers.
* Connect the application to the database.
* Develop the business logic.

### Deployment
Create an account on 
Visit [demo app](http://play-scala-demo.hvn.cloudbees.net) deployed on [Cloudbees](http://www.cloudbees.com).
