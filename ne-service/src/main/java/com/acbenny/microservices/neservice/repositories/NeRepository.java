package com.acbenny.microservices.neservice.repositories;

import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.acbenny.microservices.neservice.models.NetworkElement;
import com.acbenny.microservices.neservice.models.Order;
import com.acbenny.microservices.neservice.models.Port;
import com.acbenny.microservices.neservice.models.TagAllocation;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.ODirection;
import com.orientechnologies.orient.core.record.OEdge;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class NeRepository {

    private ODatabaseSession db;

    @Value("${tagRange.start:1}")
    private int tagStart;

    @Value("${tagRange.end:4094}")
    private int tagEnd;

    @Autowired
    public void setDb(ODatabaseSession db) {
        this.db = db;
    }

    public void close() {
        db.activateOnCurrentThread();
        db.close();
    }

    private boolean isTagUsed(Stream<OEdge> str, int tag){
        return str.anyMatch(e -> tag == (int)e.getProperty("tagId"));
    }

    public void createNE(NetworkElement ne) {
        db.activateOnCurrentThread();
        OVertex ov = db.newVertex("NetworkElement");
        if (ne.getNeId() != 0) {
            ov.setProperty("neId", ne.getNeId());
        }
        ov.setProperty("model", ne.getModel());
        ov.save();
        ne.setNeId(ov.getProperty("neId"));

        if (!ne.getPorts().isEmpty()) {
            ne.getPorts().forEach((k, v) -> {
                OVertex ovp = db.newVertex("Port");
                ovp.setProperty("logicalPortName", v.getPort());
                ovp.save();
                OEdge oe = ov.addEdge(ovp, "Ports");
                oe.setProperty("portName", v.getPort());
                oe.save();
            });
        }
    }

    public NetworkElement[] getAllNEs() {
        String sql = "SELECT FROM NetworkElement";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql);
        return rs.vertexStream().map(x -> new NetworkElement(x.getProperty("neId"), x.getProperty("model"),null))
                .toArray(NetworkElement[]::new);
    }

    private OVertex getNEFromDB(int neId) {
        String sql = "SELECT FROM NetworkElement WHERE neId = ?";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql, neId);
        return rs.vertexStream().findFirst().orElseThrow();
    }

    public NetworkElement getNE(int neId) {
        OVertex ovNE = getNEFromDB(neId);

        NetworkElement ne = new NetworkElement(ovNE.getProperty("neId"), ovNE.getProperty("model"));
        for (OVertex ovPort : ovNE.getVertices(ODirection.OUT, "Ports")){
            Port p = new Port(ovPort.getProperty("logicalPortName"));
            for (OVertex ovTag : ovPort.getVertices(ODirection.OUT, "Tags")) {
                TagAllocation tag = new TagAllocation(ovTag.getProperty("tagId"),
                                            new Order(ovTag.getProperty("orderId"), ovTag.getProperty("serviceId")));
                p.addTag(tag);
            }
            ne.addPort(p);
        }
        return ne;
    }

    public NetworkElement route(int neId, Order o) { 
        OVertex ovNE = getNEFromDB(neId);
        NetworkElement ne = null;
        
        for (OVertex ovPort : ovNE.getVertices(ODirection.OUT, "Ports")){
            Iterable<OEdge> ovTags = ovPort.getEdges(ODirection.OUT, "Tags");
            for (int i = tagStart; i <= tagEnd; i++) {
                Stream<OEdge> str = StreamSupport.stream(ovTags.spliterator(), false);
                if (!isTagUsed(str,i)){
                    OVertex ovTag = db.newVertex("Tag");
                    ovTag.setProperty("tagId", i);
                    ovTag.setProperty("serviceId", o.getServiceId());
                    ovTag.setProperty("orderId",o.getOrderId());
                    ovTag.save();
                    OEdge oe = ovPort.addEdge(ovTag, "Tags");
                    oe.setProperty("tagId", i);
                    oe.save();
                    Port port = new Port(ovPort.getProperty("logicalPortName"));
                    port.addTag(new TagAllocation(i,o)); 
                    ne = new NetworkElement(ovNE.getProperty("neId"), ovNE.getProperty("model"));
                    ne.addPort(port);
                    return ne;
                }
            }
        }
        throw new NoSuchElementException("No Free Tags available on "+neId);
    }

	public void unroute(NetworkElement ne) {
        String sql = "SELECT expand(outE('Ports')[portName=?].inV().outE('Tags')[tagId=?].inV()) FROM NetworkElement WHERE neId = ?";
        db.activateOnCurrentThread();
        ne.getPorts().forEach((pKey,port) -> {
            port.getTags().forEach((tKey,tag) -> {
                OResultSet rs = db.command(sql, pKey, tKey, ne.getNeId());
                rs.vertexStream()
                    .filter(ovTag -> tag.getOrd().getServiceId().equals(ovTag.getProperty("serviceId")) && tag.getOrd().getOrderId() == (long)ovTag.getProperty("orderId"))
                    .forEach(ovTag -> {
                        ovTag.delete();
                        ovTag.save();
                    });
            });
        });
	}
}