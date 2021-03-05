package com.acbenny.microservices.nmsstub;

import java.util.ArrayList;

import com.acbenny.microservices.nmsstub.models.Command;
import com.acbenny.microservices.nmsstub.models.ConfigOperation;
import com.acbenny.microservices.nmsstub.models.Port;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import brave.Tracer;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
@RequestMapping("/nms")
@Timed
public class Controller {

    Logger logger = LoggerFactory.getLogger(Controller.class);

    @Autowired
    private CommandRepository repo;

    @Autowired
    private Tracer tracer;

    private Counter totCounter;
    private Counter clashCounter;

    public Controller(MeterRegistry registry) {
        totCounter = registry.counter("count.nmsStub.operations","type","total");
        clashCounter = registry.counter("count.nmsStub.operations","type","clashed");
    }

    @PostMapping
    public boolean doPost(@RequestBody ArrayList<ConfigOperation> oprs) {
        boolean anyFailure = false;
        for (ConfigOperation opr : oprs) {
            totCounter.increment();
            for (Port port : opr.getPorts().values()) {
                for (int tag : port.getTags().keySet()) {
                    Command cmd = new Command(
                        opr.getNeId(),
                        opr.getVlanDescription(),
                        opr.getVpnName(),
                        opr.isCreateCommunity(),
                        opr.getVrfName(),
                        opr.getInterfaceId(),
                        opr.getFilterId(),
                        port.getPort(),
                        tag);
                    cmd.setClashType(validateCommand(cmd));
                    if (cmd.getClashType() != null) {
                        cmd.setValid(false);
                        clashCounter.increment();
                        anyFailure = true;
                    }
                    cmd.setTraceId(tracer.currentSpan().context().traceIdString());
                    repo.save(cmd);
                }
            }
        }
        return !anyFailure;
    }

    private String validateCommand(Command cmd) {
        String clashType = null;

        if (repo.existsByNeIdAndPortAndTagAndValidTrue(cmd.getNeId(), cmd.getPort(), cmd.getTag()))
            clashType = (clashType==null?"":clashType) +"PORT/TAG|";
        if (repo.existsByNeIdAndVrfNameAndValidTrue(cmd.getNeId(), cmd.getVrfName()))
            clashType = (clashType==null?"":clashType)+"VRF|";
        if (repo.existsByNeIdAndFilterIdAndValidTrue(cmd.getNeId(), cmd.getFilterId()))
            clashType = (clashType==null?"":clashType)+"FILTER|";
        if (cmd.isCreateCommunity() && repo.existsByNeIdAndVpnNameAndValidTrue(cmd.getNeId(), cmd.getVpnName()))
            clashType = (clashType==null?"":clashType)+"COMMUNITYEXISTS|";
        if (!cmd.isCreateCommunity() && !repo.existsByNeIdAndVpnNameAndValidTrue(cmd.getNeId(), cmd.getVpnName()))
            clashType = (clashType==null?"":clashType)+"COMMUNITYNOTCREATED|";
        if (repo.existsByNeIdAndVrfNameAndInterfaceIdAndValidTrue(cmd.getNeId(), cmd.getVrfName(), cmd.getInterfaceId()))
            clashType = (clashType==null?"":clashType)+"VRF/INTERFACEID|";
        
        if (clashType != null) {
            logger.error("CLASH DETECTED!!! :"+clashType);
        }

        return clashType;
    }
}
