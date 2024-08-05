package com.summary.simple.rpc.provider.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务容器
 *
 * @author jie.luo
 * @since 2024/8/5
 */
public class RpcProviderContainer {
    /**
     * 服务容器
     */
    public static Map<Class<?>, Object> CONTAINER = new HashMap<>();
}
