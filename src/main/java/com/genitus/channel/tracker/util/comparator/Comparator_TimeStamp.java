package com.genitus.channel.tracker.util.comparator;

import com.genitus.channel.tracker.util.Utils;
import org.elasticsearch.search.SearchHit;


import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by Administrator on 2017/8/22.
 */
public class Comparator_TimeStamp  implements Comparator {
    public int compare(Object arg0, Object arg1) {
        SearchHit hit0=(SearchHit)arg0;
        SearchHit hit1=(SearchHit)arg1;

        String timestamp0 = hit0.getSource().get("@timestamp").toString();
        String timestamp1 = hit1.getSource().get("@timestamp").toString();
     //   SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return compareTimeStamp(timestamp0,timestamp1,simpleDateFormat);
    }
    public int compareTimeStamp(String timestamp0,String timestamp1,SimpleDateFormat simpleDateFormat){
        long time0= Utils.changToLongType(timestamp0,simpleDateFormat);
        long time1= Utils.changToLongType(timestamp1,simpleDateFormat);
        if (time0!=time1)
            return time0>time1?-1:1;
        else return 0;
    }

}
