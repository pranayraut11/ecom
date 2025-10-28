package com.ecom.authprovider.service.specification;

import com.ecom.authprovider.dto.request.RealmRequest;

public interface AdminService {
    boolean createRealm(RealmRequest request) ;
    boolean deleteRealm(String realmName) ;
    void updateRealm(String realmName, Object realmConfig) ;
    Object getRealm(String realmName) ;
}
