package com.acbenny.microservices.orderservice.repositories;

import java.util.Optional;

import com.acbenny.microservices.orderservice.models.Order;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.metadata.sequence.OSequence;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {

    private ODatabaseSession db;

    @Autowired
    public void setDb(ODatabaseSession db) {
        this.db = db;
    }

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
            ovPrev = getLatestOrder(ord.getServiceId()).orElseThrow();
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
        return rs.vertexStream().map(x -> new Order(x.getProperty("orderId"), x.getProperty("serviceId")))
                .toArray(Order[]::new);
    }

    public Order getOrd(long ordId) {
        String sql = "SELECT FROM Orders WHERE orderId = ?";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql, ordId);

        return rs.vertexStream().findFirst().map(x -> new Order(x.getProperty("orderId"), x.getProperty("serviceId")))
                .orElseThrow();
    }

    public Order[] getAllServiceOrders(String serviceId) {
        String sql = "SELECT FROM Orders WHERE serviceId = ?";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql, serviceId);
        return rs.vertexStream().map(x -> new Order(x.getProperty("orderId"), x.getProperty("serviceId")))
                .toArray(Order[]::new);
    }

    public Optional<OVertex> getLatestOrder(String serviceId) {
        String sql = "SELECT FROM (TRAVERSE out('NextOrder') FROM (SELECT FROM Orders WHERE serviceId = ?)) WHERE out().size() = 0";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql, serviceId);
        return rs.vertexStream().findFirst();
    }

	public Order getLatestServiceOrder(String serviceId) {
        return getLatestOrder(serviceId)
                    .map(x -> new Order(x.getProperty("orderId"), x.getProperty("serviceId")))
                    .orElseThrow();
	}
}