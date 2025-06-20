package org.example.service;

import org.example.generic.GenericResult;
import org.springframework.stereotype.Service;

@Service
public interface AudioService {
    GenericResult transferText(String text, String speed) throws Exception;
}
