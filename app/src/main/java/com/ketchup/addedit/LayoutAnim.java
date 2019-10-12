package com.ketchup.addedit;

import android.animation.Animator;
import android.view.View;

public class LayoutAnim implements Animator.AnimatorListener {
    private View view;
    private boolean isChecked = false;

    private float showUpAlpha = 1.0f;
    private float disappearAlpha = 0.0f;

    public LayoutAnim(final View view, final boolean isChecked) {
        this.view = view;
        this.isChecked = isChecked;
    }

    public void setChecked(boolean check) {
        isChecked = check;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        if (isChecked) {
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        if (!isChecked) {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
