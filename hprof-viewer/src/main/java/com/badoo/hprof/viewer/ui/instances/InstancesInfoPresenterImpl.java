package com.badoo.hprof.viewer.ui.instances;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.library.model.Instance;
import com.badoo.hprof.viewer.provider.ClassProvider;
import com.badoo.hprof.viewer.provider.InstanceProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Created by Erik Andre on 13/12/15.
 */
public class InstancesInfoPresenterImpl implements InstancesInfoPresenter {

    private final View view;
    private final List<Instance> instances;
    private final ClassProvider clsProvider;
    private final InstanceProvider instanceProvider;

    public InstancesInfoPresenterImpl(@Nonnull View view, @Nonnull List<Instance> instances,
                                      @Nonnull ClassProvider clsProvider, @Nonnull InstanceProvider instanceProvider) {
        this.view = view;
        this.instances = instances;
        this.clsProvider = clsProvider;
        this.instanceProvider = instanceProvider;
        update();
    }

    private void update() {
        List<InstanceInfo> model = new ArrayList<InstanceInfo>();
        for (Instance instance : instances) {
            ClassDefinition instanceCls = instanceProvider.getClass(instance);
            String name = clsProvider.getClassName(instanceCls) + " @ " + instance.getObjectId();
            model.add(new InstanceInfo(instance, name, clsProvider.getInstanceSizeForClass(instanceCls)));
        }
        view.showInstances(model);
    }

    @Override
    public void onInstanceSelected(@Nonnull InstanceInfo instance) {
        Map<Object, Object> fields = null;
        try {
            fields = instanceProvider.getInstanceFields(instance.instance);
            view.showInstanceDetails(fields);
        }
        catch (IOException e) {
            throw new RuntimeException("Could not read fields for instance " + instance.name);
        }
    }
}
