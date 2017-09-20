package com.genitus.channel.tracker.util.hdfsthrift;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.net.Socket;

public class ThriftConnectionFactory extends BasePoolableObjectFactory {
    private String host;
    private int port;
    private int timeout;

    public ThriftConnectionFactory(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
    }

    @Override
    public Object makeObject() throws Exception {
        TTransport tTransport = new TSocket(host, port, timeout);
        tTransport.open();
        return tTransport;
    }

    public void destroyObject(Object obj) throws Exception  {
        if (obj instanceof TTransport) {
            ((TTransport)obj).close();
        }
    }

    public boolean validateObject(Object obj) {
        if (obj instanceof TSocket) {
            TSocket tSocket = ((TSocket)obj);
            Socket socket = tSocket.getSocket();
            try {
                socket.sendUrgentData(0xff);
            } catch (Exception e) {
                return false;
            }
            if (!socket.isConnected()) {
                return false;
            }
            if (socket.isClosed()) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }


    /**
     *  No-op.
     *
     *  @param obj ignored
     */
    public void activateObject(Object obj) throws Exception {

    }

    /**
     *  No-op.
     *
     * @param obj ignored
     */
    public void passivateObject(Object obj) throws Exception {

    }
}
