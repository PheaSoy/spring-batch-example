package org.soyphea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class OrderBatchProcessor implements ItemProcessor<Order, Order> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OrderBatchProcessor.class);
  private String skipOrderId = "00002";
  @Override
  public Order process(Order order) throws Exception {
    LOGGER.info("Process the batch with order id => {}", order.getOrderId());
    Order newOrder = new Order(order.getOrderId(), order.getStatus(), order.getPrice() * 100);
    LOGGER.info("Process order id => {} is completed", order.getOrderId());
    if(order.getOrderId().equals(skipOrderId)) return null;
    return newOrder;
  }
}
