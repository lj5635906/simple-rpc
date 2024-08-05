package com.summary.simple.rpc.consumer.codec;

import com.alibaba.fastjson.JSON;
import com.summary.simple.rpc.base.RpcRequest;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 客户端 自定义RPC 数据编码
 * 将 RpcRequest 编码后发送服务端
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class RpcConsumerEncoder extends MessageToMessageEncoder<RpcRequest> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcRequest message, List<Object> list) throws Exception {

        if (null != message) {

            System.out.println("向服务端发送数据：" + message);

            byte[] bytes = JSON.toJSONBytes(message);

            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(bytes);

            list.add(byteBuf);

        }

    }
}
