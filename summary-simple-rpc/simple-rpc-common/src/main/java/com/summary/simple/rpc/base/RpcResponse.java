package com.summary.simple.rpc.base;

import lombok.Data;

/**
 * RPC响应数据
 *
 * @author jie.luo
 * @since 2024/8/5
 */
@Data
public class RpcResponse {
    /**
     * 请求ID
     */
    private String requestId;
    /**
     * 响应数据
     */
    private Object result;
}
