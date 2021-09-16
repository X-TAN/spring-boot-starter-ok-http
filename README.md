# HttpClient

## 目的

> 1.实现去除繁重的请求构建，着重关注点放于出参入参
> 2.实现更为方便的基于 **`spring`** 的统一  **`api-bean`** 管理机制
> 3.基于第一二点，从而达到规范第三方请求的愿景
> 4.因为是接口,因此拥有着大量的空间给足第三方接口充分的 **接口说明**

## 版本

> **`1.0.0`**

## 注解

### HttpClient
>##### 简介
>
>``` ceylon
>HttpClient 作用域只允许在接口类上应用 使用后将一个接口类注册为 HttpClient 类, 并注册到 Spring 容器中
>```
>##### 参数
>
>| 名称  | 必传 |                             描述                             |
>| :---: | :--: | :----------------------------------------------------------: |
>| value | `N`  | 请求地址 `host` ，不传入时默认取方法上的地址进行访问。<br>如果传入了该参数，则在请求方法上请尽量使用地址映射。实现 `host + path` 的手段<br>如果该参数不为空，并且调用方法的地址也为请求地址全路径，则优先使用方法的请求地址 |
>| name  | `N`  | 注册到 `spring` 容器中的自定义 `bean` 名称，如果未传入，则默认为 `className ` 作为 `bean` 名称 |
>
>##### 示例
>
>`1`
>
>```java
>@HttpClient
>public interface TestClient {
>    
>@Get("localhost:8080/test")
>String test(@Param("id") Integer id);
>    
>}
>```
>`2`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Get("test")
>String test(@Param("id") Integer id);
>    
>}
>```
>`3`
>
>```java
>@HttpClient(value = "localhost:8080", name = "testClient")
>public interface TestClient {
>    
>@Get("test")
>String test(@Param("id") Integer id);
>    
>}
>```


### EnableHttpClients
>##### 简介
>
>``` ceylon
>EnableHttpClients 作用域只允许在 Spring 启动类上应用, 使用后将开启 HttpClient 的自动注册功能,从而在项目的启动过程中,将对应包下被 HttpClient 注解所标识的接口类注册到 Spring 的容器中
>```
>##### 参数
>
>|     名称     | 必传 |                             描述                             |
>| :----------: | :--: | :----------------------------------------------------------: |
>|    value     | `N`  |       与 `basePackages` 相呼应,两个参数代表同样的作用        |
>| basePackages | `N`  | 该参数代表扫描 `HttpClient` 类并注册成 `spring` 的 `bean` 指定的包路径集合<br>可指定多个<br>如果未传入该包名,则默认扫描Application所在目录 |
>
>##### 示例
>
>```java
>@EnableHttpClients("com.xingyun.linkiebuy.*")
>public class Application {
>    
>public static void main(String[] args) {SpringApplication.run(Application.class, args);}
>    
>}
>```

### Headers
>##### 简介
>
>``` ceylon
>Headers 作用域在 Class，Method，Parameter 上均可使用，主要为 Http 提供自定义请求头，具备动静双态
>```
>##### 参数
>
>| 名称  | 必传 |                             描述                             |
>| :---: | :--: | :----------------------------------------------------------: |
>| value | `N`  | 当该注解被声明在类或者方法上时则为静态请求头定义方式<br>输入为**字符串数组**,可定义单个或者多个<br>使用`:`来进行请求头 `name` 与 `value` 的切割<br>当该注解被应用再请求参数上时,无需指定注解参数值<br>但是必须指定请求参数的类型为HttpHeaders<br>通过HttpHeaders进行动态构建请求头 |
>
>##### 示例
>
>静态 - Class
>```java
>@HttpClient("localhost:8080")
>@Headers({"version:1.0.0", "author:XOptional-TAN"})
>public interface TestClient {
>    
>}
>```
>静态 - Method
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Get("test")
>@Headers({"version:1.0.0", "author:XOptional-TAN"})
>String test(@Param("id") Integer id);
>    
>}
>```
>动态 - Parameter
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Patch(value = "test", contentType = HttpContentType.MULTIPART_FILE)
>String test(@Headers HttpHeaders headers,
>                  @Param("id") Integer id,
>                  @Param("file") byte[] file,
>                  @Param("file1") File file1,
>                  @Param("file2") InputStream file2);
>    
>}
>```

### Get
>##### 简介
>
>``` ceylon
>Get 作用域只允许在 HttpClient 接口类中的 Method 上应用，代表该方法将作为 GET 请求方式进行 Http 请求
>```
>##### 参数
>
>|    名称     | 必传 |                             描述                             |
>| :---------: | :--: | :----------------------------------------------------------: |
>|    value    | `Y`  | 请求 `path` ,也可为请求全路径<br>当该值只声明了 `path` 时,接口类上的 `HttpClient` 注解必须声明 `host` 否则会抛出 `HttpClientException` 未找到对应的 `host`<br>当该值声明了全地址路径,并且接口类上同样定义了 `host` 会以当前方法全路径作为最终访问地址 |
>| contentType | `N`  |  请求内容类型<br>如果没有特殊的请求内容格式,无需设置该参数   |
>
>##### 示例
>
>`host + path`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Get("test")
>String test(@Param("id") Integer id);
>    
>}
>```
>`url`
>```java
>@HttpClient
>public interface TestClient {
>    
>@Get("localhost:8080/test")
>String test(@Param("id") Integer id);
>    
>}
>```

### Delete
>##### 简介
>
>``` ceylon
>Delete 作用域只允许在 HttpClient 接口类中的 Method 上应用，代表该方法将作为 DELETE 请求方式进行 Http 请求
>```
>##### 参数
>
>|    名称     | 必传 |                             描述                             |
>| :---------: | :--: | :----------------------------------------------------------: |
>|    value    | `Y`  | 请求 `path` ,也可为请求全路径<br>当该值只声明了 `path` 时,接口类上的 `HttpClient` 注解必须声明 `host` 否则会抛出 `HttpClientException` 未找到对应的 `host`<br>当该值声明了全地址路径,并且接口类上同样定义了 `host` 会以当前方法全路径作为最终访问地址 |
>| contentType | `N`  |  请求内容类型<br>如果没有特殊的请求内容格式,无需设置该参数   |
>
>##### 示例
>
>`host + path`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Delete("test")
>String test(@Param("id") Integer id);
>    
>}
>```
>`url`
>```java
>@HttpClient
>public interface TestClient {
>    
>@Delete("localhost:8080/test")
>String test(@Param("id") Integer id);
>    
>}
>```

### Post
>##### 简介
>
>``` ceylon
>Post 作用域只允许在 HttpClient 接口类中的 Method 上应用，代表该方法将作为 POST 请求方式进行 Http 请求
>```
>##### 参数
>
>|    名称     | 必传 |                             描述                             |
>| :---------: | :--: | :----------------------------------------------------------: |
>|    value    | `Y`  | 请求 `path` ,也可为请求全路径<br>当该值只声明了 `path` 时,接口类上的 `HttpClient` 注解必须声明 `host` 否则会抛出 `HttpClientException` 未找到对应的 `host`<br>当该值声明了全地址路径,并且接口类上同样定义了 `host` 会以当前方法全路径作为最终访问地址 |
>| contentType | `N`  | 请求内容类型<br>如果没有特殊的请求内容格式,无需设置该参数<br>如果需要进行多文件上传,值则需要设置为 `multipart/form-data` |
>
>##### 示例
>
>`form-body`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Post(value = "test")
>String test(@Param("age") Integer age, @Param("name") String name, @Param("sex") String sex);
>    
>}
>```
>`multipart`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Post(value = "test", contentType = HttpContentType.MULTIPART_FILE)
>String test(@Param("id") Integer id,
>           @Param("file") byte[] file,
>           @Param("file1") File file1,
>           @Param("file2") InputStream file2);
>    
>}
>```
>`json-body`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Post(value = "test")
>String test(@JSONBody Object obj);
>    
>}
>```
>`file-body`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Post(value = "test")
>String test(@FileBody byte[] file);
>    
>}
>```

### Put
>##### 简介
>
>``` ceylon
>Put 作用域只允许在 HttpClient 接口类中的 Method 上应用，代表该方法将作为 PUT 请求方式进行 Http 请求
>```
>##### 参数
>
>|    名称     | 必传 |                             描述                             |
>| :---------: | :--: | :----------------------------------------------------------: |
>|    value    | `Y`  | 请求 `path` ,也可为请求全路径<br>当该值只声明了 `path` 时,接口类上的 `HttpClient` 注解必须声明 `host` 否则会抛出 `HttpClientException` 未找到对应的 `host`<br>当该值声明了全地址路径,并且接口类上同样定义了 `host` 会以当前方法全路径作为最终访问地址 |
>| contentType | `N`  | 请求内容类型<br>如果没有特殊的请求内容格式,无需设置该参数<br>如果需要进行多文件上传,值则需要设置为 `multipart/form-data` |
>
>##### 示例
>
>`form-body`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Put(value = "test")
>String test(@Param("age") Integer age, @Param("name") String name, @Param("sex") String sex);
>    
>}
>```
>`multipart`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Put(value = "test", contentType = HttpContentType.MULTIPART_FILE)
>String test(@Param("id") Integer id,
>     @Param("file") byte[] file,
>     @Param("file1") File file1,
>     @Param("file2") InputStream file2);
>    
>}
>```
>`json-body`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Put(value = "test")
>String test(@JSONBody Object obj);
>    
>}
>```
>`file-body`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Put(value = "test")
>String test(@FileBody byte[] file);
>    
>}
>```

### Patch
>##### 简介
>
>``` ceylon
>Patch 作用域只允许在 HttpClient 接口类中的 Method 上应用，代表该方法将作为 PATCH 请求方式进行 Http 请求
>```
>##### 参数
>
>|    名称     | 必传 |                             描述                             |
>| :---------: | :--: | :----------------------------------------------------------: |
>|    value    | `Y`  | 请求 `path` ,也可为请求全路径<br>当该值只声明了 `path` 时,接口类上的 `HttpClient` 注解必须声明 `host` 否则会抛出 `HttpClientException` 未找到对应的 `host`<br>当该值声明了全地址路径,并且接口类上同样定义了 `host` 会以当前方法全路径作为最终访问地址 |
>| contentType | `N`  | 请求内容类型<br>如果没有特殊的请求内容格式,无需设置该参数<br>如果需要进行多文件上传,值则需要设置为 `multipart/form-data` |
>
>##### 示例
>
>`form-body`
>
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Put(value = "test")
>String test(@Param("age") Integer age, @Param("name") String name, @Param("sex") String sex);
>    
>}
>```
>`multipart`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Put(value = "test", contentType = HttpContentType.MULTIPART_FILE)
>String test(@Param("id") Integer id,
>@Param("file") byte[] file,
>@Param("file1") File file1,
>@Param("file2") InputStream file2);
>    
>}
>```
>`json-body`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Put(value = "test")
>String test(@JSONBody Object obj);
>    
>}
>```
>`file-body`
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Put(value = "test")
>String test(@FileBody byte[] file);
>    
>}
>```

### Param
>##### 简介
>
>``` ceylon
>Param 作用域只允许在 HttpClient 接口类中 Method 的 Paramter 上应用
>```
>##### 参数
>
>| 名称  | 必传 |                        描述                         |
>| :---: | :--: | :-------------------------------------------------: |
>| value | `Y`  | 用于表单内容请求时,使用该注解声明参数的实际请求名称 |
>
>##### 示例
>
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Get("test")
>String test(@Param("id") Integer id);
>    
>}
>```

### PathParam
>##### 简介
>
>``` ceylon
>PathParam 作用域只允许在 HttpClient 接口类中 Method 的 Paramter 上应用
>```
>##### 参数
>
>| 名称  | 必传 |                             描述                             |
>| :---: | :--: | :----------------------------------------------------------: |
>| value | `Y`  | 用于动态地址请求时,使用该注解声明参数的实际请求对应的动态地址名称 |
>
>##### 示例
>
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Get("test/{id}/{detailId}")
>String get(@PathParam("id") Integer id, @PathParam("detailId") Integer detailId);
>    
>}
>```

### JSONBody
>##### 简介
>
>``` ceylon
>JSONBody 作用域只允许在 HttpClient 接口类中 Method 的 Paramter 上应用
>```
>##### 参数
>
>| 名称  | 必传 |                          描述                           |
>| :---: | :--: | :-----------------------------------------------------: |
>| value | `Y`  | 用于 `JSON` 内容请求时,使用该注解声明参数为 `JSON` 实体 |
>
>##### 示例
>
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Post(value = "test")
>TestModel postJson(@JSONBody Object obj);
>    
>}
>```

### FileBody
>##### 简介
>
>``` ceylon
>FileBody 作用域只允许在 HttpClient 接口类中 Method 的 Paramter 上应用
>```
>##### 参数
>
>| 名称  | 必传 |                          描述                          |
>| :---: | :--: | :----------------------------------------------------: |
>| value | `Y`  | 用于 `binary` 内容请求时,使用该注解声明参数为 `binary` |
>
>##### 示例
>
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Put(value = "test", contentType = HttpContentType.FILE)
>String putFile(@FileBody byte[] file);
>    
>}
>```

## 扩展

### 动态地址与请求方式
>
>##### 示例
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>    
>@Put(value = "test/{id}", contentType = HttpContentType.FILE)
>String test(@PathParam("id") Integer id, @FileBody byte[] file);
> 
>@Post(value = "test/{id}", contentType = HttpContentType.FILE)
>String test(@PathParam("id") Integer id, @JSONBody Object obj);
>    
>}
>```
### 文件流类型
>
>```coffeescript
>现支持 JAVA 中 File InputStream byte[] 三种流数据传输类型
> ```
>
>##### 示例
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
> 
>@Post(value = "test", contentType = HttpContentType.MULTIPART_FILE)
>String test(@Param("id") Integer id,
>         @Param("file") byte[] file,
>         @Param("file1") File file1,
>         @Param("file2") InputStream file2);
> 
>@Put(value = "test/{id}", contentType = HttpContentType.FILE)
>String test(@PathParam("id") Integer id, @FileBody InputStream file);
>}
>```
### 请求头应用先后顺序
>
>```ceylon
>下面示例中
>假设参数 Headers 中传入的 Version 为 1.0.2 则最终上送请求的请求头中 Version 为 1.0.2
>假设参数 Headers 中未传入 Version 则最终上送请求的请求头中  Version 为 1.0.1
> 
>优先级顺序 Paramter 的优先级最高
>Paramter -> Method -> Class
>```
>
>##### 示例
>```java
>@HttpClient("localhost:8080")
>@Headers("version:1.0.0")
>public interface TestClient {
> 
>@Put(value = "test/{id}", contentType = HttpContentType.FILE)
>@Headers("version:1.0.1")
>String test(@Headers HttpHeaders headers, @PathParam("id") Integer id, @FileBody InputStream file);
>     
>}
>```

### HTTP  请求前缀自动补充

>
>```ceylon
>当 HttpClient 或 Get Post Delete Put Patch 请求方式注解中如果填写的 Host 或 地址 没有填写请求前缀，在请求时会自动补充前缀： http://
> 
>请尽量补全请求前缀，可能你需要的是 https://
>```
>
>##### 示例
>```java
> @HttpClient("localhost:8080")
>@Headers("version:1.0.0")
>public interface TestClient {
>
>@Put(value = "test/{id}", contentType = HttpContentType.FILE)
>@Headers("version:1.0.1")
>String test(@Headers HttpHeaders headers, @PathParam("id") Integer id,
>          @FileBody InputStream file);
> 
>@Put(value = "localhost:8080/test/{id}", contentType = HttpContentType.FILE)
>@Headers("version:1.0.1")
>String test2(@Headers HttpHeaders headers, @PathParam("id") Integer id,
>          @FileBody InputStream file);
>     
>}
>```

### 请求  HOST 冲突问题

>
>```ceylon
>当 HttpClient 填写的 Host 为 localhost:8080
>当在上面标记的 HttpClient 接口类中的 Get Post Delete Put Patch 方法声明的地址为 localhost:8081
>最终请求会以 方法所声明的地址 作为 最终请求地址
>```
>
>##### 示例
>```java
>@HttpClient("localhost:8080")
>public interface TestClient {
>
>@Get("localhost:8081/test")
>String test(@Param("id") Integer id);
>
>}
>```

