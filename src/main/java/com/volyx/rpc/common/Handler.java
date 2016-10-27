package com.volyx.rpc.common;

import javax.annotation.Nonnull;

public interface Handler {

    @Nonnull
    Result execute(@Nonnull Request request);

    @Nonnull
    String getName();
}
