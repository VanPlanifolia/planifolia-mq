package van.planifolia.planifoliamq.test;

import lombok.extern.slf4j.Slf4j;
import van.planifolia.planifoliamq.annotations.DelayConsumer;
import van.planifolia.planifoliamq.model.DelayMessage;

/**
 * @author Van.Planifolia
 * @create 2025/4/14 16:39
 * @description
 */
@Slf4j
public class Customer {

    @DelayConsumer(queue = "order", topic = "c1")
    public void c1(DelayMessage message) {
        log.info("c1收到消息:{}", message);
    }
    @DelayConsumer(queue = "order", topic = "c2")
    public void c2(DelayMessage message) {
        log.info("c2收到消息:{}", message);
    }
    @DelayConsumer(queue = "order", topic = "d1")
    public void d1(DelayMessage message) {
        log.info("d1收到消息:{}", message);
    }
    @DelayConsumer(queue = "order", topic = "e1")
    public void e1 (DelayMessage message) {
        log.info("e1收到消息:{}", message);
    }

}
