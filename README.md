# About
This GitHub repository hosts the implementation of a web application designed to simulate cloud provider services.
The application aims to create an environment where users can manage and control the state of virtual machines and associated resources, providing a seamless experience.

# Project Overview
The project focuses on two main domains: user management and machine management. 
Users are able to create and control their virtual machines within the application.
Each machine is associated with its creator, and users have visibility only to their own machines. The core functionalities include:

User Management: Extends functionalities from the previous assignment, with additional permissions for machine management.

Machine Management: Includes actions such as searching, starting, stopping, restarting, creating, and destroying machines, each accompanied by specific permissions.

## Requirements
Machines are defined by certain attributes, including ID, status, creator reference, and an active flag for soft deletion. 

Actions like search, start, stop, restart, create, and destroy are governed by corresponding permissions.
Operations like start, stop, and restart have a time delay and execute asynchronously in the background.

## Actions on Machines
SEARCH: Retrieves a list of active machines based on user-defined parameters.

START: Initiates the start process for a machine.

STOP: Initiates the stop process for a machine.

RESTART: Initiates the restart process for a machine.

CREATE: Instantly creates a new machine.

DESTROY: Marks a machine as deleted without removing it from the database.

## Scheduling Operations
Operations such as START, STOP, and RESTART can be scheduled for future execution, with the system attempting to execute them at the specified time.

## Frontend Implementation
In addition to user management interfaces, the frontend includes three new pages:

Machine Search: Displays all machines on page load and provides a form for advanced searching based on backend functionalities.

Machine Creation: Allows users to create new machines with a simple form.

Error History Page: Displays errors encountered during scheduled operations execution, limited to the logged-in user's machines.

## Technologies

### Frontend:
- [:computer: Frontend Repository](https://github.com/david-n0/Cloud-WebApp-Frontend)
  
Framework: Angular
### Backend:
- [:gear: Backend Repository](https://github.com/david-n0/Cloud-WebApp-Backend)
  
Framework: Spring 
Database: Relational Database

