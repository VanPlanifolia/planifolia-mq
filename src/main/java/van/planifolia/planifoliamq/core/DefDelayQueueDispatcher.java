package van.planifolia.planifoliamq.core;

import lombok.extern.slf4j.Slf4j;
import van.planifolia.planifoliamq.model.DelayMessage;

import java.util.List;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 14:28
 * @description 缺省的队列消息分发器 其依赖于CustomerRegister(消费者注册表).
 * 1.系统提供了最基础的消息分发机制,该分发器会加载注册表中的所有消费者,根据dispatch传递进来的队列名称和消息体去匹配若干个消费者,若匹配到多个会同时进行消费.
 * 2.若使用者想采用其他的分发机制,想必一定会用到CustomerRegister,所以只需要重写dispatch方法即可
 */
@Slf4j
public class DefDelayQueueDispatcher implements DelayMessageDispatcher {

    private final CustomerRegister customerRegister;

    /**
     * 构造方法,消息分发器在构造之前必须要传递进来customerRegister
     *
     * @param customerRegister 消费者注册器
     */
    public DefDelayQueueDispatcher(CustomerRegister customerRegister) {
        this.customerRegister = customerRegister;
    }


    /**
     * 分发消息处理器,这一步是解析消息的queue以及topic,将其分发到之前我们为每个消费者创建的代理对象上面
     *
     * @param queue   队列
     * @param message 消息体
     */
    @Override
    public void dispatch(String queue, DelayMessage message) {
        List<CustomerInvoke> invokeList = customerRegister.getInvokes(queue, message.getTopic());
        if (invokeList.isEmpty()) {
            log.info("消息customerKey:{},没有对应的处理器默认被丢弃!", customerRegister.buildKey(queue, message.getTopic()));
            return;
        }
        invokeList.forEach(invoker -> invoker.invoke(message));
    }

    /**
     * 分发器名称
     *
     * @return 名称
     */
    @Override
    public String getName() {
        return "EVERY_CUSTOMER";
    }
}
