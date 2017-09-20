package com.genitus.channel.tracker.util.hdfsthrift;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.thrift.transport.TTransport;

public class ThriftConnectionPoolFactory {
    private GenericObjectPool<TTransport> pool;

    public ThriftConnectionPoolFactory(ThriftPoolConfig config) {
        ThriftConnectionFactory factory = new ThriftConnectionFactory(config.getHost(), config.getPort(), config.getTimeout());
        pool = new GenericObjectPool<TTransport>(factory, config);
    }

    public TTransport getConnection() throws Exception {
        return (TTransport)pool.borrowObject();
    }

    public void releaseConnection(TTransport tTransport) throws Exception {
        pool.returnObject(tTransport);
    }
    public void init(){
        pool=new GenericObjectPool();
    }
}
