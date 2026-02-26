package van.planifolia.planifoliamq.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
     * 异步线程池
     * @return 线程池
     */
    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // 设置核心线程数
        executor.setMaxPoolSize(10);  // 设置最大线程数
        executor.setQueueCapacity(25);  // 设置队列容量
        executor.setThreadNamePrefix("QueueObserver-");
        executor.initialize();
        return executor;
    }

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
    @DependsOn("customerRegister")
    public QueueFactory queueFactory(CustomerRegister customerRegister,ThreadPoolTaskExecutor  threadPoolTaskExecutor) {
        return new QueueFactory(customerRegister,threadPoolTaskExecutor);
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
