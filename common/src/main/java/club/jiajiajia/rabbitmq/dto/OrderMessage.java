package club.jiajiajia.rabbitmq.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderMessage implements Serializable {
    private Integer orderId;
    private String orderNumber;
}
