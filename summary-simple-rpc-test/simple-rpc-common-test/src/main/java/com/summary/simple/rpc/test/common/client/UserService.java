package com.summary.simple.rpc.test.common.client;

import com.summary.simple.rpc.test.common.pojo.User;

/**
 * @author jie.luo
 * @since 2024/8/5
 */
public interface UserService {
    /**
     * hello 请求
     *
     * @param param 请求参数
     * @return 响应数据
     */
    String sayHello(String param);

    /**
     * 测试对象请求
     *
     * @param user User
     * @return 响应数据
     */
    String saveUser(User user);
}
