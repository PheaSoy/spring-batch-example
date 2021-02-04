package org.soyphea;

import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

  @Autowired
  private JobBuilderFactory jobBuilderFactory;

  @Autowired
  private StepBuilderFactory stepBuilderFactory;

  @Value("${input-orders}")
  private String fileInput;

  @Bean
  public Job importOrders(Step step1,JobCompleteTeller jobCompleteTeller) {
    return jobBuilderFactory.get("importOrders")
        .incrementer(new RunIdIncrementer())
        .listener(jobCompleteTeller)
        .flow(step1)
        .end()
        .build();
  }

  @Bean
  public Step step1(ItemWriter<Order> writer, ItemReader<Order> reader,
      ItemProcessor<Order, Order> processor) {
    return stepBuilderFactory.get("step1")
        .<Order, Order>chunk(10)
        .reader(reader)
        .processor(processor)
        .writer(writer).build();
  }

  @Bean
  OrderBatchProcessor orderBatchProcessor() {
    return new OrderBatchProcessor();
  }



  @Bean
  public FlatFileItemReader<Order> reader() {
    return new FlatFileItemReaderBuilder<Order>()
        .name("ordersItemReader")
        .resource(new ClassPathResource("orders.csv"))
        .delimited()
        .names(new String[]{"orderId", "status","price"})
        .fieldSetMapper(new BeanWrapperFieldSetMapper<Order>() {{
          setTargetType(Order.class);
        }})
        .build();
  }
  @Bean
  public JdbcBatchItemWriter<Order> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Order>()
        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
        .sql("INSERT INTO payment_order (order_id, status,price) VALUES (:orderId, :status,:price)")
        .dataSource(dataSource)
        .build();
  }
}
