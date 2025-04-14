package van.planifolia.planifoliamq.core;

import van.planifolia.planifoliamq.model.DelayMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 11:56
 * @description 消费者执行代理类, 代理了消费者的消费动作, 统一入口
 */
public class CustomerInvoke {
    /**
     * 是那个Object
     */
    private final Object bean;
    /**
     * 是Object里的那个method
     */
    private final Method method;

    /**
     * 构造器,需要吧每个customer method 注册成代理对象
     *
     * @param bean   被注册的bean对象
     * @param method 被注册的方法 object和method 配合找到具体要被代理的对象
     */
    public CustomerInvoke(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    /**
     * 代理调用消费消息
     *
     * @param delayMessage 被消费的消息
     */
    public void invoke(DelayMessage delayMessage) {
        try {
            method.invoke(bean, delayMessage);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
