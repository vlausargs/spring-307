package com.valos.core.spring307.base.dao;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Repository
public class AuthorityDaoImpl implements AuthorityDao{
    @Autowired
    private EntityManager em;

    @Override
    @Transactional
    public void assignAuthorityToUser(Object request) {
        Map<String, Object> requestMap = (Map<String, Object>) request;

        em.createNativeQuery("Insert into auth_authority_member (user_id,auth_id) " +
                        " VALUES(:user_id,'DEFAULT_AUTH') ")
                .setParameter("user_id",requestMap.get("user_id"))
                .executeUpdate();
    }
}
