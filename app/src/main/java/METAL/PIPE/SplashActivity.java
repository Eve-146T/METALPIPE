// SplashActivity.java
package METAL.PIPE;

import android.animation.ObjectAnimator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ViewGroup.LayoutParams;
import android.view.Gravity;
import android.util.TypedValue;

public class SplashActivity extends Activity {
    private ImageView headphoneIcon;
    private TextView headphoneText;
    private TextView tapText;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set fullscreen flags before creating view
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Set flags to extend behind navigation bar
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        // Initialize sound

        // Create main container
        container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setBackgroundColor(Color.BLACK);
        container.setGravity(Gravity.CENTER);
        container.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        // Create and setup headphone icon
        headphoneIcon = new ImageView(this);
        int iconSize = (int) (getResources().getDisplayMetrics().widthPixels * 0.7);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMargins(0, -(int)(iconSize * 0.2), 0, 50);
        headphoneIcon.setLayoutParams(iconParams);
        headphoneIcon.setImageResource(R.drawable.headphones);
        headphoneIcon.setColorFilter(Color.WHITE);

        // Create and setup headphone text
        headphoneText = new TextView(this);
        headphoneText.setText("This app is best experienced with headphones.");
        headphoneText.setTextColor(Color.WHITE);
        headphoneText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        headphoneText.setGravity(Gravity.CENTER);
        headphoneText.setTypeface(Typeface.MONOSPACE);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0, 0, 0, 40);
        headphoneText.setLayoutParams(textParams);

        // Create and setup tap text
        tapText = new TextView(this);
        tapText.setText("Tap anywhere to start.");
        tapText.setTextColor(Color.WHITE);
        tapText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tapText.setGravity(Gravity.CENTER);
        tapText.setTypeface(Typeface.MONOSPACE);
        tapText.setAlpha(0f);

        // Add views to container
        container.addView(headphoneIcon);
        container.addView(headphoneText);
        container.addView(tapText);

        setContentView(container);

        // Initially set alpha to 0
        headphoneIcon.setAlpha(0f);
        headphoneText.setAlpha(0f);

        startAnimations();

        // Set click listener for the whole screen
        container.setOnClickListener(v -> {
            // Play sound


            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            // Disable transition animation
            overridePendingTransition(0, 0);

            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }

    private void startAnimations() {
        ObjectAnimator iconFade = ObjectAnimator.ofFloat(headphoneIcon, "alpha", 0f, 1f);
        ObjectAnimator textFade = ObjectAnimator.ofFloat(headphoneText, "alpha", 0f, 1f);

        AnimatorSet firstSet = new AnimatorSet();
        firstSet.playTogether(iconFade, textFade);
        firstSet.setDuration(2000);

        ObjectAnimator tapFade = ObjectAnimator.ofFloat(tapText, "alpha", 0f, 1f);
        tapFade.setDuration(1500);

        AnimatorSet fullSet = new AnimatorSet();
        fullSet.playSequentially(firstSet, tapFade);
        fullSet.start();
    }
}