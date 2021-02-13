package com.acbenny.microservices.nmsstub;

import com.acbenny.microservices.nmsstub.models.Command;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommandRepository extends JpaRepository<Command,Long> {
    boolean existsByNeIdAndVpnNameAndValidTrue (int neId, String vpnName);
    boolean existsByNeIdAndVrfNameAndValidTrue (int neId, String vrfName);
    boolean existsByNeIdAndVrfNameAndInterfaceIdAndValidTrue (int neId, String vrfName, int interfaceId);
    boolean existsByNeIdAndFilterIdAndValidTrue (int neId, int filterId);
    boolean existsByNeIdAndPortAndTagAndValidTrue (int neId, String port, int tag);
}
