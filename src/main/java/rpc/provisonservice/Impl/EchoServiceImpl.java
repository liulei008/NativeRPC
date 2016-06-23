package rpc.provisonservice.Impl;

import rpc.provisonservice.EchoService;

/**
 * Created by liu.lei on 2016/6/22.
 * 服务提供者，运行在服务端，负责提供服务实现类
 */
public class EchoServiceImpl implements EchoService{

    @Override
    public String echo(String ping) {
        return ping!=null?ping+"-->I am OK":"I am not OK";
    }
}
