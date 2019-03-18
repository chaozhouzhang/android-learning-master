# OKHttp

## 示例
```
private void test() {
    String url = "http://wwww.baidu.com";
    OkHttpClient okHttpClient = new OkHttpClient();
    final Request request = new Request.Builder()
            .url(url)
            .build();
    Call call = okHttpClient.newCall(request);
    call.enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            Log.d(TAG, "onFailure: ");
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            Log.d(TAG, "onResponse: " + response.body().string());
        }
    });
}
```

## 客户端初始化

```
OkHttpClient okHttpClient = new OkHttpClient();
```

Builder模式：
```
public OkHttpClient() {
  this(new Builder());
}
```
```
public Builder() {
  dispatcher = new Dispatcher();
  protocols = DEFAULT_PROTOCOLS;
  connectionSpecs = DEFAULT_CONNECTION_SPECS;
  eventListenerFactory = EventListener.factory(EventListener.NONE);
  proxySelector = ProxySelector.getDefault();
  if (proxySelector == null) {
    proxySelector = new NullProxySelector();
  }
  cookieJar = CookieJar.NO_COOKIES;
  socketFactory = SocketFactory.getDefault();
  hostnameVerifier = OkHostnameVerifier.INSTANCE;
  certificatePinner = CertificatePinner.DEFAULT;
  proxyAuthenticator = Authenticator.NONE;
  authenticator = Authenticator.NONE;
  connectionPool = new ConnectionPool();
  dns = Dns.SYSTEM;
  followSslRedirects = true;
  followRedirects = true;
  retryOnConnectionFailure = true;
  callTimeout = 0;
  connectTimeout = 10_000;
  readTimeout = 10_000;
  writeTimeout = 10_000;
  pingInterval = 0;
}
```

```
OkHttpClient(Builder builder) {
  this.dispatcher = builder.dispatcher;
  this.proxy = builder.proxy;
  this.protocols = builder.protocols;
  this.connectionSpecs = builder.connectionSpecs;
  this.interceptors = Util.immutableList(builder.interceptors);
  this.networkInterceptors = Util.immutableList(builder.networkInterceptors);
  this.eventListenerFactory = builder.eventListenerFactory;
  this.proxySelector = builder.proxySelector;
  this.cookieJar = builder.cookieJar;
  this.cache = builder.cache;
  this.internalCache = builder.internalCache;
  this.socketFactory = builder.socketFactory;

  boolean isTLS = false;
  for (ConnectionSpec spec : connectionSpecs) {
    isTLS = isTLS || spec.isTls();
  }

  if (builder.sslSocketFactory != null || !isTLS) {
    this.sslSocketFactory = builder.sslSocketFactory;
    this.certificateChainCleaner = builder.certificateChainCleaner;
  } else {
    X509TrustManager trustManager = Util.platformTrustManager();
    this.sslSocketFactory = newSslSocketFactory(trustManager);
    this.certificateChainCleaner = CertificateChainCleaner.get(trustManager);
  }

  if (sslSocketFactory != null) {
    Platform.get().configureSslSocketFactory(sslSocketFactory);
  }

  this.hostnameVerifier = builder.hostnameVerifier;
  this.certificatePinner = builder.certificatePinner.withCertificateChainCleaner(
      certificateChainCleaner);
  this.proxyAuthenticator = builder.proxyAuthenticator;
  this.authenticator = builder.authenticator;
  this.connectionPool = builder.connectionPool;
  this.dns = builder.dns;
  this.followSslRedirects = builder.followSslRedirects;
  this.followRedirects = builder.followRedirects;
  this.retryOnConnectionFailure = builder.retryOnConnectionFailure;
  this.callTimeout = builder.callTimeout;
  this.connectTimeout = builder.connectTimeout;
  this.readTimeout = builder.readTimeout;
  this.writeTimeout = builder.writeTimeout;
  this.pingInterval = builder.pingInterval;

  if (interceptors.contains(null)) {
    throw new IllegalStateException("Null interceptor: " + interceptors);
  }
  if (networkInterceptors.contains(null)) {
    throw new IllegalStateException("Null network interceptor: " + networkInterceptors);
  }
}
```




调度分配器
协议
连接规格
事件监听工厂
代理选择器
cookie
socket工厂
主机名验证
代理验证器
连接池
dns
遵循ssl重定向
遵循重定向
重试连接失败
调用超时
连接超时
读取超时
写入超时
ping间隔


## 请求初始化


Request是final类，不可被继承，构造函数没有修饰符，不能在其他类中被直接实例化。



Builder模式

内部维护一个Builder
```
Builder(Request request) {
  this.url = request.url;
  this.method = request.method;
  this.body = request.body;
  this.tags = request.tags.isEmpty()
      ? Collections.emptyMap()
      : new LinkedHashMap<>(request.tags);
  this.headers = request.headers.newBuilder();
}
```
请求地址
请求方法
请求体
请求标志
请求头



## 请求地址
HttpUrl

## 请求头

Headers是final类，不可被继承，构造函数没有修饰符，不能在其他类中被直接实例化。

维护一个字符串数组，key和value依次按顺序放入。


Builder模式
维护一个Builder


## 请求体

抽象类

媒体类型


## 媒体类型


## 请求时间

HttpDate



## 创建调用


```
/**
 * Prepares the {@code request} to be executed at some point in the future.
 */
@Override public Call newCall(Request request) {
  return RealCall.newRealCall(this, request, false /* for web socket */);
}
```


```
static RealCall newRealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
  // Safely publish the Call instance to the EventListener.
  RealCall call = new RealCall(client, originalRequest, forWebSocket);
  call.eventListener = client.eventListenerFactory().create(call);
  return call;
}
```

## 真实调用


```
private RealCall(OkHttpClient client, Request originalRequest, boolean forWebSocket) {
  this.client = client;
  this.originalRequest = originalRequest;
  this.forWebSocket = forWebSocket;
  this.retryAndFollowUpInterceptor = new RetryAndFollowUpInterceptor(client);
  this.timeout = new AsyncTimeout() {
    @Override protected void timedOut() {
      cancel();
    }
  };
  this.timeout.timeout(client.callTimeoutMillis(), MILLISECONDS);
}

```

客户端
原始访问
是否用于websocket
重试和跟踪拦截器
超时处理
超时时间

## 事件监听器



## 重试和跟踪拦截器


## 执行请求

```
@Override public Response execute() throws IOException {
  synchronized (this) {
    if (executed) throw new IllegalStateException("Already Executed");
    executed = true;
  }
  captureCallStackTrace();
  timeout.enter();
  eventListener.callStart(this);
  try {
    client.dispatcher().executed(this);
    Response result = getResponseWithInterceptorChain();
    if (result == null) throw new IOException("Canceled");
    return result;
  } catch (IOException e) {
    e = timeoutExit(e);
    eventListener.callFailed(this, e);
    throw e;
  } finally {
    client.dispatcher().finished(this);
  }
}
```

如果已执行则抛出已经执行的异常，如果未执行，则标注已执行
获取指定的堆栈信息
超时管理
调用事件监听

```
client.dispatcher().executed(this);
```
```
/** Used by {@code Call#execute} to signal it is in-flight. */
synchronized void executed(RealCall call) {
  runningSyncCalls.add(call);
}
```
执行客户端的调度分配器，仅仅只是将此次调用添加到运行中的同步调用序列中


```
Response getResponseWithInterceptorChain() throws IOException {
  // Build a full stack of interceptors.
  List<Interceptor> interceptors = new ArrayList<>();
  interceptors.addAll(client.interceptors());
  interceptors.add(retryAndFollowUpInterceptor);
  interceptors.add(new BridgeInterceptor(client.cookieJar()));
  interceptors.add(new CacheInterceptor(client.internalCache()));
  interceptors.add(new ConnectInterceptor(client));
  if (!forWebSocket) {
    interceptors.addAll(client.networkInterceptors());
  }
  interceptors.add(new CallServerInterceptor(forWebSocket));

  Interceptor.Chain chain = new RealInterceptorChain(interceptors, null, null, null, 0,
      originalRequest, this, eventListener, client.connectTimeoutMillis(),
      client.readTimeoutMillis(), client.writeTimeoutMillis());

  return chain.proceed(originalRequest);
}
```
返回通过拦截器责任链处理的相应结果
1、添加客户端的拦截器
2、添加重试和追踪的拦截器
3、添加

如果结果为空，则抛出已取消的异常，如果不为空，则返回结果
如果抛出输入输出异常，则调用事件监听器的失败响应方法，并继续抛出异常
最后结束客户端的调度分配器


## 调度分配器

最大请求书
每个主机的最大请求数
空闲回调线程
线程池
准备好的异步调用
进行中的异步调用
进行中的同步调用

## 异步调用

## 同步调用

## 与线程同名的线程

## 协议
Protocol
```
http/1.0
http/1.1
spdy/3.1
h2
h2_prior_knowledge
quic
```

枚举类继承自
公共枚举类
枚举的每个对象都是该类的实例对象，通过直接调用构造函数生成
维护一个表示协议的私有不变的字符串属性
构造函数包含表示协议的字符串参数，构造函数没有修饰符修饰，不能在其他类直接实例化
重写toString()函数直接返回协议属性值
增加一个公有静态方法，通过协议的字符串名称返回协议的对象

## 地址



## Cookie

为了辨别用户身份进行session跟踪而储存在用户本地终端上的数据，通常经过加密。


session

时域，也就是用户与交互系统通信的时间间隔，Session 对象存储特定用户会话所需的属性及配置信息，会话状态仅在支持 cookie 的浏览器中保留。



名称
取值
过期时间，毫秒数时间戳
域名
路径
是否安全
是否仅支持http
是否过期
是否仅支持主机

类中维护一个Builder对象
实际逻辑放在Builder类中



## CookieJar





## BridgeInterceptor

```
Bridges from application code to network code. First it builds a network request from a user request. Then it proceeds to call the network. Finally it builds a user response from the network response.

连接应用程序代码和网络代码。
首先，它从用户请求构建网络请求。然后，它继续进行网络调用。最后，它从网络响应构建用户响应。
```

1、从用户请求构建网络请求：

添加请求头信息：

Content-Type
Content-Length
Transfer-Encoding : chunked
Host
Connection : Keep-Alive
Accept-Encoding : gzip
Cookie
User-Agent


2、继续运行



## gzip

Accept-Encoding，HTTP Header中Accept-Encoding 是浏览器发给服务器,声明浏览器支持的编码类型



## RealInterceptorChain


继续执行的处理：

如果索引大于等于拦截器的数量，则抛出断言错误。
自增调用次数。


## HTTP编码解码器codec



## 连接

Connection

## 路由

## 套接字

## 握手

## 协议

## 流分配



## 拦截器链

责任链：
获取请求
对原始请求进行处理后继续进行请求，并返回请求的结果
获取连接（只对链中的网络拦截器有用，对链中的应用拦截器总是返回空）
获取调用
获取连接超时毫秒数
获取读取超时毫秒数
获取写入超时毫秒数
获取连接超时的链
获取读取超时的链
获取写入超市的链

拦截器：
对链进行处理后返回结果

## RetryAndFollowUpInterceptor

## Dns

## 连接池
ConnectionPool





