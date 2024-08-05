package com.summary.simple.rpc.test.prodvider.service;

import com.summary.simple.rpc.test.common.client.UserService;
import com.summary.simple.rpc.test.common.pojo.User;

/**
 * @author jie.luo
 * @since 2024/8/5
 */
public class UserServiceImpl implements UserService {
    @Override
    public String sayHello(String param) {
        System.out.println("sayHello-----接收到客户端参数：" + param);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "你好：" + param;
    }

    @Override
    public String saveUser(User user) {
        System.out.println("saveUser-----接收到客户端参数：" + user);
        return "SUCCESS";
    }
}
