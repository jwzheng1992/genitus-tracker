package com.genitus.channel.tracker.util.audio;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.genitus.karyo.model.data.SessionMedia;
import org.genitus.karyo.model.log.RawLog;
import org.genitus.karyo.model.log.SvcLog;

import java.io.File;
import java.util.*;

public class RemoteService {
    private static final List<String> AUDIO_EXTRAS = Arrays.asList(PropertiesUtils.getProperty("logs.extras.fields", "").split(","));
    public void savaAudioFile(final byte[] data, final String aue, final String auf, String sid) {
       // String filePath = "AUDIO_FILE_PATH" + File.separator + sid + File.separator;
        String filePath = "/home/jwzheng/genitus-tracker/";
        System.out.println("start in save");
        try {
            // 音频解码
    //        LOGGER.info("音频解码开始！");
            System.out.println("音频解码开始！");
            byte[] bt = AudioTransform.getSoundByte(data, aue, auf);
            System.out.println("bt.length is: "+bt.length);
            if (bt != null) {
                System.out.println("bt[] is not null");
                String filename = filePath + sid + ".wav";
                FileUtils.savaFile(filePath, filename, bt, false);
            }
        } catch (Throwable e) {
            String filename = filePath + sid + ".original";
            try {
                FileUtils.savaFile(filePath, filename, data, false);
            } catch (Exception e1) {
        //        LOGGER.error("", e1);
                System.out.println(e1);
            }
        //    LOGGER.error("音频解码失败！", e);
            System.out.println(e);
        }
    }





    public void unionDataToAudioInfo(String sid, AudioInfo audioInfo, UnionData union) throws Exception {
        if (union != null) {
            // 日志格式转换
            for (SvcLog svcLog : union.getData().svcLogs) {
                //LOGGER.info("====" + union.getClientPerfModel());
                // 在 extra字段中添加客户端日志数据 -- yllu2
                buildAudioData(audioInfo, svcLog, union.getClientPerfModel());
            }

            if (union.getMedia() != null) {
                System.out.println("union.getMedia() != null");
                // 解码保存音频文件
                String aue = audioInfo.getExtras().get("aue");
                String auf = audioInfo.getExtras().get("auf");
                if (StringUtils.isNotEmpty(aue) && StringUtils.isNotEmpty(auf)) {
                    savaAudioFile(union.getMedia().mediaData.data, aue, auf, sid);

                }
                else{
                    System.out.println("here1");
                    savaAudioFile(union.getMedia().mediaData.data, aue, auf, sid);
                }
            }
            /*
            // 按时间戳倒序
            Collections.sort(audioInfo.getData(), new Comparator<AudioData>() {
                @Override
                public int compare(AudioData o1, AudioData o2) {
                    return (int) (o2.getTimestamp() - o1.getTimestamp());
                }
            });
            */
        }
    }

    public void buildAudioData(AudioInfo audioInfo, SvcLog svcLog, String clientLog) throws Exception {
        AudioData data = new AudioData();
        data.setType(svcLog.type);
        data.setSid(svcLog.sid);
        data.setUid(svcLog.uid);
        data.setSyncid(svcLog.syncid);
        data.setTimestamp(svcLog.timestamp);
        data.setIp(svcLog.ip);
        data.setCallname(svcLog.callName);
        System.out.println("svcLog.logs.size() is: "+svcLog.logs.size());
        for (RawLog log : svcLog.logs) {
            AudioLog audioLog = new AudioLog();
            audioLog.setTimestamp(log.timestamp);
            audioLog.setLevel(log.level);
            audioLog.setDescs(log.descs);
            audioLog.setExtras(log.extras);

            Map<String, String> map = log.extras;
            Iterator<Map.Entry<String,String>> iter = map.entrySet().iterator();
            while (iter.hasNext()){
                Map.Entry<String,String> entry = iter.next();
                System.out.println(entry.getKey()+" "+entry.getValue());
            }

            // 添加客户端日志rawlog数据 -- yllu2
            audioLog.setClientPerfLog(clientLog);

            data.getLogs().add(audioLog);

            Map<String, String> extras = new HashMap<String, String>();
            extras.putAll(log.extras);
            parseExtras(extras, log.extras.get("params"));
            parseExtras(extras, log.extras.get("val"));
            parseIviir(extras, log.extras.get("IVIIR"));

            for (Map.Entry<String, String> entry : extras.entrySet()) {
                if ("finalResult".equals(entry.getKey()) && !audioInfo.getExtras().containsKey("recs")) {
                    audioInfo.getExtras().put("recs", getRecsResult(entry.getValue()));
                }
                if (AUDIO_EXTRAS.contains(entry.getKey()) && !audioInfo.getExtras().containsKey(entry.getKey())) {
                    audioInfo.getExtras().put(entry.getKey(), entry.getValue());
                }
            }
        }
        audioInfo.getData().add(data);

    }

    private void parseExtras(Map<String, String> extras, String params) {
        if (StringUtils.isNotEmpty(params)) {
            for (String keyValue : params.split(",")) {
                String[] kv = keyValue.split("=", 2);
                if (kv.length == 2) {
                    extras.put(kv[0].trim(), kv[1].trim());
                }
            }
        }
    }


    private void parseIviir(Map<String, String> extras, String iviir) {
        if (StringUtils.isEmpty(iviir)) {
            return;
        }
        JSONObject object = JSONObject.parseObject(iviir);
        if (object.containsKey("age")) {
            extras.put("age", (String) object.get("age"));
        }
        if (object.containsKey("gender")) {
            extras.put("gender", (String) object.get("gender"));
        }
    }


    private String getRecsResult(String jsonArray) {
        StringBuffer recs = new StringBuffer();
        try {
            JSONArray array = JSONArray.parseArray(jsonArray);
            for (int i = 0; i < array.size(); i++) {
                String text = array.get(i).toString();
                JSONObject json = JSONObject.parseObject(text);
                JSONArray ws = json.getJSONArray("ws");
                for (int j = 0; j < ws.size(); j++) {
                    recs.append(ws.getJSONObject(j).getJSONArray("cw").getJSONObject(0).getString("w"));
                }
            }
        } catch (Exception e) {
         //   LOGGER.error("jsonArray --- " + jsonArray, e);
            recs.append(jsonArray);
        }
        return recs.toString();
    }
}
