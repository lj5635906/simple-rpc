package com.summary.simple.rpc.test.consumer;

import com.summary.simple.rpc.consumer.RpcConsumer;
import com.summary.simple.rpc.test.common.client.UserService;
import com.summary.simple.rpc.test.common.pojo.User;

/**
 * @author jie.luo
 * @since 2024/8/5
 */
public class RpcConsumerBootstrap {
    public static void main(String[] args) throws InterruptedException {

        UserService userService = (UserService) RpcConsumer.proxy("127.0.0.1", 8888, UserService.class);

        while(true){
            String s = userService.sayHello("summary");
            System.out.println(s);

            User user = new User();
            String result = userService.saveUser(user);
            System.out.println("save user : " + result);

            Thread.sleep(2000);
        }

    }
}
