
package van.planifolia.planifoliamq.core;

import van.planifolia.planifoliamq.model.DelayMessage;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 10:21
 * @description 延迟消息包装器，实现了Delayed接口的具体延迟逻辑,以及自动的根据队列名称与消息主题进行分发操作
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
        return unit.toSeconds(runAt - System.currentTimeMillis());
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     *
     * <p>The implementor must ensure {@link Integer#signum
     * signum}{@code (x.compareTo(y)) == -signum(y.compareTo(x))} for
     * all {@code x} and {@code y}.  (This implies that {@code
     * x.compareTo(y)} must throw an exception if and only if {@code
     * y.compareTo(x)} throws an exception.)
     *
     * <p>The implementor must also ensure that the relation is transitive:
     * {@code (x.compareTo(y) > 0 && y.compareTo(z) > 0)} implies
     * {@code x.compareTo(z) > 0}.
     *
     * <p>Finally, the implementor must ensure that {@code
     * x.compareTo(y)==0} implies that {@code signum(x.compareTo(z))
     * == signum(y.compareTo(z))}, for all {@code z}.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     * @apiNote It is strongly recommended, but <i>not</i> strictly required that
     * {@code (x.compareTo(y)==0) == (x.equals(y))}.  Generally speaking, any
     * class that implements the {@code Comparable} interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
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
