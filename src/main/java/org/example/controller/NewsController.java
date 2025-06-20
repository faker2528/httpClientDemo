package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.generic.GenericResult;
import org.example.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @RequestMapping("/getDailyNews")
    public GenericResult getNews() {
        return newsService.getNews();
    }
}
