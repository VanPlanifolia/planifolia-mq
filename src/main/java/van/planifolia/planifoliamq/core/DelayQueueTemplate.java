package van.planifolia.planifoliamq.core;

import van.planifolia.planifoliamq.model.DelayMessage;

/**
 * 消息发送模板：封装延迟/即时消息发送接口
 */
public class DelayQueueTemplate {
    private final DelayQueueDispatcher dispatcher;

    public DelayQueueTemplate(DelayQueueDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * 发送即时消息
     */
    public void send(String queue, String topic, String body) {
        DelayMessage msg = new DelayMessage(topic, body);
        QueueFactory.offer(queue, msg, dispatcher, 0L);
    }

    /**
     * 发送延迟消息
     */
    public void sendDelay(String queue, String topic, String body, long delayMillis) {
        DelayMessage msg = new DelayMessage(topic, body);
        QueueFactory.offer(queue, msg, dispatcher, delayMillis);
    }
}
