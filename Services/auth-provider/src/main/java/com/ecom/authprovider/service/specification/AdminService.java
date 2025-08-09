package com.ecom.authprovider.service.specification;

public interface AdminService {
    void createRealm(String realmName) ;
    void deleteRealm(String realmName) ;
    void updateRealm(String realmName, Object realmConfig) ;
    Object getRealm(String realmName) ;
}
