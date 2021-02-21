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
                    cmd.setValid(validateCommand(cmd));
                    if (!cmd.isValid()) {
                        clashCounter.increment();
                        anyFailure = true;
                    }
                    repo.save(cmd);
                }
            }
        }
        return !anyFailure;
    }

    private boolean validateCommand(Command cmd) {
        if (repo.existsByNeIdAndPortAndTagAndValidTrue(cmd.getNeId(), cmd.getPort(), cmd.getTag())
                || repo.existsByNeIdAndVrfNameAndValidTrue(cmd.getNeId(), cmd.getVrfName())
                || repo.existsByNeIdAndVrfNameAndInterfaceIdAndValidTrue(cmd.getNeId(), cmd.getVrfName(), cmd.getInterfaceId())
                || repo.existsByNeIdAndFilterIdAndValidTrue(cmd.getNeId(), cmd.getFilterId())
                || (cmd.isCreateCommunity() && repo.existsByNeIdAndVpnNameAndValidTrue(cmd.getNeId(), cmd.getVpnName()))
        ) {
            logger.error("CLASH DETECTED!!!");
            return false;
        }
        return true;
    }

}
