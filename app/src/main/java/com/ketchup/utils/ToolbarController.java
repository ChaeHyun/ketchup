package com.ketchup.utils;

import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.ketchup.R;

import javax.inject.Inject;


/**
 * Activity에 속해 있는 Appbar를 하위 Fragment에서 쉽게 조작할 수 있도록
 * 메소드화 시켜서 관리한다.
 * */
public class ToolbarController {
    private DrawerLayout drawer;

    private ActionBarDrawerToggle toggle;

    private CollapsingToolbarLayout ctl;
    private Toolbar toolbar;

    private TextInputLayout titleLayout;
    private EditText titleEditText;

    @Inject
    public ToolbarController(Activity activity) {
        drawer = activity.findViewById(R.id.drawer_layout);
        ctl = activity.findViewById(R.id.activity_main_collapsing_toolbar);
        toolbar = activity.findViewById(R.id.toolbar);
        titleLayout = activity.findViewById(R.id.add_item_hint_title);
        titleEditText = activity.findViewById(R.id.add_item_edit_text_title);

        toggle = new ActionBarDrawerToggle(activity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    /** About toggle */
    public ActionBarDrawerToggle getToggle() {
        return toggle;
    }

    public void toggleSyncState() {
        toggle.syncState();
    }

    public void setDrawerIndicatorEnabled(boolean enabled) {
        toggle.setDrawerIndicatorEnabled(enabled);
    }

    public void addToolbarOnClickListener(View.OnClickListener listener) {
        toggle.setToolbarNavigationClickListener(listener);
    }


    /** About Drawer */
    public DrawerLayout getDrawer() {
        return drawer;
    }

    public void addDrawerListener(ActionBarDrawerToggle toggle) {
        drawer.addDrawerListener(toggle);
    }

    public void removeDrawerListener(ActionBarDrawerToggle toggle) {
        drawer.removeDrawerListener(toggle);
    }

    /** About Toolbar */
    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setToolbarColor(int color) {
        ctl.setBackgroundColor(color);
        toolbar.setBackgroundColor(color);
    }

    public void setTitle(String title) {
        setCollapsingToolbarTitle(title);
        setToolbarTitle(title);
    }

    public void setCollapsingToolbarTitle(String title) {
        ctl.setTitle(title);
    }

    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    /** EditText Layout which is going to be visible only for AddEditTaskFragment. */
    public void setupTitleLayout(int visibility, String preset, String error) {
        titleLayout.setVisibility(visibility);
        titleEditText.setText(preset);
        titleEditText.setError(error);
    }
}
