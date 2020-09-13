package com.acbenny.microservices.neservice.repositories;

import java.util.ArrayList;

import com.acbenny.microservices.neservice.models.NetworkElement;
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.record.OVertex;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class NeRepository {

    private ODatabaseSession db;

    @Autowired
    public void setDb(ODatabaseSession db){
        this.db = db;
    }

    public void close(){
        db.activateOnCurrentThread();
        db.close();
    }
    
    public void createNE (NetworkElement ne){
        db.activateOnCurrentThread();
        OVertex ov = db.newVertex("NetworkElement");
        ov.setProperty("neId", ne.getNeId());
        ov.setProperty("model", ne.getModel());
        ov.save();
    }

    public NetworkElement[] getAllNEs() {
        String sql = "SELECT FROM NetworkElement";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql);
        return rs.vertexStream()
                    .map(x -> new NetworkElement(x.getProperty("neId"), x.getProperty("model")))
                    .toArray(NetworkElement[]::new);
    }

    public NetworkElement getNE (int neId){
        String sql = "SELECT FROM NetworkElement WHERE neId = ?";
        db.activateOnCurrentThread();
        OResultSet rs = db.command(sql, neId);
        
        return rs.vertexStream()
                    .findFirst()
                    .map(x -> new NetworkElement(x.getProperty("neId"),x.getProperty("model")))
                    .orElseThrow();
    }
}