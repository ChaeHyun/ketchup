package com.ketchup.utils;

import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AnchoringFab extends CoordinatorLayout.LayoutParams {

    private FloatingActionButton fab;
    private CoordinatorLayout.LayoutParams params;

    public AnchoringFab(ViewGroup.LayoutParams params, FloatingActionButton fab) {
        super(params);
        this.fab = fab;
        this.params = (CoordinatorLayout.LayoutParams) params;
    }

    public void removeAnchor(Drawable drawable) {
        params.gravity = Gravity.BOTTOM | GravityCompat.END;
        params.setAnchorId(-1);
        params.anchorGravity = 0;

        fab.setLayoutParams(params);

        setFabIconDrawable(drawable);
    }

    public void addAnchor(int anchorViewId, Drawable drawable) {
        params.setAnchorId(anchorViewId);
        params.anchorGravity = Gravity.BOTTOM | GravityCompat.END;
        params.gravity = 0;

        fab.setLayoutParams(params);

        setFabIconDrawable(drawable);
    }

    public void setFabIconDrawable(Drawable drawable) {
        fab.setImageDrawable(drawable);
        fab.hide();
        fab.show();
    }
}
