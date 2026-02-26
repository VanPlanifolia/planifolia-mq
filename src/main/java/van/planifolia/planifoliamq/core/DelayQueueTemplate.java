package van.planifolia.planifoliamq.core;

import lombok.extern.slf4j.Slf4j;
import van.planifolia.planifoliamq.model.DelayMessage;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 10:19
 * @description 消息队列Template<br>
 * 1.初始化的时候可以指定消息分发器,使用者可以重写消息分发器实现自己想要的分发逻辑<br>
 * 2.消息发送API,提供了普通消息与延迟消息的发送入口,若使用者不想了解具体逻辑,只管使用直接调用方法{@link #send(String, String, String)}即可
 */
@Slf4j
public class DelayQueueTemplate {

    private final DelayMessageDispatcher dispatcher;

    private final QueueFactory queueFactory;

    /**
     * 初始化Template之前需要指定消息分发器
     *
     * @param dispatcher 消息分发器
     */
    public DelayQueueTemplate(DelayMessageDispatcher dispatcher, QueueFactory queueFactory) {
        this.dispatcher = dispatcher;
        this.queueFactory = queueFactory;
        log.info("PMQ-DQ发送消息模板初始化完毕.分发策略为:{}", dispatcher.getName());
    }

    /**
     * 发送即时消息
     */
    public void send(String queue, String topic, String body) {
        DelayMessage msg = new DelayMessage(topic, body);
        queueFactory.offer(queue, msg, dispatcher, 0L);
    }

    /**
     * 发送延迟消息
     */
    public void sendDelay(String queue, String topic, String body, long delayMillis) {
        DelayMessage msg = new DelayMessage(topic, body);
        queueFactory.offer(queue, msg, dispatcher, delayMillis);
    }
}
