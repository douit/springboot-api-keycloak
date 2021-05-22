# Spring boot REST API + KEYCLOAK
>>> Spring boot rest API with spring security with keycloak
>>> It has: Create account, Sign in account api and some addition apis to test



# SETUP KEYCLOAK
- Download Key Cloak 
-> Follow link https://www.keycloak.org/downloads.html -> Download Keycloak server (ZIP for window, TAR for ubuntu linux)
-> Unzip 

- Run Key Cloak
-> cd to key cloak folder/bin
-> Type standalone.bat

- Server setup
-> Follow link after run key cloak:  http://localhost:8080/auth
-> Create account

- Setup Realm (used to manage app (client))
-> After sign in, move your mouse to top left corner to see "Add realm" button
-> Fill name -> Create

- Create app (client) for realm
-> Move to Clients section -> Click "Create" button in top right corner
-> Client Id: unique name (named by you)
-> Choose Client protocol: openid-connect -> Why this? Standard client protocol for html/js
-> Root URL: This source code address (http://localhost:8081) -> Why 8081? KEYCLOAK server runs in port 8080.

- Config app (client) 
-> Fill name (you wish)
-> Access Type: Confidental (Allow us to create and login user)
-> Enable: Standard Flow Enabled, Direct Access Grants Enabled, Authorization Enabled
-> Save

- Create realm level role
-> Head to Roles section -> Click "Add Role" button in top right corner
-> Add Role then save
-> Go to Clients page -> Select your created client -> Moved to Service Account Roles in that client
-> Add selected to assign role

# RUN SOURCE CODE
-> Make sure already setup Java jdk 8 above

>>>Located to source then type "mvnw spring-boot:run" in your terminal to run source

# REFERENCE
https://www.tutorialsbuddy.com/keycloak-quickstart


>>>>>> All Contributions are appreciated ^^
