package com.genitus.channel.tracker.client;

import com.genitus.channel.tracker.model.parameter.Parameter;
import com.genitus.channel.tracker.util.TimeStampProcess;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class ESClient {
    private static Logger logger = LoggerFactory.getLogger(HBaseClient.class);
    private TransportClient client;
    private Settings setting;
    public ESClient(){}
    public ESClient(String cluster_Name,String name,String ipAddress,int port)throws UnknownHostException {
        try{
            logger.info("ipAddress: "+ipAddress+" port: "+port);
            setting=Settings.settingsBuilder()
                    .put(cluster_Name,name)
                    .build();
            client= TransportClient.builder()
                    .settings(setting)
                    .build()
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ipAddress), port));
        }catch (UnknownHostException e){
            throw new UnknownHostException("UnknownHostException");
        }

    }
    public TransportClient getClient(){
        return client;
    }


    public void close(){
        if (client!=null)
            client.close();
    }


    //定义两个方法 一个是getSearchResponse 另一个是turnPage  getSearchResponse中 需要获取过滤时间戳 turnpage中只需要搜索指定页数的内容，两个方法可以重用搜索的方法

    /**
     * This method can be called for users.
     * @param param The object of parameter class which contains all info about query.
     * @param indices
     * @return
     */
    public LinkedList<Object> getSearchResponse(Parameter param, String ...indices) {
        int startPage = param.getStartIndex(); //准备搜索第几页
        int pageSize = param.getSize();       //每页显示的大小
        int fromPos = (startPage-1)*pageSize+1;  //第一个数据的位置 （包含）
        int toPos = startPage*pageSize;         // 最后一个数据的位置 （包含）
        return getSearchResponse(param.getQuery(),fromPos,pageSize,indices);
    }


    /**
     * This method is called by the above method.
     * @param query
     * @param fromPos
     * @param pageSize
     * @param indices
     * @return LinkedList<Object></> The first object is timestamp(String type),and the second object is searchResponse(SearchResponse type).
     */
    private  LinkedList<Object> getSearchResponse(Map<String, Object> query, int fromPos, int pageSize, String ...indices) {
        FieldSortBuilder sortOrder=new FieldSortBuilder("@timestamp").order(SortOrder.DESC);
        String filterTimeStamp = getFilterTimeStamp(client,sortOrder,indices);
        BoolQueryBuilder boolQueryBuilder = getQueryBuilder(query,filterTimeStamp);
        SearchResponse searchResponse = fromSizeSearch(client,fromPos,pageSize,boolQueryBuilder,sortOrder,indices);
        LinkedList<Object> list = new LinkedList<Object>();
        list.add(filterTimeStamp);
        list.add(searchResponse);
        return list;
    }


    /**
     * This method is used to get newest timestamp and make timestamp forward 20 seconds.
     * @param client
     * @param sortOrder
     * @param indices
     * @return
     */
    private   String getFilterTimeStamp(TransportClient client,FieldSortBuilder sortOrder,String ... indices){
        String recentTimeStamp=fromSizeSearch(client,sortOrder,indices);
        //解析timestamp时间 将该时间退后20s 具体退后多长时间是个参数 可以自己设置 此处设置的是20s 大多数场景下 满足搜索返回一致性性能要求
        String filterTimeStamp = TimeStampProcess.makeTimeStampEarlier(recentTimeStamp,new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        logger.info("recentTimeStamp is: "+recentTimeStamp+" filterTimeStamp is: "+filterTimeStamp);
        return filterTimeStamp;
    }

    /**
     * Parse Map<String, Object> query to get BoolQueryBuilder
     * @param query
     * @param filterTimeStamp
     * @return
     */
    private  BoolQueryBuilder  getQueryBuilder(Map<String, Object> query,String filterTimeStamp) {
        if (query!=null&&!query.isEmpty())
            return parseQuery(query,filterTimeStamp);
        else {
            logger.info("query problem");
            return new BoolQueryBuilder();
        }

    }


    /**
     * Parse Map<String, Object> query to create BoolQueryBuilder query
     * @param query
     * @param filterTimeStamp
     * @return
     */
    private  BoolQueryBuilder  parseQuery(Map<String, Object> query,String filterTimeStamp) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        Iterator<Map.Entry<String, Object>> iterator = query.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, Object> entry = iterator.next();
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println("key is : "+key+", and value is : "+value);
            if(key.equals("star")&&value!=null&&!value.equals("")){
                System.out.println("here star");
                continue;
            }

            if(key.equals("sid")&&value!=null&&!value.equals("")){
                queryBuilder.must(QueryBuilders.termQuery(key,value));
                System.out.println("here sid");
            }

            if(key.equals("uid")&&value!=null&&!value.equals("")){
                queryBuilder.must(QueryBuilders.termQuery(key,value));
                System.out.println("here uid");
            }

            //下面代码实现了完全的精确匹配  可以运行成功
            if (key.equals("recs")&&value!=null&&!value.equals("")){
                queryBuilder.must( QueryBuilders.matchPhraseQuery("recs",(String)value));
                System.out.println("here recs");
            }

            if (key.equals("fromTimestamp")&&value!=null&&!value.equals("")){
                queryBuilder.must( QueryBuilders.rangeQuery("@timestamp").from(value));
                System.out.println("here fromTimestamp ");
            }
            if (key.equals("toTimestamp")&&value!=null&&!value.equals("")){
                queryBuilder.must( QueryBuilders.rangeQuery("@timestamp").to(value));
                System.out.println("here toTimestamp ");
            }
        }

        return queryBuilder.must(QueryBuilders.rangeQuery("@timestamp").to(filterTimeStamp));
    }

    //这个fromSizeSearch主要是返回当前所有的文档中最新的时间戳（实际上是用作过滤的时间戳）  此处的indices可以优化 使用最新的index作为第一次返回时间戳时候的索引

    /**
     * This fromSizeSearch method is used to find the newest timestamp in es cluster.
     * To get the newest timestamp, we have to search in es cluster using from+size method and sort the results based on timestamp.
     * Finally we get the top one SearchHit. So we get the newest timestamp.
     *
     * @param client
     * @param sortOrder
     * @param indices
     * @return timestamp, String type.
     */
    private  String fromSizeSearch( TransportClient client,FieldSortBuilder sortOrder, String ... indices){
        SearchRequestBuilder searchRequestBuilder =client.prepareSearch(indices).addSort(sortOrder).setFrom(0).setSize(10).setSearchType(SearchType.DEFAULT); //此处的0和100可以是固定的参数 不需要变动
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        SearchHit[] hits = searchResponse.getHits().getHits();
        return hits[0].getSource().get("@timestamp").toString();
    }

    //注意：fromPos-1是因为setFrom的起始位置是0 setFrom(0)表示从第一个数据开始返回

    /**
     * This fromSizeSearch is different with the above one.
     * The above one is used to get newest timestamp.
     * This one is used to get SearchResponse based on specific conditions.
     * @param client
     * @param fromPos
     * @param pageSize
     * @param boolQueryBuilder
     * @param sortOrder
     * @param indices
     * @return
     */
    private  SearchResponse fromSizeSearch( TransportClient client,int fromPos, int pageSize , BoolQueryBuilder boolQueryBuilder, FieldSortBuilder sortOrder , String ...indices){
        SearchRequestBuilder searchRequestBuilder =client.prepareSearch(indices).addSort(sortOrder).setFrom(fromPos-1).setSize(pageSize).setSearchType(SearchType.DEFAULT);
        searchRequestBuilder.setQuery(boolQueryBuilder);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        return searchResponse;

    }

    /**
     * Similar with getSearchResponse. First getSearchResponse method is get some parameter from param object and then call the second getSearchResponse method.
     * So here, first turnPage method is get some parameter from param object and then call the second turnPage method.
     * @param param
     * @param filterTimeStamp
     * @param indices
     * @return
     */
    public  SearchResponse turnPage( Parameter param,String filterTimeStamp,String ...indices){
        int startPage = param.getStartIndex(); //准备翻到第几页
        int pageSize = param.getSize();       //每页显示的大小
        int fromPos = (startPage-1)*pageSize+1;  //第一个数据的位置 （包含）
        int toPos = startPage*pageSize;         // 最后一个数据的位置 （包含）
        return turnPage(param.getQuery(),fromPos,pageSize,filterTimeStamp,indices);
        //turnPage(param.getQuery(),pageSize*(fromPages-1)+1,pageSize, filterTimeStamp);
    }

    /**
     * Parse query and then from+size search.
     * @param query
     * @param fromPos
     * @param pageSize
     * @param filterTimeStamp
     * @param indices
     * @return
     */
    private  SearchResponse turnPage( Map<String, Object> query, int fromPos, int pageSize,String filterTimeStamp,String ...indices) {
        FieldSortBuilder sortOrder=new FieldSortBuilder("@timestamp").order(SortOrder.DESC);
        BoolQueryBuilder  boolQueryBuilder = getQueryBuilder(query,filterTimeStamp);
        SearchResponse searchResponse = fromSizeSearch(client,fromPos,pageSize,boolQueryBuilder,sortOrder,indices);
        return searchResponse;
    }

/*
    //尚未验证性能
    public SearchResponse turnPage1(Parameter param,String ...indices){

        int fromPages = param.getStartIndex();  //Note：startIndex is the pages, startIndex=0 indicates it is first page
        int pageSize=param.getSize();
        String filterTimeStamp=param.getTimeStamp();

        SearchResponse searchResponse=new SearchResponse();
        //SearchResponse searchResponse =esClient.turnPage(param,filterTimeStamp);
        if(param.getCluster().equals("single")){
            searchResponse =turnPage(param,filterTimeStamp);
        }
        if (param.getCluster().equals("multi")){
            searchResponse =turnPage( param.getQuery(), 1,  fromPages*pageSize, filterTimeStamp);   //如果是在多集群上搜索 则需要在每个集群上都返回第1到第fromPages*pageSize的数据
        }

        return searchResponse;
    }
    */
}
