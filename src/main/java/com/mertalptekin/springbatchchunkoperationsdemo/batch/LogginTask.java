package com.mertalptekin.springbatchchunkoperationsdemo.batch;

import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@Component
public class LogginTask implements TaskExecutor {
    @Override
    public void execute(Runnable task) {
        String threadName = Thread.currentThread().getName();
        Integer threadPriority = Thread.currentThread().getPriority();
        Long threadId = Thread.currentThread().getId();
        String threadState = Thread.currentThread().getThreadGroup().getName();

        System.out.println(threadName + " " + threadPriority + " " + threadState + " " + threadId);
    }
}
