package van.planifolia.planifoliamq.core;

import lombok.extern.slf4j.Slf4j;
import van.planifolia.planifoliamq.annotations.DelayConsumer;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 13:37
 * @description 消费者注册器, 这里我们要通过注解加载出来所有的消费者
 */
@Slf4j
public class CustomerRegister {
    /**
     * 这里我们维护一个Map,因为我们消费者是Method,具体的消费业务是绑定的Topic和Queue,那么我们这边要绑定的应该是Topic+Queue
     */
    private static final Map<String, List<CustomerInvoke>> customerTopicInvokeMap = new HashMap<>();

    /**
     * 注册消费者Bean里的消费者方法
     *
     * @param bean 被注册的消费者Bean
     */
    public void register(Object bean) {
        Method[] methods = bean.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(DelayConsumer.class)) {
                DelayConsumer annotation = method.getAnnotation(DelayConsumer.class);
                String queue = annotation.queue();
                String topic = annotation.topic();
                String customerKey = buildKey(queue, topic);
                customerTopicInvokeMap.computeIfAbsent(customerKey, k -> new ArrayList<>()).add(new CustomerInvoke(bean, method));
                log.info("消费者:{},注册成功!", customerKey);
            }
            log.info("所有消费者注册成功! \n目前注册表状态:");
            customerTopicInvokeMap.forEach((k, v) -> log.info("Key {},消费者数量 {},", k, v.size()));
        }
    }

    /**
     * 查询队列根据名称
     *
     * @param queue 队列名称
     * @param topic 主题
     * @return 查询结果
     */
    public List<CustomerInvoke> getInvokes(String queue, String topic) {
        return customerTopicInvokeMap.getOrDefault(buildKey(queue, topic), Collections.emptyList());
    }

    /**
     * 查询全部的invoke
     *
     * @return 查询结果
     */
    public List<CustomerInvoke> getAll() {
        List<CustomerInvoke> allQueue = new ArrayList<>();
        customerTopicInvokeMap.values().forEach(allQueue::addAll);
        return allQueue;
    }

    /**
     * 构建key
     *
     * @param queue 队列名称
     * @param topic 主题
     * @return 消费者invoke key
     */
    public String buildKey(String queue, String topic) {
        return queue + "-" + topic;
    }
}
