package com.mertalptekin.springbatchchunkoperationsdemo.listener;

import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class CustomSkipListener  implements SkipListener {

    @Override
    public void onSkipInRead(Throwable t) {
        System.out.println("onSkipInRead ");
        SkipListener.super.onSkipInRead(t);
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        System.out.println("onSkipInWrite ");
        SkipListener.super.onSkipInWrite(item, t);
    }

    @Override
    public void onSkipInProcess(Object item, Throwable t) {
        System.out.println("processSkip " + item);
        // atlanan birşey varsa repository.save();
        // logger.log();
        SkipListener.super.onSkipInProcess(item, t);
    }
}
