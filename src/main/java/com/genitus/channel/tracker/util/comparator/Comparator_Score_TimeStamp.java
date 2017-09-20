package com.genitus.channel.tracker.util.comparator;

import com.genitus.channel.tracker.util.Utils;
import org.elasticsearch.search.SearchHit;


import java.text.SimpleDateFormat;
import java.util.Comparator;

/**
 * Created by Administrator on 2017/8/22.
 */
public class Comparator_Score_TimeStamp implements Comparator {
    public int compare(Object arg0, Object arg1) {
        SearchHit hit0=(SearchHit)arg0;
        SearchHit hit1=(SearchHit)arg1;

        String timestamp0 = hit0.getSource().get("@timestamp").toString();
        float score0=hit0.getScore();
        String timestamp1 = hit1.getSource().get("@timestamp").toString();
        float score1=hit1.getScore();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return compareTimeStamp(timestamp0,timestamp1,score0,score1,simpleDateFormat);
    }
    public int compareTimeStamp(String timestamp0,String timestamp1,float score0,float score1,SimpleDateFormat simpleDateFormat){
        if (score0!=score1)
            return score0>score1?1:-1;
        else {
            long time0= Utils.changToLongType(timestamp0,simpleDateFormat);
            long time1= Utils.changToLongType(timestamp1,simpleDateFormat);

            if (time0!=time1)
                return time0>time1?1:-1;
            else return 0;
        }


    }
}
