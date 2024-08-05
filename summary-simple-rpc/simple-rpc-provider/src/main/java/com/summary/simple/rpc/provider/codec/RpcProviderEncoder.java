package com.summary.simple.rpc.provider.codec;

import com.alibaba.fastjson.JSON;
import com.summary.simple.rpc.base.RpcResponse;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * 客户端 自定义RPC 数据编码
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class RpcProviderEncoder extends MessageToMessageEncoder<RpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcResponse response, List<Object> list) throws Exception {
        if (null != response) {

            System.out.println("向客户端发送数据：" + response);

            byte[] bytes = JSON.toJSONBytes(response);

            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(bytes);

            list.add(byteBuf);
        }
    }
}
