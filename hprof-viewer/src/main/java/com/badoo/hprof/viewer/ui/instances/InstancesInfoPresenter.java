package com.badoo.hprof.viewer.ui.instances;

import java.util.List;

/**
 * Created by Erik Andre on 13/12/15.
 */
public interface InstancesInfoPresenter {

    interface View {

        void showInstances(List<InstanceInfo> instances);
    }
}
