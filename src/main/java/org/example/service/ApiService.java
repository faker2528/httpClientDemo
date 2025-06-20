package org.example.service;

import org.example.generic.GenericResult;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface ApiService{
    Map getServices(Map params);
}
