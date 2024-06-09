# Backend-Spring
REST Back end for a booking app written in Spring Framework.

Team 4:
- Marko Mitošević SV56-2021
- Ilija Bešlin SV71-2021
- Branko Marić SV70-2021

Initialising the project:
- Install Postgres
- Run the init_database.sql script locates in resources folder
  ( psql -U postgres -d postgres -a -f init_database.sql )
- Run the app


Login credentials for test data:
- Hosts:
    - email: john.doe@example.com , password: password123
    - email: jane.smith@example.com , password: securepass
    - email: bob.johnson@example.com , password: pass123 
    - email: alice.williams@example.com , password: alicespass
- Guest:
  - email: charlie.brown@example.com , password: charliespass
  - email: eva.miller@example.com , password: evasspass
  - email: david.davis@example.com , password: davidspass
  - email: grace.jones@example.com , password: gracesspass
  - email: frank.white@example.com , password: franksspass
- Admin:
  - email: helen.martin@example.com , password: helenspass
- SysAdmin:
  - email: sys.admin@example.com , password: helenspass


### Command for starting keycloak
- kc.bat start-dev --http-port=8180