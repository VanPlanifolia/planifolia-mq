package van.planifolia.planifoliamq.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import van.planifolia.planifoliamq.core.*;

import java.util.List;

/**
 * @Description
 * @Author Van.Planifolia
 * @Date 2026/2/25
 */
@Configuration
public class MQAutoConfiguration {

    /**
     * 消费者注册器,初始化该Bean并且将所有的消费者加入注册表
     *
     * @return 消费者注册器
     */
    @Bean
    public CustomerRegister customerRegister(List<Customer> customers) {
        CustomerRegister customerRegister = new CustomerRegister();
        customerRegister.register(customers);
        return customerRegister;
    }

    /**
     * 创建队列工厂
     *
     * @param customerRegister 消费者注册器
     * @return 队列工厂,用于初始化bean以及创建每个队列的观察者
     */
    @Bean
    public QueueFactory queueFactory(CustomerRegister customerRegister) {
        QueueFactory queueFactory = new QueueFactory();
        customerRegister.getAllQueueName().forEach(queueFactory::createPollingTask);
        return queueFactory;
    }

    /**
     * 创建延迟队列分发器
     *
     * @param customerRegister 注册表
     * @return 延迟队列分发器
     */
    @Bean
    @ConditionalOnMissingBean
    public DelayMessageDispatcher delayQueueDispatcher(CustomerRegister customerRegister) {
        return new DefDelayQueueDispatcher(customerRegister);
    }

    /**
     * 创建队列消息发送模板
     *
     * @param queueFactory         队列工厂
     * @param delayQueueDispatcher 分发策略
     * @return 构建结果
     */
    @Bean
    @ConditionalOnMissingBean(DelayQueueTemplate.class)
    public DelayQueueTemplate delayQueueTemplate(QueueFactory queueFactory, DelayMessageDispatcher delayQueueDispatcher) {
        return new DelayQueueTemplate(delayQueueDispatcher, queueFactory);
    }


}
