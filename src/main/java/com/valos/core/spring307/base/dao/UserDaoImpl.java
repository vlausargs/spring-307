package com.valos.core.spring307.base.dao;

import jakarta.persistence.EntityManager;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private EntityManager em;

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
