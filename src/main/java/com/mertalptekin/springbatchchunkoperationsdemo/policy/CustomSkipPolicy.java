package com.mertalptekin.springbatchchunkoperationsdemo.policy;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

@Component
public class CustomSkipPolicy implements SkipPolicy {
    @Override
    public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {

        // Not: Atlatmaya sıfırdan başlattı.

        System.out.println("Şuana kadar kaç kayıt atlanmış" + skipCount);
        return t instanceof RuntimeException; // RuntimeException durumları oluşursa, kendi custom hata exception sınfımıza göre atlamayı berlirleyebiliriz.
    }
}
