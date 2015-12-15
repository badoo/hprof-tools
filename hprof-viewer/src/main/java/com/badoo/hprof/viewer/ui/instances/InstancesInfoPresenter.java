package com.badoo.hprof.viewer.ui.instances;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Created by Erik Andre on 13/12/15.
 */
public interface InstancesInfoPresenter {

    void onInstanceSelected(@Nonnull InstanceInfo instance);

    interface View {

        void showInstances(@Nonnull List<InstanceInfo> instances);

        void showInstanceDetails(@Nonnull Map<Object, Object> fields);
    }
}
