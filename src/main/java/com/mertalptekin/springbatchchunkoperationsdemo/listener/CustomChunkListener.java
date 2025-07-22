package com.mertalptekin.springbatchchunkoperationsdemo.listener;

import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

@Component
public class CustomChunkListener implements ChunkListener {

    @Override
    public void beforeChunk(ChunkContext context) {
        System.out.println("Before Chunk");
        ChunkListener.super.beforeChunk(context);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
        System.out.println("After Chunk");
        ChunkListener.super.afterChunkError(context);
    }
}
