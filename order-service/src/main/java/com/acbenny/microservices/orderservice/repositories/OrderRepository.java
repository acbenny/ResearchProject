package com.acbenny.microservices.orderservice.repositories;

import java.util.Optional;
import java.util.Set;

import javax.annotation.PreDestroy;

import com.acbenny.microservices.orderservice.models.Order;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.metadata.sequence.OSequence;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private ODatabaseSession db;
    
    @Autowired
    public NEServiceClient neService;

    @Autowired
    public void setDb(ODatabaseSession db) {
        this.db = db;
    }

    @PreDestroy
    public void close() {
        db.activateOnCurrentThread();
        db.close();
    }

    public void createOrder(Order ord) {
        db.activateOnCurrentThread();
        OVertex ovPrev = null;
        OVertex ov = db.newVertex("Orders");
        if (ord.getOrderId() != 0) {
            ov.setProperty("orderId", ord.getOrderId());
        }
        if (ord.getServiceId() == null) {
            OSequence seq = db.getMetadata().getSequenceLibrary().getSequence("svcIdSeq");
            ord.setServiceId(String.format("%s%07d", "SVC", seq.next()));
        } else {
            ovPrev = getLatestOrder(ord.getServiceId()).orElse(null);
        }
        ov.setProperty("serviceId", ord.getServiceId());
        ov.save();
        if (ovPrev != null){
            ovPrev.addEdge(ov, "NextOrder").save();
        }
        db.commit();
    }

    public Order[] getAllOrders() {
        String sql = "SELECT FROM Orders";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql);
        return rs.vertexStream().map(x -> new Order(x.getProperty("orderId"), x.getProperty("serviceId"), null))
                .toArray(Order[]::new);
    }

    public Order getOrd(long ordId) {
        String sql = "SELECT FROM Orders WHERE orderId = ?";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql, ordId);

        return rs.vertexStream().findFirst().map(x -> new Order(x.getProperty("orderId"), x.getProperty("serviceId"), x.getProperty("neIds")))
                .orElseThrow();
    }

    public Order[] getAllServiceOrders(String serviceId) {
        String sql = "SELECT FROM Orders WHERE serviceId = ?";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql, serviceId);
        return rs.vertexStream().map(x -> new Order(x.getProperty("orderId"), x.getProperty("serviceId") , x.getProperty("neIds")))
                .toArray(Order[]::new);
    }

    public Optional<OVertex> getLatestOrder(String serviceId) {
        String sql = "SELECT FROM (TRAVERSE out('NextOrder') FROM (SELECT FROM Orders WHERE serviceId = ?)) WHERE out().size() = 0";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql, serviceId);
        return rs.vertexStream().findFirst();
    }

	public Order getLatestServiceOrder(String serviceId) {
        OVertex ov = getLatestOrder(serviceId).orElseThrow();
        return new Order(ov.getProperty("orderId"), ov.getProperty("serviceId"), ov.getProperty("neIds"));
	}

	public void routeOrder(String serviceId, Set<Integer> neIDs) {
        OVertex ov = getLatestOrder(serviceId).orElseThrow();
        Order ord = new Order(ov.getProperty("orderId"), ov.getProperty("serviceId"),null);
        neIDs.forEach(neId -> neService.route(neId, ord));
        ov.setProperty("neIds", neIDs, OType.EMBEDDEDSET);
        ov.save();
	}

	public void unrouteOrder(String serviceId, Set<Integer> neIDs) {
        OVertex ov = getLatestOrder(serviceId).orElseThrow();
        Order ord = new Order(ov.getProperty("orderId"), ov.getProperty("serviceId"), ov.getProperty("neIds"));
        ord.getNeIds().forEach(ne -> {
            if (neIDs.isEmpty() || neIDs.contains(ne)) {
                neService.unroute(neService.getOrdDetails(ne, ord.getOrderId()));
                ord.removeNE(ne);
            }
        });
        ov.setProperty("neIds", neIDs, OType.EMBEDDEDSET);
        ov.save();
	}
}