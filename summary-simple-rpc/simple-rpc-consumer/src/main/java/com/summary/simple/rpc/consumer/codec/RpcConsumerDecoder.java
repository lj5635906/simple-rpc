package com.summary.simple.rpc.consumer.codec;

import com.alibaba.fastjson.JSON;
import com.summary.simple.rpc.base.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 客户端 自定义RPC 数据解码
 * 接收服务端数据并解码
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class RpcConsumerDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf message, List<Object> list) throws Exception {

        // 数据长度
        int msgLen = message.readableBytes();
        byte[] bytes = new byte[msgLen];
        message.readBytes(bytes);

        RpcResponse response = JSON.parseObject(bytes, RpcResponse.class);

        System.out.println("接收服务端数据：" + response);

        list.add(response);
    }
}
