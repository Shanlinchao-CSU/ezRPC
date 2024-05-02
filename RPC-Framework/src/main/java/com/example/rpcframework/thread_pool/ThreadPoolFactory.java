package com.example.rpcframework.thread_pool;

import com.example.rpcframework.common.RpcRequest;
import com.example.rpcframework.common.RpcResponse;
import com.example.rpcframework.common.constant.MsgStatus;
import com.example.rpcframework.common.constant.MsgType;
import com.example.rpcframework.protocol.MsgHeader;
import com.example.rpcframework.protocol.RpcProtocol;
import com.example.rpcframework.server.ServerUtil.ServiceNameBuilder;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.reflect.FastClass;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程池工厂*/
@Slf4j
public class ThreadPoolFactory {
    // 将所有任务分为快速任务和慢速任务,该变量为慢速任务线程池
    private static ThreadPoolExecutor slowTaskPool;
    private static ThreadPoolExecutor fastTaskPool;
    // 用于存储慢速任务的标识与执行时间,再次执行该任务时,可以通过执行时间选择是否变为快速任务
    private static volatile ConcurrentHashMap<String, AtomicInteger> slowTaskMap = new ConcurrentHashMap<>();
    private static int coreNum = Runtime.getRuntime().availableProcessors();
    // 用缓存存储服务
    private static Map<String,Object> serviceMap;

    // 静态代码快,用于初始化类中静态变量
    static{
        /**
         * corePoolSize: 最小可以同时运行的线程数量
         * maximumPoolSize: 最大线程数量
         * keepAliveTime: 当线程池中的线程数量大于corePoolSize时,如果这时没有新的任务提交,核心线程外的线程会等待,直到等待的时间超过了 keepAliveTime才会被回收销毁
         * unit: keepAliveTime的时间单位
         * workQueue: 任务队列,用来储存等待执行任务的队列
         * ThreadFactory: 创建新线程时执行,这里实际上是一个ThreadFactory的实例,用匿名内部类实现接口*/
        slowTaskPool = new ThreadPoolExecutor(coreNum/2,coreNum,60L,TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000),
                r->{
                    Thread thread = new Thread(r);
                    thread.setName("slow-"+r.hashCode());
                    thread.setDaemon(true);
                    return thread;
        });

        // 快速任务线程池相比慢速,需要更多的最小、最大线程数量
        fastTaskPool = new ThreadPoolExecutor(coreNum,coreNum*2,60L,TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(1000),
                r->{
                    Thread thread = new Thread(r);
                    thread.setName("fast-"+r.hashCode());
                    thread.setDaemon(true);
                    return thread;
                });
        startCleanSlowTask();
    }

    public static void setServiceMap(Map<String,Object> map) {
        serviceMap = map;
    }

    /**
     * 启动定时任务,每隔五分钟清空slowTaskMap*/
    private static void startCleanSlowTask() {
        Executors.newSingleThreadScheduledExecutor().scheduleWithFixedDelay(()->{
            slowTaskMap.clear();
        },5,5,TimeUnit.MINUTES);
    }

    public static void submitRequest(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) {
        MsgHeader header = protocol.getHeader();
        RpcRequest request = protocol.getBody();
        String key = request.getClassName()+request.getMethodName()+request.getServiceVersion();
        ThreadPoolExecutor pool = fastTaskPool;
        if (slowTaskMap.contains(key) && slowTaskMap.get(key).intValue() >= 10) {
            pool = slowTaskPool;
        }
        pool.submit(()->{
            RpcResponse response = new RpcResponse();
            // 获取任务执行时间
            long start = System.currentTimeMillis();
            try {
                Object result = handle_request(request);
                response.setData(result);
                response.setDataClass(result == null ? null : result.getClass());
                header.setStatus((byte) MsgStatus.SUCCESS.ordinal());
            }catch (Exception e) {
                response.setException(e);
                header.setStatus((byte) MsgStatus.FAILED.ordinal());
                log.error("请求处理失败!请求ID:{},错误信息:{}",header.getRequestId(),e.toString());
            }finally {
                long total_time = System.currentTimeMillis() - start;
                // 判断慢速或快速任务
                if (total_time > 1000) {
                    // 存在则返回,不存在则插入并返回null
                    AtomicInteger timeOutCount = slowTaskMap.putIfAbsent(key, new AtomicInteger(1));
                    if (timeOutCount!=null){
                        timeOutCount.incrementAndGet();
                    }
                }
            }
            log.info("线程{}结束,服务类:{},方法:{},版本:{}",Thread.currentThread().getName(),request.getClassName(),request.getMethodName(),request.getServiceVersion());
            ctx.fireChannelRead(new RpcProtocol<RpcResponse>(header,response));
        });
    }

    private static Object handle_request(RpcRequest request) throws Exception {
        String serviceKey = ServiceNameBuilder.buildServiceName(request.getClassName(),request.getServiceVersion());
        // 获取服务
        Object serviceBean = serviceMap.get(serviceKey);

        if (serviceBean == null) {
            throw new RuntimeException("服务未找到:"+serviceKey);
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = {request.getData()};

        // 因为这里只拥有方法名,所以需要使用反射
        // 使用CGLIB提供的FastClass,高效地进行反射调用类方法
        FastClass fastClass = FastClass.create(serviceClass);
        int methodIdx = fastClass.getIndex(methodName,parameterTypes);
        return fastClass.invoke(methodIdx,serviceBean,parameters);
    }
}
