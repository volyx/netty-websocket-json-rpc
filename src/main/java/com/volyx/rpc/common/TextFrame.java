package com.volyx.rpc.common;

import javax.annotation.Nonnull;

public interface TextFrame {
    @Nonnull
    String getId();

    int getType();
}
