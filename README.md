# simple-rpc

简版 基于Netty rpc 框架



RPC 又称远程过程调用，我们目前所知远程调用分为两种，现在服务间通信的方式基本以这两种为主

1、基于HTTP的 restful 形式的远程调用，以Spring cloud 的 openfeign 和 restTemplate为代表，采用的协议是HTTP的7层调用协议，并且协议的参数和响应序列化基本以JSON格式与XML格式为主。

2、基于TCP的狭义的RPC远程调用，以阿里的Dubbo为代表，主要通过netty来实现4层网络协议，NIO来异步传输，序列化也可以是JSON或者hessian2以及Java自带的序列化等，可以配置。



当前 simple-rpc 是以第二种的RPC远程调用来自己实现。

simple-rpc 是模仿dubbo，服务消费者和服务提供者约定接口和协议，消费者远程调用提供者，提供者返回响应数据，消费者获取响应数据。底层网络通信使用 Netty。



**主要实现思路**

1、创建一个公共的接口项目

​	创建接口以及方法，用于消费者和提供者之间的约定。

2、创建一个服务提供者

​	需要监听消费者的请求，按照约定返回数据

3、创建一个消费者

​	通过公共接口类，调用自己可使用的方法。



**具体实现思路**

* 定义请求参数与响应数据

```java
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
```

RpcRequest 请求参数包含：请求id，调用类信息、调用发放、调用参数

```java
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
```

RpcResponse 响应数据：请求id、响应数据

* 服务提供者

  1、服务提供者参数解码，响应数据编码

  2、将所有的远程调用接口类、类实现实例化后进行缓存

  ```java
  // <UserService.class,new UserServiceImpl()>
  public static Map<Class<?>, Object> CONTAINER = new HashMap<>();
  ```

  3、基于Netty，处理调用请求

  4、走请求参数中获取调用类，调用方法，通过反射调用方法并获取响应结果

  5、封装返回数据到 RpcResponse , 并将 RpcResponse 通过Netty发送到客户端

* 服务提供端

  1、服务消费者参数编码、返回数据解码

  2、消费者调用远程接口，通过代理将参数转换为 RpcRequest 对象。

  ```java
  public class RpcConsumer {
  
      /**
       * 创建一个线程池：大小为CPU核数
       */
      private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
  
      private static RpcConsumerHandler rpcConsumerHandler;
  
      /**
       * 初始化Netty 连接
       *
       * @param host 连接地址
       * @param port 连接端口
       * @throws InterruptedException e
       */
      private static void init(String host, int port) throws InterruptedException {
  
          rpcConsumerHandler = new RpcConsumerHandler();
  
          NioEventLoopGroup group = new NioEventLoopGroup();
  
          Bootstrap bootstrap = new Bootstrap();
          bootstrap.group(group)
                  .channel(NioSocketChannel.class)
                  .option(ChannelOption.TCP_NODELAY, true)
                  .handler(new ChannelInitializer<NioSocketChannel>() {
                      @Override
                      protected void initChannel(NioSocketChannel channel) throws Exception {
                          ChannelPipeline pipeline = channel.pipeline();
                          pipeline.addLast(new RpcConsumerDecoder());
                          pipeline.addLast(new RpcConsumerEncoder());
                          pipeline.addLast(rpcConsumerHandler);
                      }
                  });
  
          bootstrap.connect(host, port).sync();
      }
  
      public static Object proxy(String host, int port, Class<?> clazz) {
          return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new InvocationHandler() {
              @Override
              public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
  
                  if (rpcConsumerHandler == null) {
                      init(host, port);
                  }
  
                  // 封装向服务端发送数据
                  RpcRequest rpcRequest = new RpcRequest();
                  rpcRequest.setRequestId(UUID.randomUUID().toString());
                  rpcRequest.setClazz(clazz);
                  rpcRequest.setMethodName(method.getName());
                  rpcRequest.setParameters(args);
                  Class<?>[] parameterTypes = new Class[args.length];
                  for (int i = 0; i < args.length; i++) {
                      parameterTypes[i] = args[i].getClass();
                  }
                  rpcRequest.setParameterTypes(parameterTypes);
  
                  rpcConsumerHandler.setRequest(rpcRequest);
  
                  return EXECUTOR.submit(rpcConsumerHandler).get();
              }
          });
      }
  }
  ```

  

  3、通过多线程与 Callable ，将 RpcRequest 对象基于Netty发送到服务端，发送后线程等待（wait），待服务端响应数据后唤醒等待线程（notify），将数据返回为服务调用线程

  ```java
  public class RpcConsumerHandler extends ChannelInboundHandlerAdapter implements Callable<Object> {
  
      private ChannelHandlerContext context;
      private RpcRequest request;
      private Object result;
  
      @Override
      public void channelActive(ChannelHandlerContext ctx) throws Exception {
          this.context = ctx;
      }
  
      @Override
      public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
          if (msg instanceof RpcResponse response) {
              result = response.getResult();
          }
  
          // 获取服务端回执数据后，唤醒等待线程
          notify();
      }
  
      @Override
      public synchronized Object call() throws Exception {
          // 向服务端发送数据
          context.writeAndFlush(request);
          // 发送参数后，等待服务端回执唤醒
          wait();
          return result;
      }
  
      public RpcRequest getRequest() {
          return request;
      }
  
      public void setRequest(RpcRequest request) {
          this.request = request;
      }
  }
  ```

  

