package com.ketchup.di;


import android.content.Context;

import com.ketchup.utils.ContextCompatUtils;
import com.ketchup.MainActivity;
import com.ketchup.utils.ToolbarController;


import dagger.Module;
import dagger.Provides;

@Module
public class ActivityModule {

    @ActivityScope
    @Provides
    ToolbarController providesToolbarController(MainActivity activity) {
        return new ToolbarController(activity);
    }

    @ActivityScope
    @Provides
    ContextCompatUtils providesContextCompatProvider(Context context) {
        return new ContextCompatUtils(context);
    }
}
