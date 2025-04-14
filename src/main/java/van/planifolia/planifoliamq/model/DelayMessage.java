/**
 * @author Van.Planifolia
 * @create 2025/4/14 10:13
 * @description
 */

package van.planifolia.planifoliamq.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DelayMessage {
    private String topic;
    private String body;
}
