package com.badoo.hprof.viewer.ui.classinfo;

import com.badoo.hprof.library.model.Instance;

import java.util.List;

import javax.annotation.Nonnull;
import javax.swing.JComponent;

/**
 * Created by Erik Andre on 12/12/15.
 */
public interface ClassesInfoPresenter {

    void onQueryByName(@Nonnull String query);

    void onListInstances(@Nonnull ClassInfo cls);

    interface View {

        void showLoading();

        void showReadyToQuery();

        void showQueryResult(List<ClassInfo> result, @Nonnull String query);

        void showInstancesListTab(@Nonnull String name, @Nonnull List<Instance> instances);
    }
}
