package com.volyx.rpc.common;

import javax.annotation.Nonnull;

public interface Request extends TextFrame {
    @Nonnull
    String getParams();

    long getStartTime();

    boolean isBatch();
}
