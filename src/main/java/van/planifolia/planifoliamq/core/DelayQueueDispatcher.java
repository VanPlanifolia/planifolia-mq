package van.planifolia.planifoliamq.core;

import lombok.extern.slf4j.Slf4j;
import van.planifolia.planifoliamq.model.DelayMessage;

import java.util.List;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 14:28
 * @description 队列消息分发器
 */
@Slf4j
public class DelayQueueDispatcher {

    private final CustomerRegister customerRegister;

    /**
     * 构造方法,消息分发器在构造之前必须要传递进来customerRegister
     *
     * @param customerRegister 消费者注册器
     */
    public DelayQueueDispatcher(CustomerRegister customerRegister) {
        this.customerRegister = customerRegister;
    }

    /**
     * 分发消息处理器,这一步是解析消息的queue以及topic,将其分发到之前我们为每个消费者创建的代理对象上面
     *
     * @param queue   队列
     * @param message 消息体
     */
    public void dispatch(String queue, DelayMessage message) {
        List<CustomerInvoke> invokeList = customerRegister.getInvokes(queue, message.getTopic());
        if (invokeList.isEmpty()) {
            log.info("消息customerKey:{},没有对应的处理器默认被丢弃!", customerRegister.buildKey(queue, message.getTopic()));
            return;
        }
        invokeList.forEach(invoker -> invoker.invoke(message));
    }
}
