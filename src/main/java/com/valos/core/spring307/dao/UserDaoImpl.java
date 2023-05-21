package com.valos.core.spring307.dao;

import jakarta.persistence.EntityManager;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private EntityManager em;

    @Override
    @Transactional
    public Object save(Object request) {
        Map<String, Object> requestMap = (Map<String, Object>) request;

        return em.createNativeQuery("Insert into users (username,password,name) " +
                        " VALUES(:username,:password,:name) returning id")
                .setParameter("username",requestMap.get("username"))
                .setParameter("password",requestMap.get("password"))
                .setParameter("name",requestMap.get("name"))
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(
                        AliasToEntityMapResultTransformer.INSTANCE
                )
                .uniqueResult();
    }

    @Transactional
    @Override
    public Object getList(Object request) {
        return em.createNativeQuery("select * from users s ")
                .unwrap(org.hibernate.query.Query.class)
                .setResultTransformer(
                        AliasToEntityMapResultTransformer.INSTANCE
                )
                .list();

    }
}
