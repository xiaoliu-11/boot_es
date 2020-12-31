package com.example.boot_es.service;

import com.alibaba.fastjson.JSON;
import com.example.boot_es.pojo.Tupian;
import com.example.boot_es.utils.HtmlParseUtil;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TupianService {

     private final static String  INDEX_NAME ="biantu_index";
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    //1解析数据放入到elasticsearch
    public Boolean parseTupian(String keywords) throws Exception {
        List<Tupian> alldata = new HtmlParseUtil().parseHtml(keywords);

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("1m");
        for (int i = 0; i < alldata.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("biantu_index")
                            .source(JSON.toJSONString(alldata.get(i)), XContentType.JSON));
        }
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !bulkResponse.hasFailures();

    }

    //2.获取这写数据实现搜索功能。

    public List<Map<String, Object>> searchPage(String keyword, int pageNo, int pageSize) throws IOException {

        if (pageNo <= 1) {
            pageNo = 1;
        }
        //条件搜索。

        SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);

        //精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", keyword);
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        //结果解析
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }
        return list;
    }
}