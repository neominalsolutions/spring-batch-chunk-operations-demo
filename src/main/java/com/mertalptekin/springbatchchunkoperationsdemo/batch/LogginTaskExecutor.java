package com.mertalptekin.springbatchchunkoperationsdemo.batch;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class LogginTaskExecutor implements TaskExecutor {
    @Override
    public void execute(Runnable task) {

        System.out.println("Task run");

        String threadName = Thread.currentThread().getName();
        Integer threadPriority = Thread.currentThread().getPriority();
        Long threadId = Thread.currentThread().getId();
        String threadState = Thread.currentThread().getThreadGroup().getName();

        System.out.println("thredName -> " + threadName + " threadId -> " + threadId);

        task.run(); // taskı run ettik.
    }
}
