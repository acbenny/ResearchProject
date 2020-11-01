package com.acbenny.microservices.neservice.repositories;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PreDestroy;

import com.acbenny.microservices.neservice.models.NetworkElement;
import com.acbenny.microservices.neservice.models.Order;
import com.acbenny.microservices.neservice.models.Port;
import com.acbenny.microservices.neservice.models.Tag;
import com.acbenny.microservices.neservice.models.VRF;
import com.orientechnologies.common.collection.OMultiCollectionIterator;
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

    @PreDestroy
    public void close() {
        db.activateOnCurrentThread();
        db.close();
    }

    private boolean checkInOEdge(Stream<OEdge> str, String propertyName, Object propertyValue) {
        return str.anyMatch(e -> propertyValue == e.getProperty(propertyName));
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
        try (OResultSet rs = db.command(sql);) {
            return rs.vertexStream().map(x -> new NetworkElement(x.getProperty("neId"), x.getProperty("model"), null))
                    .toArray(NetworkElement[]::new);
        }
    }

    private OVertex getNEFromDB(int neId) {
        String sql = "SELECT FROM NetworkElement WHERE neId = ?";
        db.activateOnCurrentThread();
        try (OResultSet rs = db.command(sql, neId);) {
            return rs.vertexStream().findFirst().orElseThrow();
        }
    }

    private OVertex getOrder(Order o, boolean create) {
        String sql = "SELECT FROM Order WHERE orderId = ?";
        db.activateOnCurrentThread();
        try (OResultSet rs = db.command(sql, o.getOrderId());) {
            return rs.vertexStream().findFirst().orElseGet(() -> {
                if (create) {
                    OVertex ov = db.newVertex("Order");
                    ov.setProperty("serviceId", o.getServiceId());
                    ov.setProperty("orderId", o.getOrderId());
                    ov.save();
                    return ov;
                } else
                    return null;
            });
        }
    }

    private Predicate<OVertex> orderFilter(long ordId) {
        return ov -> {
            for (OVertex ovOrder : ov.getVertices(ODirection.IN, "AllocatedTags")) {
                if (ovOrder.getProperty("orderId").equals(ordId))
                    return true;
            }
            return false;
        };
    }

    public NetworkElement getNEWithOrderFilter(int neId, long ordId) {
        OVertex ovNE = getNEFromDB(neId);
        NetworkElement ne = new NetworkElement(ovNE.getProperty("neId"), ovNE.getProperty("model"));
        for (OVertex ovPort : ovNE.getVertices(ODirection.OUT, "Ports")) {
            Port p = new Port(ovPort.getProperty("logicalPortName"));
            for (OVertex ovTag : ovPort.getVertices(ODirection.OUT, "Tags")) {
                for (OVertex ovOrder : ovTag.getVertices(ODirection.IN, "AllocatedTags")) {
                    Order ord = new Order(ovOrder.getProperty("orderId"), ovOrder.getProperty("serviceId"));
                    if (ordId == 0 || ordId == ord.getOrderId()) {
                        Tag tag = new Tag(ovTag.getProperty("outerTag"), ord);
                        p.addTag(tag);
                    }
                }
            }
            if (ordId == 0 || p.getTags().size() > 0)
                ne.addPort(p);
        }

        Set<String> neVrfs = new HashSet<>();
        OVertex ovVpn = null;
        for (OVertex ovVrf : ovNE.getVertices(ODirection.OUT, "Vrfs")) {
            neVrfs.add(ovVrf.getProperty("vrfName"));
            for (OEdge oeInterface : ovVrf.getEdges(ODirection.IN, "AllocatedVrfs")) {
                OVertex ovOrder = oeInterface.getFrom();
                if (ordId == 0 || ordId == (long) ovOrder.getProperty("orderId")) {
                    ovVpn = ovVrf.getVertices(ODirection.IN, "VpnVrfs").iterator().next();
                    ne.addVrf(new VRF(ovVpn.getProperty("vpnName"),ovVrf.getProperty("vrfName"))
                                ,oeInterface.getProperty("interfaceId"),ovOrder.getProperty("serviceId"));
                }
            }
        }

        if (ordId != 0 && ovVpn != null) {
            Stream<OVertex> vpnVrfStream = StreamSupport.stream(ovVpn.getVertices(ODirection.OUT, "VpnVrfs").spliterator(), false);
            long neVrfCount = vpnVrfStream.filter(v -> neVrfs.contains(v.getProperty("vrfName"))).count();
            if (neVrfCount == 1)
                ne.setCreateDeleteCommunity(true);
        }

        for (OEdge oeFilter : ovNE.getEdges(ODirection.OUT, "Filters")) {
            if (ordId == 0 || ordId == (long) oeFilter.getTo().getProperty("orderId")) {
                ne.addFilter(oeFilter.getProperty("id"), oeFilter.getTo().getProperty("serviceId"));
            }
        }

        if (ne.getPorts().size() == 0 && ne.getVrfs().size() == 0) {
            throw new NoSuchElementException("Ord id " + ordId + " not found on " + neId);
        }

        return ne;
    }

    public NetworkElement getNE(int neId) {
        return getNEWithOrderFilter(neId, 0);
    }

    public NetworkElement route(int neId, String portInput, Order o) {
        OVertex ovNE = getNEFromDB(neId);
        NetworkElement ne = null;

        for (OVertex ovPort : ovNE.getVertices(ODirection.OUT, "Ports")) {
            if (portInput == null || portInput.equals(ovPort.getProperty("logicalPortName"))) {
                Iterable<OEdge> ovTags = ovPort.getEdges(ODirection.OUT, "Tags");
                for (int i = tagStart; i <= tagEnd; i++) {
                    Stream<OEdge> str = StreamSupport.stream(ovTags.spliterator(), false);
                    if (!checkInOEdge(str, "tagId", i)) {
                        OVertex ovTag = db.newVertex("Tag");
                        ovTag.setProperty("outerTag", i);
                        ovTag.save();
                        OEdge oe = ovPort.addEdge(ovTag, "Tags");
                        oe.setProperty("tagId", i);
                        oe.save();
                        getOrder(o, true).addEdge(ovTag, "AllocatedTags").save();
                        Port port = new Port(ovPort.getProperty("logicalPortName"));
                        port.addTag(new Tag(i, o));
                        ne = new NetworkElement(ovNE.getProperty("neId"), ovNE.getProperty("model"));
                        ne.addPort(port);
                        return ne;
                    }
                }
            }
        }
        throw new NoSuchElementException("No Free Tags available on " + neId);
    }

    public void unroute(NetworkElement ne) {
        String sql = "SELECT expand(outE('Ports')[portName=?].inV().outE('Tags')[tagId=?].inV()) FROM NetworkElement WHERE neId = ?";
        db.activateOnCurrentThread();
        ne.getPorts().forEach((pKey, port) -> {
            port.getTags().forEach((tKey, tag) -> {
                try (OResultSet rs = db.command(sql, pKey, tKey, ne.getNeId());) {
                    rs.vertexStream().filter(orderFilter(tag.getOrd().getOrderId())).forEach(ovTag -> {
                        ovTag.delete();
                        ovTag.save();
                    });
                    cleanUpOrder(tag.getOrd());
                }
            });
        });
    }

    private void cleanUpOrder(Order o) {
        OVertex ov = getOrder(o, false);
        if (ov != null) {
            if (((OMultiCollectionIterator<OEdge>) ov.getEdges(ODirection.BOTH)).size() == 0) {
                ov.delete();
                ov.save();
            }
        }
    }

    private OVertex getVpn(String vpnName, boolean create) {
        String sql = "SELECT FROM Vpn WHERE vpnName = ?";
        db.activateOnCurrentThread();
        try (OResultSet rs = db.command(sql, vpnName);) {
            return rs.vertexStream().findFirst().orElseGet(() -> {
                if (create) {
                    OVertex ov = db.newVertex("Vpn");
                    ov.setProperty("vpnName", vpnName);
                    ov.save();
                    return ov;
                } else
                    return null;
            });
        }
    }

    public NetworkElement assignVrf(int neId, Order order) {
        if (order.getVpnName() != null) {
            db.activateOnCurrentThread();
            String sql = "SELECT FROM Order WHERE orderId = ?";
            OVertex ovOrder = null;
            try (OResultSet rs = db.command(sql, order.getOrderId());) {
                ovOrder = rs.vertexStream().findFirst().orElseThrow();
            }
            if (ovOrder.getVertices(ODirection.OUT, "AllocatedVrfs").iterator().hasNext()) {
                return getNEWithOrderFilter(neId, order.getOrderId());
            } else {
                db.begin();
                OVertex ovVpn = getVpn(order.getVpnName(), true);
                OVertex ovNe = getNEFromDB(neId);

                Stream<OVertex> ovStream = StreamSupport.stream(ovVpn.getVertices(ODirection.OUT, "VpnVrfs").spliterator(), false);
                boolean found = false;
                for (int i = tagStart; i <= tagEnd; i++) {
                    String vrfName = String.format("%s-s%04d", order.getVpnName(), i);
                    if (!ovStream.anyMatch(v -> vrfName == (String) v.getProperty("vrfName"))) {
                        OVertex ovVrf = db.newVertex("Vrf");
                        ovVrf.setProperty("vrfName", vrfName);
                        ovVrf.save();
                        ovVpn.addEdge(ovVrf, "VpnVrfs").save();
                        OEdge oeInterface = ovOrder.addEdge(ovVrf, "AllocatedVrfs");
                        oeInterface.setProperty("interfaceId", 1);
                        oeInterface.save();
                        ovNe.addEdge(ovVrf, "Vrfs").save();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    db.rollback();
                    throw new NoSuchElementException("VRF spoke id exhausted!");
                }

                Stream<OEdge> oeStream = StreamSupport.stream(ovNe.getEdges(ODirection.OUT, "Filters").spliterator(),false);
                found = false;
                for (int i = tagStart; i <= tagEnd; i++) {
                    if (!checkInOEdge(oeStream,"id",i)) {
                        OEdge oe = ovNe.addEdge(ovOrder,"Filters");
                        oe.setProperty("id", i);
                        oe.save();
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    db.rollback();
                    throw new NoSuchElementException("Filters exhausted!");
                }
            }
        }
        db.commit();
		return getNEWithOrderFilter(neId, order.getOrderId());
	}

	public void unassignOrder(Order o) {
        OVertex ovOrd = getOrder(o, false);
        if (ovOrd != null) {
            db.begin();
            for(OVertex ovTag : ovOrd.getVertices(ODirection.OUT, "AllocatedTags"))
                ovTag.delete();
            for(OVertex ovVrf : ovOrd.getVertices(ODirection.OUT, "AllocatedVrfs"))
                ovVrf.delete();
            
            if (((OMultiCollectionIterator<OEdge>) ovOrd.getEdges(ODirection.OUT)).size() == 0) {
                ovOrd.delete();
            }

            OVertex ovVpn = getVpn(o.getVpnName(), false);
            if (ovVpn != null) {
                if (((OMultiCollectionIterator<OEdge>) ovVpn.getEdges(ODirection.BOTH)).size() == 0) {
                    ovVpn.delete();
                }
            }

            db.commit();
        } else
        throw new NoSuchElementException("Ord id " + o.getOrderId() + " not found!");
	}
}