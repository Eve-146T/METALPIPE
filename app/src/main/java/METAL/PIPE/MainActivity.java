// MainActivity.java
package METAL.PIPE;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // These window features must be requested BEFORE setContentView
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        MediaPlayer startSound = MediaPlayer.create(this, R.raw.pipe);
        if (startSound != null) {
            startSound.setOnCompletionListener(mp -> {
                mp.release(); // Clean up after sound finishes
            });
            startSound.start();
        }
        // Create the game view
        GameView gameView = new GameView(this);

        // Adjust the parameters as desired
        gameView.setAverageSpeed(15f);     // Faster falling speed
        gameView.setAverageScale(0.5f);    // Larger pipes
        gameView.setAverageRotation(45f);  // 45-degree base rotation

        // Set the view
        setContentView(gameView);

        // Set flags to extend behind navigation bar
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        // Make navigation bar transparent
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

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
}


class Pipe {
    float x, y;
    float speed;
    float rotation;
    float scale;
    float width, height;
    boolean isAlive = true;

    Pipe(float x, float y, float speed, float rotation, float scale) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.rotation = rotation;
        this.scale = scale;
    }

    void update() {
        y += speed;
    }

    boolean contains(float touchX, float touchY) {
        return touchX >= x - (width * scale) / 2 &&
                touchX <= x + (width * scale) / 2 &&
                touchY >= y - (height * scale) / 2 &&
                touchY <= y + (height * scale) / 2;
    }
}

class GameView extends View {
    private Bitmap pipeBitmap;
    private MediaPlayer hitSound;
    private ArrayList<Pipe> pipes = new ArrayList<>();
    private Random random = new Random();
    private long lastSpawnTime = 0;
    private static final long SPAWN_DELAY = 500; // 1 second
    private Paint backgroundPaint;

    // Customizable parameters
    private float averageSpeed = 5f;
    private float averageScale = 1f;
    private float averageRotation = 0f;

    public GameView(Context context) {
        super(context);

        // Load the pipe image
        pipeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pipe);

        // Initialize sound
        hitSound = MediaPlayer.create(context, R.raw.pipe);

        // Initialize background paint
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);

        // Start the game loop
        post(new Runnable() {
            @Override
            public void run() {
                update();
                postDelayed(this, 16); // ~60 FPS
            }
        });
    }

    private void spawnPipe() {
        float x = random.nextFloat() * getWidth();
        float speed = averageSpeed + (random.nextFloat() - 0.5f) * 2; // Variation in speed
        float rotation = averageRotation + random.nextFloat() * 360; // Random rotation
        float scale = averageScale + (random.nextFloat() - 0.5f) * 0.5f; // Variation in scale

        Pipe pipe = new Pipe(x, -100, speed, rotation, scale);
        pipe.width = pipeBitmap.getWidth();
        pipe.height = pipeBitmap.getHeight();
        pipes.add(pipe);
    }

    private void update() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime > SPAWN_DELAY) {
            spawnPipe();
            lastSpawnTime = currentTime;
        }

        Iterator<Pipe> iterator = pipes.iterator();
        while (iterator.hasNext()) {
            Pipe pipe = iterator.next();
            pipe.update();

            // Remove pipes that are off screen or "popped"
            if (!pipe.isAlive || pipe.y > getHeight()) {
                iterator.remove();
                if (pipe.y > getHeight()) {
                    playHitSound();
                }
            }
        }

        invalidate(); // Trigger redraw
    }

    private void playHitSound() {
        if (hitSound != null) {
            hitSound.seekTo(0);
            hitSound.start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Fill the background with black
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        for (Pipe pipe : pipes) {
            Matrix matrix = new Matrix();
            matrix.setTranslate(pipe.x - (pipe.width * pipe.scale) / 2,
                    pipe.y - (pipe.height * pipe.scale) / 2);
            matrix.preRotate(pipe.rotation, (pipe.width * pipe.scale) / 2,
                    (pipe.height * pipe.scale) / 2);
            matrix.preScale(pipe.scale, pipe.scale);

            canvas.drawBitmap(pipeBitmap, matrix, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float touchX = event.getX();
            float touchY = event.getY();

            for (Pipe pipe : pipes) {
                if (pipe.contains(touchX, touchY)) {
                    pipe.isAlive = false;
                    break;
                }
            }
        }
        return true;
    }

    // Methods to adjust game parameters
    public void setAverageSpeed(float speed) {
        averageSpeed = speed;
    }

    public void setAverageScale(float scale) {
        averageScale = scale;
    }

    public void setAverageRotation(float rotation) {
        averageRotation = rotation;
    }
}