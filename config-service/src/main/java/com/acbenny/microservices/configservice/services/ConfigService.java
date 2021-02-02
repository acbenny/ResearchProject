package com.acbenny.microservices.configservice.services;

import com.acbenny.microservices.configservice.models.ConfigOperation;
import com.acbenny.microservices.configservice.models.NetworkElement;
import com.acbenny.microservices.configservice.models.Order;
import com.acbenny.microservices.configservice.models.VRF;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @Autowired
    public NEServiceClient neService;

    public ConfigOperation generateOperation(Order ord, int neId) {
        NetworkElement ne = null;
        if (ord.getVpnName() == null)
            ne = neService.getOrdDetails(neId, ord.getOrderId());
        else
            ne = neService.assignVRF(neId, ord);
        
        return populateOperation(ord, ne);
	}

	public ConfigOperation getOperation(Order ord, Integer neId) {
        NetworkElement ne = neService.getOrdDetails(neId, ord.getOrderId());
        return populateOperation(ord, ne);
    }
    
    private ConfigOperation populateOperation(Order ord, NetworkElement ne) {
        ConfigOperation opr = new ConfigOperation();
        opr.setNeId(ne.getNeId());
        opr.setVlanDescription(ord.getServiceId());
        VRF vrf = ne.getVrfs().stream().findAny().orElse(null);
        if (vrf != null) {
            opr.setVpnName(vrf.getVpnName());
            opr.setVrfName(vrf.getVrfName());
            opr.setInterfaceId(
                vrf.getInterfaces().entrySet().stream()
                    .filter(x -> ord.getServiceId().equals(x.getValue()))
                    .findFirst().orElseThrow()
                    .getKey());
            opr.setCreateCommunity(vrf.isCreateDeleteCommunity());
            opr.setFilterId(
                ne.getFilterIds().entrySet().stream()
                    .filter(x -> ord.getServiceId().equals(x.getValue()))
                    .findFirst().orElseThrow()
                    .getKey());
        }
        opr.setPorts(ne.getPorts());
        return opr;
    }
}