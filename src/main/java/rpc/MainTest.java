package rpc;

import rpc.localproxyservice.RpcImporter;
import rpc.provisonservice.EchoService;
import rpc.provisonservice.Impl.EchoServiceImpl;
import rpc.publishservice.RpcExporter;

import java.net.InetSocketAddress;

/**
 * Created by liu.lei on 2016/6/22.
 */
public class MainTest {
    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //RPC服务端（本地）发布RPC服务
                    RpcExporter.exporter("localhost",8080);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
        //将本地服务发布成远程服务,供消费者调用
        RpcImporter<EchoService> importer=new RpcImporter<EchoService>();
        EchoService echo = importer.importer(EchoServiceImpl.class,new InetSocketAddress("localhost",8080));

        System.out.println(echo.echo("Are you OK?"));

    }
}
