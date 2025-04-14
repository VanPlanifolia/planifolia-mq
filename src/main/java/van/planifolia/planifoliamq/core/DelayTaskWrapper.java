
package van.planifolia.planifoliamq.core;

import van.planifolia.planifoliamq.model.DelayMessage;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 10:21
 * @description 延迟消息包装器，实现了Delayed接口的具体延迟逻辑,以及提供了选择消息分发器的入口
 */

public class DelayTaskWrapper implements Delayed {
    /**
     * 被包装的消息体,方便获取到消息的topic信息分发操作
     */
    private final DelayMessage message;
    /**
     * 消息分发器,要采用那种方式的消息分发机制
     */
    private final DelayQueueDispatcher dispatcher;
    /**
     * 什么时候去执行
     */
    private final long runAt;

    /**
     * 构造器，传入参数为到期后要做的事情，以及要延迟多长时间
     *
     * @param dispatcher  消息分发器
     * @param timeUnit    入参时间格式
     * @param delayMillis 延迟时间
     */
    public DelayTaskWrapper(DelayMessage message, DelayQueueDispatcher dispatcher, long delayMillis, TimeUnit timeUnit) {
        this.message = message;
        this.dispatcher = dispatcher;
        this.runAt = System.currentTimeMillis() + timeUnit.toMillis(delayMillis);
    }

    /**
     * 构造器，传入参数为到期后要做的事情，以及要延迟多长时间
     *
     * @param dispatcher  消息分发器
     * @param delayMillis 延迟时间
     */
    public DelayTaskWrapper(DelayMessage message, DelayQueueDispatcher dispatcher, long delayMillis) {
        this.message = message;
        this.dispatcher = dispatcher;
        this.runAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(delayMillis);
    }

    /**
     * Returns the remaining delay associated with this object, in the
     * given time unit.
     *
     * @param unit the time unit
     * @return the remaining delay; zero or negative values indicate
     * that the delay has already elapsed
     */
    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(runAt - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Delayed o) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
    }

    /**
     * 调用则执行消息业务
     */
    public void execute(String queueName) {
        dispatcher.dispatch(queueName, message);
    }
}
