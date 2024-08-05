package com.summary.simple.rpc.consumer.handler;

import com.summary.simple.rpc.base.RpcRequest;
import com.summary.simple.rpc.base.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

/**
 * 客户端处理器
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class RpcConsumerHandler extends ChannelInboundHandlerAdapter implements Callable<Object> {

    private ChannelHandlerContext context;
    private RpcRequest request;
    private Object result;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.context = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcResponse response) {
            result = response.getResult();
        }

        // 获取服务端回执数据后，唤醒等待线程
        notify();
    }

    @Override
    public synchronized Object call() throws Exception {
        // 向服务端发送数据
        context.writeAndFlush(request);
        // 发送参数后，等待服务端回执唤醒
        wait();
        return result;
    }

    public RpcRequest getRequest() {
        return request;
    }

    public void setRequest(RpcRequest request) {
        this.request = request;
    }
}
