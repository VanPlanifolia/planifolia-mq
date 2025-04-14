package van.planifolia.planifoliamq.core;

import van.planifolia.planifoliamq.model.DelayMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 11:56
 * @description <br>
 * 这里我们则是抽象出来了一个代理类,该类职责为代理使用者创建的CustomerHandler中的所有方法,对外暴露了执行消费者方法的口子{@link #invoke(DelayMessage)}方便消息分发器进行统一处理<br>
 * 1.目前该代理还是手动的,后续应该考虑自动进行扫码CustomerHandler进行代理操作
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
