package com.badoo.hprof.viewer.ui.instances;

import java.util.List;

import javax.annotation.Nonnull;

/**
 * Created by Erik Andre on 12/12/15.
 */
public interface InstanceInfoPresenter {

    void onQueryByName(@Nonnull String query);

    interface View {

        void showLoading();

        void showReadyToQuery();

        void showQueryResult(List<ClassInfo> result, @Nonnull String query);
    }
}
