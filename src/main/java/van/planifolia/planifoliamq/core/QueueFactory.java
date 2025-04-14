/**
 * @author Van.Planifolia
 * @create 2025/4/14 10:19
 * @description
 */

package van.planifolia.planifoliamq.core;

import lombok.extern.slf4j.Slf4j;
import van.planifolia.planifoliamq.exception.QueueIsExistsException;
import van.planifolia.planifoliamq.model.DelayMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;

@Slf4j
public class QueueFactory {
    /**
     * 被管理的所有的延迟队列Map
     */
    private static final Map<String, DelayQueue<DelayTaskWrapper>> delayQueueMap = new HashMap<>();

    /**
     * 创建一个新队列
     *
     * @param name 队列名称
     */
    public static void createQueue(String name) {
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
    public static DelayQueue<DelayTaskWrapper> getQueue(String name) {
        return delayQueueMap.computeIfAbsent(name, k -> new DelayQueue<>());
    }

    /**
     * 发布一条任务到队列中
     *
     * @param queueName 被发布的队列名称
     * @param message 被分发的消息
     * @param dispatcher 消息分发器具
     * @param second    延迟时间
     */
    public static void offer(String queueName, DelayMessage message, DelayQueueDispatcher dispatcher, Long second) {
        getQueue(queueName).offer(new DelayTaskWrapper(message, dispatcher, second));
    }

    /**
     * 创建队列轮询任务，阻塞拉取任务并执行
     *
     * @param queueName 被操作的队列名称
     * @param registry  消费者注册表
     */
    public static Runnable createPollingTask(String queueName, CustomerRegister registry) {
        return () -> {
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
