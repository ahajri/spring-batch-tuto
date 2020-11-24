package com.ahajri.knoor.batch;

import com.ahajri.knoor.model.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class ArticleItemProcessor implements ItemProcessor<Article, Article> {
    private static Logger logger = LoggerFactory.getLogger(ArticleItemProcessor.class);
    @Override
    public Article process(Article item) throws Exception {
        logger.info("Processing Article 'ID:  {}'", item.getId());
        return item;
    }
}
