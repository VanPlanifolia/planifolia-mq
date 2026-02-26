package van.planifolia.planifoliamq.core;

import van.planifolia.planifoliamq.model.DelayMessage;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 14:28
 * @description 队列消息分发器 其依赖于CustomerRegister(消费者注册表).
 * 1.系统提供了最基础的消息分发机制,该分发器会加载注册表中的所有消费者,根据dispatch传递进来的队列名称和消息体去匹配若干个消费者,若匹配到多个会同时进行消费.
 * 2.若使用者想采用其他的分发机制,想必一定会用到CustomerRegister,所以只需要重写dispatch方法即可
 */
public interface DelayMessageDispatcher {


    /**
     * 分发消息处理器,这一步是解析消息的queue以及topic,将其分发到之前我们为每个消费者创建的代理对象上面
     *
     * @param queue   队列
     * @param message 消息体
     */
    void dispatch(String queue, DelayMessage message);

    /**
     * 分发器名称
     *
     * @return 名称
     */
    String getName();
}
