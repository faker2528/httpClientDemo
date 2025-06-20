package org.example.service;

import org.example.generic.GenericResult;
import org.springframework.stereotype.Service;

@Service
public interface NewsService {
    public GenericResult getNews();
}
