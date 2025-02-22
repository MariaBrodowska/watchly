package com.example.watchly;

import static androidx.core.content.ContextCompat.startActivity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Pages {
    private Context context;
    public Pages(Context context){
        this.context = context;
    }
    public void animateButton(View v) {
        ScaleAnimation scaleDown = new ScaleAnimation(
                1f, 0.8f, 1f, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleDown.setDuration(150);
        scaleDown.setFillAfter(true);
        ScaleAnimation scaleUp = new ScaleAnimation(
                0.8f, 1f, 0.8f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(100);
        scaleUp.setFillAfter(true);
        v.startAnimation(scaleDown);
        scaleDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                v.startAnimation(scaleUp);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }
    public void animateMenuButton(View button, TextView textView) {
        ScaleAnimation scaleDown = new ScaleAnimation(
                1f, 0.9f, 1f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleDown.setDuration(150);

        scaleDown.setFillAfter(true);
        ObjectAnimator colorChange = ObjectAnimator.ofArgb(textView, "textColor",
                Color.parseColor("#FFFFFF"), Color.parseColor("#FF9800"));
        colorChange.setDuration(100);

        ScaleAnimation scaleUp = new ScaleAnimation(
                0.9f, 1f, 0.9f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setDuration(100);
        scaleUp.setFillAfter(true);
        scaleDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                textView.startAnimation(scaleUp);
                colorChange.reverse();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        button.startAnimation(scaleDown);
        colorChange.start();
    }
    public void setMenuIntent(TextView text, ImageView button, Class<?> cls) {
        button.setOnClickListener(v -> {
            animateMenuButton(v, text);
            Intent intent = new Intent(context, cls);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                ((Activity) context).finish();
            }
        });
    }

    public void setName(TextView name){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userName = currentUser.getDisplayName();
            name.setText(userName);
        } else {
            name.setText("Guest");
        }
    }
    public void setLogout(ImageView logout){
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(context, StartActivity.class);
            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).finish();
            }
        });
    }
}
