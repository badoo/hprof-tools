package com.badoo.hprof.viewer.provider;

import javax.annotation.Nonnull;

/**
 * Base class for data providers used by presenters.
 *
 * Created by Erik Andre on 12/12/15.
 */
public abstract class BaseProvider {

    private ProviderStatus status = ProviderStatus.NOT_LOADED;

    public synchronized ProviderStatus getStatus() {
        return status;
    }

    protected void setStatus(@Nonnull ProviderStatus status) {
        this.status = status;
    }

    public enum ProviderStatus {
        NOT_LOADED,
        LOADED
    }
}
