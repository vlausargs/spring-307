package com.valos.core.spring307.services;

public interface TokenSerivce {

    public String generateToken();

    public Object checkToken();

    public Object removeToken();
}
