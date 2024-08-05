package com.summary.simple.rpc.consumer;

import com.summary.simple.rpc.base.RpcRequest;
import com.summary.simple.rpc.consumer.codec.RpcConsumerDecoder;
import com.summary.simple.rpc.consumer.codec.RpcConsumerEncoder;
import com.summary.simple.rpc.consumer.handler.RpcConsumerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jie.luo
 * @since 2024/8/5
 */
public class RpcConsumer {

    /**
     * 创建一个线程池：大小为CPU核数
     */
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static RpcConsumerHandler rpcConsumerHandler;

    /**
     * 初始化Netty 连接
     *
     * @param host 连接地址
     * @param port 连接端口
     * @throws InterruptedException e
     */
    private static void init(String host, int port) throws InterruptedException {

        rpcConsumerHandler = new RpcConsumerHandler();

        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        pipeline.addLast(new RpcConsumerDecoder());
                        pipeline.addLast(new RpcConsumerEncoder());
                        pipeline.addLast(rpcConsumerHandler);
                    }
                });

        bootstrap.connect(host, port).sync();
    }

    public static Object proxy(String host, int port, Class<?> clazz) {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                if (rpcConsumerHandler == null) {
                    init(host, port);
                }

                // 封装向服务端发送数据
                RpcRequest rpcRequest = new RpcRequest();
                rpcRequest.setRequestId(UUID.randomUUID().toString());
                rpcRequest.setClazz(clazz);
                rpcRequest.setMethodName(method.getName());
                rpcRequest.setParameters(args);
                Class<?>[] parameterTypes = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    parameterTypes[i] = args[i].getClass();
                }
                rpcRequest.setParameterTypes(parameterTypes);

                rpcConsumerHandler.setRequest(rpcRequest);

                return EXECUTOR.submit(rpcConsumerHandler).get();
            }
        });
    }
}
