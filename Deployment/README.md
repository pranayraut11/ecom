## Application deployment on local 
### Prerequisite
* Git
* Docker
* Kubernetes
* Kubectl
* Helm
* Minimum 16GB RAM

#### Clone project 
```
git clone https://github.com/pranayraut11/ecom.git
```
Go to directory 
```
cd ecom/Deployment/helm/
```
Deployment using helm
```
helm install dev-env environment/dev
```
You will see
```
NAME : dev-env
LAST DEPLOYED : Sat Jul  1 22:28:54 2023
NAMESPACE : default
STATUS : deployed
REVISION : 1
TEST SUITE : None
```
Check pods
```
kubectl get pods
```
Output should look like 
```
NAME                                              READY   STATUS    RESTARTS   AGE
cart-deployment-b9b9b7487-b8cwq                   1/1     Running   0          2m33s
dev-env-keycloak-0                                1/1     Running   0          2m33s
dev-env-minio-554ff94df-d8f9t                     1/1     Running   0          2m33s
dev-env-mongodb-bc744d44d-w7nqq                   1/1     Running   0          2m33s
dev-env-postgresql-0                              1/1     Running   0          2m33s
dev-env-redis-master-0                            1/1     Running   0          2m33s
dev-env-redis-replicas-0                          1/1     Running   0          2m33s
ecom-ui-84465cd9cf-jlrv4                          1/1     Running   0          2m33s
filemanager-service-deployment-6c99b9f979-zgrw7   1/1     Running   0          2m33s
inventory-deployment-85989869cd-xtvxg             1/1     Running   0          2m33s
orchestrator-deployment-74d4659566-rd2sq          1/1     Running   0          2m33s
product-deployment-5c6f5dbbbf-jqlc5               1/1     Running   0          2m33s
user-service-deployment-7db7bd9496-v5xw5          1/1     Running   0          2m33s
```

### Before accessing application on browser we will need to do configration .
* ### Add path to hosts file .
  Open hosts file
  * Windows - Windows/System32/drivers/etc
  * Linux - /etc/hosts
  
  Add following paths to hosts file and save.
  ```
  127.0.0.1 dev-env
  127.0.0.1 keycloak.local
  127.0.0.1 minio.local
  127.0.0.1 dev-env-ui
  ```

* ### Keycloak OAuth server configuration
    Go to web browser and access [http://keycloak](http://keycloak.local/)
    

* ### Minio File server configuration