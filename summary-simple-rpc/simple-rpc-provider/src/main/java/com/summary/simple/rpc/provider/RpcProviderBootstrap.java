package com.summary.simple.rpc.provider;

import com.summary.simple.rpc.provider.codec.RpcProviderDecoder;
import com.summary.simple.rpc.provider.codec.RpcProviderEncoder;
import com.summary.simple.rpc.provider.handler.RpcProviderHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author jie.luo
 * @since 2024/8/5
 */
public class RpcProviderBootstrap {

    public static void start(String host, int port) throws InterruptedException {
        // 创建两个线程池
        // 处理连接
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 处理读写操作
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        // 获取管道
                        ChannelPipeline pipeline = channel.pipeline();
                        // 添加数据编码解码
                        pipeline.addLast(new RpcProviderDecoder());
                        pipeline.addLast(new RpcProviderEncoder());
                        // 添加 RpcProviderHandler
                        pipeline.addLast(new RpcProviderHandler());
                    }
                });

        // 绑定地址、端口
        serverBootstrap.bind(host, port).sync();

        System.out.println("server started");
    }
}
