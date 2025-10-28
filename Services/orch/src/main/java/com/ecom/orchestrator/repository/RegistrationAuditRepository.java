package com.ecom.orchestrator.repository;

import com.ecom.orchestrator.entity.RegistrationAudit;
import com.ecom.orchestrator.entity.RegistrationRoleEnum;
import com.ecom.orchestrator.entity.RegistrationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationAuditRepository extends JpaRepository<RegistrationAudit, Long> {

    List<RegistrationAudit> findByOrchName(String orchName);

    List<RegistrationAudit> findByOrchNameAndAsRole(String orchName, RegistrationRoleEnum asRole);

    List<RegistrationAudit> findByServiceName(String serviceName);

    List<RegistrationAudit> findByStatus(RegistrationStatusEnum status);
}
