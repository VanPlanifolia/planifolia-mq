package van.planifolia.planifoliamq.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 13:37
 * @description 消费者标记注解,用于标注方法为消费者方法
 *
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DelayConsumer {
    // 消费的队列名
    String queue();
    // 消费的主题名
    String topic();
}