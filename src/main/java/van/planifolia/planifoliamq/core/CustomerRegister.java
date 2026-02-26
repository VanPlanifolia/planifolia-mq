package van.planifolia.planifoliamq.core;

import lombok.extern.slf4j.Slf4j;
import van.planifolia.planifoliamq.annotations.DelayConsumer;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 13:37
 * @description <br>
 * 消费者注册表, 这里我们管理了所有消费者的注册关系,cKey ('queueName-topicName'),相同cKey的消费者会被注册到一个group中<br>
 * 1.目前该注册表只关注method,不关注class,若多个方法被相同的cKey标注,会被注册到一组.<br>
 * 2.若使用者想要修改注册逻辑则尽可重写该类的 {@link #register(Object)} 方法来修改注册逻辑<br>
 * 3.若使用者想要修改注册topic逻辑则尽可重写该类的{{@link #buildKey(String, String)}}
 */
@Slf4j
public class CustomerRegister {

    public static final String SPLIT = "-";
    /**
     * 这里我们维护一个Map,因为我们消费者是Method,具体的消费业务是绑定的Topic和Queue,那么我们这边要绑定的应该是Topic+Queue
     */
    private static final Map<String, List<CustomerInvoke>> customerTopicInvokeMap = new HashMap<>();

    /**
     * 注册消费者Bean里的消费者方法
     *
     * @param customerList 被注册的消费者Bean
     */
    public void register(List<Customer> customerList) {
        customerList.forEach(customer -> {
            Method[] methods = customer.getClass().getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(DelayConsumer.class)) {
                    DelayConsumer annotation = method.getAnnotation(DelayConsumer.class);
                    String queue = annotation.queue();
                    String topic = annotation.topic();
                    String customerKey = buildKey(queue, topic);
                    customerTopicInvokeMap.computeIfAbsent(customerKey, k -> new ArrayList<>()).add(new CustomerInvoke(customer, method));
                    log.info("消费者:{},注册成功!", customerKey);
                }
            }
            log.info("所有消费者注册成功! \n目前注册表状态:");
            customerTopicInvokeMap.forEach((k, v) -> log.info("Key {},消费者数量 {},", k, v.size()));
        });

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
     * 获取全部队列的名称
     *
     * @return 获取到的名称
     */
    public Set<String> getAllQueueName() {
        return customerTopicInvokeMap.keySet().stream().map(cKey -> cKey.split(SPLIT)[0]).collect(Collectors.toSet());
    }

    /**
     * 构建key
     *
     * @param queue 队列名称
     * @param topic 主题
     * @return 消费者invoke key
     */
    public String buildKey(String queue, String topic) {
        return queue + SPLIT + topic;
    }
}
