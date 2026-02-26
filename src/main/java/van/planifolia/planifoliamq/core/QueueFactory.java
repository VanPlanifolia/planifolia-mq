

package van.planifolia.planifoliamq.core;

import lombok.extern.slf4j.Slf4j;
import van.planifolia.planifoliamq.exception.QueueIsExistsException;
import van.planifolia.planifoliamq.model.DelayMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 10:19
 * @description <br>
 * 队列工厂,我们这里构建出来了一个静态工厂用于统一管理DelayQueue.<br>
 * 1.提供了创建队列,获取队列(获取若不存在则自动创建)的API方法<br>
 * 2.提供了最基础的消息发布的API方法,可以指定队列,指定topic,指定消息分发器,指定延迟时间进行消息发布(当然我们不推荐使用者直接调用该方法,而是去使用{@link DelayQueueTemplate#send(String, String, String)})进行消息的发送<br>
 * 3.提供了多线程监听队列的方法,每个线程会监控一个队列,轮询去拉去消息实现消息队列逻辑
 */
@Slf4j
public class QueueFactory {
    /**
     * 被管理的所有的延迟队列Map
     */
    private static final Map<String, DelayQueue<DelayTaskWrapper>> delayQueueMap = new ConcurrentHashMap<>();

    /**
     * 创建一个新队列
     *
     * @param name 队列名称
     */
    public  void createQueue(String name) {
        if (delayQueueMap.get(name) != null) {
            throw new QueueIsExistsException("创建的队列已存在！");
        }
        delayQueueMap.put(name, new DelayQueue<>());
    }

    /**
     * 根据名称查找一个队列信息`
     *
     * @param name 队列名称
     * @return 查找到的队列信息
     */
    public  DelayQueue<DelayTaskWrapper> getQueue(String name) {
        return delayQueueMap.computeIfAbsent(name, k -> new DelayQueue<>());
    }

    /**
     * 发布一条任务到队列中
     *
     * @param queueName  被发布的队列名称
     * @param message    被分发的消息
     * @param dispatcher 消息分发器具
     * @param second     延迟时间
     */
    public  void offer(String queueName, DelayMessage message, DelayMessageDispatcher dispatcher, Long second) {
        getQueue(queueName).offer(new DelayTaskWrapper(message, dispatcher, second));
    }

    /**
     * 创建队列轮询任务，阻塞拉取任务并执行
     *
     * @param queueName 被操作的队列名称
     */
    public Runnable createPollingTask(String queueName) {
        return () -> {
            log.info("队列:{},消息观察者创建成功",queueName);
            DelayQueue<DelayTaskWrapper> queue = getQueue(queueName);
            while (true) {
                try {
                    DelayTaskWrapper task = queue.take();
                    task.execute(queueName);
                } catch (InterruptedException ignored) {
                    log.info("轮询拉取消息异常:{}", ignored.getMessage());
                }
            }
        };
    }
}
