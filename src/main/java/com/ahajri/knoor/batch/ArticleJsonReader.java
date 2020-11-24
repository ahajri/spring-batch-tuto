package com.ahajri.knoor.batch;

import com.ahajri.knoor.model.Article;
import com.ahajri.knoor.utils.DataUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

public class ArticleJsonReader implements JsonObjectReader<Article> {
    private static final Log logger = LogFactory.getLog(ArticleJsonReader.class);

    private Resource resource;

    private ObjectMapper mapper = new ObjectMapper();

    private List<Article> items;
    private int index = 0;

    public ArticleJsonReader(Resource resource) {
        this.resource = resource;
    }

    @Override
    public void open(Resource resource) throws Exception {
        Assert.notNull(resource, "Input resource must be set");
        if (!resource.exists()) {
            logger.warn("Input resource does not exist " + resource.getDescription());
            return;
        }

        if (!resource.isReadable()) {
            logger.warn("Input resource is not readable " + resource.getDescription());
            return;
        }

        try {
            this.items = Collections
                    .synchronizedList(
                            this.mapper.readValue(resource.getInputStream(),
                                    TypeFactory.defaultInstance()
                                            .constructCollectionType(List.class, Article.class)));
        } catch (Exception ex) {
            throw new ParseException("Parsing error", ex);
        }
    }

    @Override
    public Article read() throws Exception {
        Article article = items.get(index);
        if (index < (items.size()-1) ) {
            index++;
        }else{
            return null;
        }
        return article;
    }

    @Override
    public void close() throws Exception {
        logger.info("-------------close");
    }
}
