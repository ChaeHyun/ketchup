package com.ketchup.di;

import com.ketchup.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityBinder {

    // AppComponent 의 SubComponent 를 생성한다.
    // Fragments Module 에서 MainActivity에서 사용되는 여러 Fragment들에 대한 Subcomponents가 만들어진다.
    // 이 Fragment는 상위 MainActivity에서 가지고 있는 ViewModelModule을 이용해서 ViewModelFactory를 주입받을 수 있는가 확인필요.
    @ActivityScope
    @ContributesAndroidInjector(modules = { ViewModelModule.class, FragmentsModule.class })
    abstract MainActivity bindMainActivity();


}
