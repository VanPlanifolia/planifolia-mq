package van.planifolia.planifoliamq.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消费者注解，用于标注消费方法，指定其监听的队列和主题
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DelayConsumer {
    String queue();  // 消费的队列名
    String topic();  // 消费的主题名
}