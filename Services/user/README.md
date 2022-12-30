### This project manages all user operations.
>Note : We have used keycloak as Authentication server.

To run this project we will need keycloak auth server up and running with default username and password.
```
Username : admin
Password : admin
```
Prerequisite 
Login to keycloak server using default "Admin" username and password
* Get "admin-cli" client id and it's secret from master realms - click on client menu under master realms.
* Create new sub-realms - realms name be anything which is relevant to project. We have created and used "ecom" as realms.(Check application.properties)
* Register our user service (This project) in keycloak server by creating client id and secret inside sub-realm("ecom"). Click on client menu.
* Create roles in sub-realms

>Note : From above steps note down all client id's and secret's and use those in properties file.

## API's
Create user : POST
```
http://localhost:8080/users/addUser
```
Request body
```
{
    "username":"pranay1@gmail.com",
    "email":"pranay1@gmail.com",
    "firstName":"pranay",
    "lastName":"Raut",
    "credentials":[{
        "type":"password",
        "value":"1234"
    }],
    "enabled":true

}
```

Login API : POST
```
http://localhost:8080/auth/login
```
Request body
```
{
    "username":"pranay1@gmail.com",
    "password":"1234"
}
```
