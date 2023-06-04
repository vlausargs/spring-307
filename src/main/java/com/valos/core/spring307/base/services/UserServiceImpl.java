package com.valos.core.spring307.base.services;

import com.valos.core.spring307.base.component.ConsMessage;
import com.valos.core.spring307.base.component.ResponseWrapper;
import com.valos.core.spring307.base.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserDao repo;

    @Override
    public Object getList(Object request) {
        ResponseWrapper result = new ResponseWrapper();

        try {
            result.setResult(repo.getList(request));
            result.setCode(1);
            result.setMessage(ConsMessage.MESSAGE_SUCCESS);
        } catch (Exception e) {
            result.setCode(0);
            System.err.println("testerr");
            System.err.println(e.getMessage());
            result.setMessage(ConsMessage.MESSAGE_FAILED);

        }
        return result;
    }
}
