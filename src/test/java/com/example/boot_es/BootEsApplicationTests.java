package com.example.boot_es;

import com.alibaba.fastjson.JSON;
import com.example.boot_es.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
class BootEsApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
     private RestHighLevelClient client;

     //索引的创建
  @Test
    void testCreateIndex() throws IOException {

        //1.创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("lsg_index");
        //2.客户端执行请求
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }


    //判断索引是否存在

    @Test
    void testExistsIndex() throws IOException{
        GetIndexRequest request = new GetIndexRequest("lsg_index");
        boolean exists = client.indices().exists(request,RequestOptions.DEFAULT);
        System.out.println(exists);

    }


    //测试索引的删除
    @Test
    void testDeleteIndex() throws IOException{
        DeleteIndexRequest request = new DeleteIndexRequest("lsg_index");
        AcknowledgedResponse delete = client.indices().delete(request,RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());

    }

    //测试添加文档。
  @Test
      void testAddDocument() throws IOException {
      //创建对象
      User user = new User("刘树国",12);
       //创建请求
        IndexRequest request = new IndexRequest("lsg_index");
        //规则
        request.id("1");
        request.timeout("1s");
        //将我们的数据放入请求，json
     request.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求,获取响应结果
        IndexResponse indexResponse  = client.index(request,RequestOptions.DEFAULT);
        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());//对应我们命令返回的状态。

    }


    //测试获取文档，即判断文档是否存在。

    @Test
    void testIsExists() throws IOException {
        GetRequest getRequest = new GetRequest("lsg_index", "1");
        //过滤掉_source
        getRequest.fetchSourceContext(new FetchSourceContext(false));

        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);

    }

      //获取文档的信息


    @Test
    void testGetDocuemnt() throws IOException {
        GetRequest getRequest = new GetRequest("lsg_index", "1");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());

        //返回全部信息
        System.out.println(getResponse);

    }


    //更新文档的信息
    @Test
    void testUpdateDocuemnt() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("lsg_index","1");
        updateRequest.timeout("1s");

        User user  = new User("张三",21);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);
        UpdateResponse  updateResponse = client.update(updateRequest,RequestOptions.DEFAULT);
        System.out.println(updateResponse.status());
    }



    //删除文档信息
    @Test
    void testDeleteDocument() throws IOException{
        DeleteRequest request = new DeleteRequest("lsg_index","1");
        request.timeout("1s");

        DeleteResponse deleteResponse = client.delete(request,RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());



    }



    //批量插入数据，平时我们会会从其他地方（mysql）批量插入数据,
    @Test
    void testBulkRequeset() throws IOException{
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        ArrayList<User> userlist = new ArrayList<>();
        userlist.add(new User("刘树国1",21));
        userlist.add(new User("刘树国2",21));
        userlist.add(new User("刘树国3",21));
        userlist.add(new User("刘树国4",21));
        userlist.add(new User("刘树国5",21));
         //批处理请求
        for (int i = 0; i <userlist.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("lsg_index")
                     .id(""+(i+1))
                    .source(JSON.toJSONString(userlist.get(i)),XContentType.JSON));

        }
        BulkResponse bulkResponse = client.bulk(bulkRequest,RequestOptions.DEFAULT);
        System.out.println(bulkResponse.hasFailures());//如果结果为false表示插入成功。


    }



}
