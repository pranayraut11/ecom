# EcomUi

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 14.1.1.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The application will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via a platform of your choice. To use this command, you need to first add a package that implements end-to-end testing capabilities.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.

## Install bootstrap
```
npm install bootstrap --save
```
## Add Bootstrap to angular.json
```
"styles": [
              "node_modules/bootstrap/dist/css/bootstrap.min.css",
              "src/styles.css"
            ],
            "scripts": [ 
            "node_modules/jquery/dist/jquery.min.js",
            "node_modules/bootstrap/dist/js/bootstrap.min.js"
          ]
```
## Install bootstrap jQuery
```
npm install bootstrap jquery --save
```
### Before building UI docker image delete all related images
```
pranayraut11/ecom-ui   latest           2c25e8f217af   18 minutes ago   51.1MB
<none>                 <none>           bb3d85155f83   18 minutes ago   635MB
node                   lts-alpine3.18   f85482183a4f   2 days ago       175MB
nginx                  alpine           4937520ae206   9 days ago       41.4MB
```
### Build docker image of UI application
```
docker build -t pranayraut11/ecom-ui .
```
### Push docker image to docker hub repo
```
docker push pranayraut11/ecom-ui
```
### Install JSON Server
```
npm install -g json-server
```
### Install module to run json config file
```
npm install json-server --save-dev
```
### Run json server with db file(file is already in the root folder)
```
node json-server-config.js
```
