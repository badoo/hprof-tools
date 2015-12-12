package com.badoo.hprof.viewer.ui.instances;

import com.badoo.hprof.library.model.ClassDefinition;
import com.badoo.hprof.viewer.provider.ClassProvider;
import com.badoo.hprof.viewer.provider.InstanceProvider;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * Implementation of InstanceInfoPresenter
 *
 * Created by Erik Andre on 12/12/15.
 */
public class InstanceInfoPresenterImpl implements InstanceInfoPresenter {

    private final View view;
    private final ClassProvider clsProvider;
    private final InstanceProvider instanceProvider;

    public InstanceInfoPresenterImpl(@Nonnull View view, @Nonnull ClassProvider clsProvider, @Nonnull InstanceProvider instanceProvider) {
        this.view = view;
        this.clsProvider = clsProvider;
        this.instanceProvider = instanceProvider;
        view.showReadyToQuery();
    }

    @Override
    public void onQueryByName(@Nonnull String query) {
        List<ClassDefinition> result = clsProvider.getClassesMatchingQuery(query);
        List<ClassInfo> model = new ArrayList<ClassInfo>();
        for (ClassDefinition cls : result) {
            int count = instanceProvider.getInstancesOfClass(cls);
            model.add(new ClassInfo(cls, clsProvider.getClassName(cls), count));
        }
        view.showQueryResult(model, query);
    }

}
