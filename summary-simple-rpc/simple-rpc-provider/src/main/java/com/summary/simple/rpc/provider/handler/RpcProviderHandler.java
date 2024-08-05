package com.summary.simple.rpc.provider.handler;

import com.alibaba.fastjson.JSONObject;
import com.summary.simple.rpc.base.RpcRequest;
import com.summary.simple.rpc.base.RpcResponse;
import com.summary.simple.rpc.provider.service.RpcProviderContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

/**
 * rpc 服务提供者 通道处理器
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class RpcProviderHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof RpcRequest request) {
            // 获取请求类
            Class<?> clazz = request.getClazz();
            // 从容器中获取调用实现
            Object requestClass = RpcProviderContainer.CONTAINER.get(clazz);

            if (null == requestClass) {
                System.out.println("服务端无提供该接口....");
                return;
            }

            // 通过反射调用方法并获得响应结果
            Method method = requestClass.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
            Object result = method.invoke(requestClass, getParameters(request.getParameterTypes(), request.getParameters()));

            // 封装自定义返回结果数据
            RpcResponse response = new RpcResponse();
            response.setRequestId(request.getRequestId());
            response.setResult(result);

            // 向客户端发送响应数据
            ctx.writeAndFlush(response);
        }

    }

    public Object[] getParameters(Class<?>[] parameterTypes, Object[] requestArgs) throws ClassNotFoundException {
        if (null == parameterTypes) {
            return null;
        }

        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < requestArgs.length; i++) {
            Object requestArg = requestArgs[i];
            if (requestArg instanceof JSONObject) {
                args[i] = JSONObject.parseObject(requestArgs[i].toString(), parameterTypes[i]);
            } else {
                args[i] = requestArgs[i];
            }
        }
        return args;
    }
}
