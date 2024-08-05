package com.summary.simple.rpc.provider.codec;

import com.alibaba.fastjson.JSONObject;
import com.summary.simple.rpc.base.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 服务端 自定义RPC 数据解码
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class RpcProviderDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf message, List<Object> list) throws Exception {
        // 数据长度
        int msgLen = message.readableBytes();
        byte[] bytes = new byte[msgLen];
        message.readBytes(bytes);

        System.out.println("接收服务端数据-JSON ：" + new String(bytes, "UTF-8"));
        RpcRequest nettyRpcRequest = JSONObject.parseObject(bytes, RpcRequest.class);
        System.out.println("接收客户端数据-对象 ：" + nettyRpcRequest);

        list.add(nettyRpcRequest);
    }
}
