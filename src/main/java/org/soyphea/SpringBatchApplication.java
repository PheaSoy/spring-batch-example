package org.soyphea;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@Slf4j
public class SpringBatchApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringBatchApplication.class, args);
  }

  @Bean
  CommandLineRunner commandLineRunner() {
    return args -> {
      startMyJob();
    };
  }

  @Autowired
  JobLauncher jobLauncher;

  @Autowired
  //@Qualifier("order_job")
  private Job job;

  public void startMyJob() {
    log.info("Start my job now");
    try {
      JobExecution execution = jobLauncher.run(job, new JobParameters());
      log.info("Job Status {} ", execution.getStatus());
      log.info("Job completed!");
    } catch (Exception e) {
      e.printStackTrace();
      log.error("Job failed");
    }
  }
}
