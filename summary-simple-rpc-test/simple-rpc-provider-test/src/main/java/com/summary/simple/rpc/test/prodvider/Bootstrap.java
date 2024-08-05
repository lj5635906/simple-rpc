package com.summary.simple.rpc.test.prodvider;

import com.summary.simple.rpc.provider.RpcProviderBootstrap;
import com.summary.simple.rpc.provider.service.RpcProviderContainer;
import com.summary.simple.rpc.test.common.client.UserService;
import com.summary.simple.rpc.test.prodvider.service.UserServiceImpl;

/**
 * @author jie.luo
 * @since 2024/8/5
 */
public class Bootstrap {
    public static void main(String[] args) throws InterruptedException {

        // 将提供接口放入容器
        RpcProviderContainer.CONTAINER.put(UserService.class, new UserServiceImpl());

        RpcProviderBootstrap.start("127.0.0.1", 8888);
    }


}
