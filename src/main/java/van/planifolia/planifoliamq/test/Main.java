package van.planifolia.planifoliamq.test;

import van.planifolia.planifoliamq.core.CustomerRegister;
import van.planifolia.planifoliamq.core.DelayQueueDispatcher;
import van.planifolia.planifoliamq.core.DelayQueueTemplate;
import van.planifolia.planifoliamq.core.QueueFactory;

import java.util.Set;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 16:39
 * @description
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        //1.构建消费者注册器,注册所有的消费者
        CustomerRegister register = new CustomerRegister();
        register.register(new Customer());
        // 一个队列一个监听者,监听所有的队列
        Set<String> all = register.getAllQueueName();
        all.forEach(e->new Thread(QueueFactory.createPollingTask(e)).start());
        // 构建c端发送者
        DelayQueueTemplate template = new DelayQueueTemplate(new DelayQueueDispatcher(register));
        template.sendDelay("order","c1","你好1",1);
        template.sendDelay("order","c1","你好2",2);
        template.sendDelay("order","c2","你好3",3);
        template.sendDelay("order","e1","你好4",4);
        template.sendDelay("order","d1","你好5",5);
        // 主线程阻塞
    }
}
