package rpc.publishservice;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by liu.lei on 2016/6/22.
 * 服务发布者，运行在RPC服务端，负责将本地服务发布成远程服务，供其他消费者调用。
 */
public class RpcExporter {
    static Executor executor= Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static void exporter(String hostName,int port)throws Exception{
        ServerSocket server=new ServerSocket();
        server.bind(new InetSocketAddress(hostName,port));
        try{
               while (true){
                   //监听客户端的TCP连接，接到连接后，将其封装成Task，由线程池执行
                   executor.execute(new ExporterTask(server.accept()));
               }
        }finally {
            server.close();
        }
    }

    private static class ExporterTask implements Runnable{
        Socket client=null;
        public ExporterTask(Socket client){
            this.client=client;
        }

        @Override
        public void run() {
            ObjectInputStream input=null;
            ObjectOutputStream output=null;
            try {
                input=new ObjectInputStream(client.getInputStream());
                //将客户端发送码流反序列化成对象
                String interfaceName=input.readUTF();
                //反射调用服务实现者
                Class<?> service=Class.forName(interfaceName);

                String methodName=input.readUTF();
                Class<?>[] parameterTypes=(Class<?>[])input.readObject();
                Object[] arguments=(Object[])input.readObject();
                Method method=service.getMethod(methodName,parameterTypes);
                Object result=method.invoke(service.newInstance(),arguments);
                //将执行结果对象反序列化，通过socket发送给客户端
                output=new ObjectOutputStream(client.getOutputStream());
                output.writeObject(result);

            }catch (Exception e){
                e.printStackTrace();
            }finally {
                //远程服务调用完成之后，释放Soket等连接资源，防止句柄泄漏
                if(output!=null){
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(input!=null){
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(client!=null){
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
}
