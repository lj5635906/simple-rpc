package com.summary.simple.rpc.base;

import lombok.Data;

import java.util.Arrays;

/**
 * RPC请求参数基础类
 *
 * @author jie.luo
 * @since 2024/8/5
 */
@Data
public class RpcRequest {
    /**
     * 请求ID
     */
    private String requestId;
    /**
     * 请求类
     */
    private Class<?> clazz;
    /**
     * 请求方法名
     */
    private String methodName;
    /**
     * 请求参数类型集合
     */
    private Class<?>[] parameterTypes;
    /**
     * 请求参数集合
     */
    private Object[] parameters;
}
