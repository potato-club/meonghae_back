package com.meonghae.userservice.infra.config;

import com.meonghae.userservice.domin.user.User;
import com.meonghae.userservice.service.port.UserRepository;
import com.meonghae.userservice.service.client.feign.PetServiceClient;
import com.meonghae.userservice.service.client.feign.S3ServiceClient;
import com.meonghae.userservice.dto.file.S3Request;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class AppConfig implements SchedulingConfigurer {

    private final UserRepository userRepository;
    private final S3ServiceClient s3Service;
    private final PetServiceClient petService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
        taskRegistrar.addTriggerTask(
                () -> {
                    // 일주일이 지난 회원 삭제
                    LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
                    List<User> usersToDelete = userRepository.findByDeletedIsTrueAndModifiedDateBefore(oneWeekAgo);
                    userRepository.deleteAll(usersToDelete);

                    for (User user : usersToDelete) {
                        S3Request requestDto = new S3Request(user.getEmail(), "USER");
                        s3Service.deleteFileForUser(requestDto);
                        petService.deletedByUserEmail(user.getEmail());
                    }
                },
                triggerContext -> new CronTrigger("0 0 0 * * *").nextExecutionTime(triggerContext) // 매일 자정에 실행
        );
    }

    @Bean
    public Executor taskExecutor() {
        ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10);
        executor.setRemoveOnCancelPolicy(true);
        return executor;
    }
}
