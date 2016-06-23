package rpc.provisonservice;

/**
 * Created by liu.lei on 2016/6/22.
 * 服务提供者，运行在服务端，负责提供服务接口定义
 */
public interface EchoService {
  String echo(String ping);
}
