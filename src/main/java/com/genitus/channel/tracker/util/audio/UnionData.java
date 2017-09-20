package com.genitus.channel.tracker.util.audio;
import org.genitus.karyo.model.data.SessionData;
import org.genitus.karyo.model.data.SessionMedia;
public class UnionData {
    private SessionData data;

    private SessionMedia media;

    private String clientPerfModel;

    public SessionData getData() {
        return data;
    }

    public void setData(SessionData data) {
        this.data = data;
    }

    public SessionMedia getMedia() {
        return media;
    }

    public void setMedia(SessionMedia media) {
        this.media = media;
    }

    public String getClientPerfModel() {
        return clientPerfModel;
    }

    public void setClientPerfModel(String clientModel) {
        this.clientPerfModel = clientModel;
    }
}
