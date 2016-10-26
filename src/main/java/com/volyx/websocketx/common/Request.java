package com.volyx.websocketx.common;

import javax.annotation.Nonnull;

public interface Request {
    @Nonnull
    String getParams();

    @Nonnull
    String getId();

    long getStartTime();

    boolean isBatch();
}
