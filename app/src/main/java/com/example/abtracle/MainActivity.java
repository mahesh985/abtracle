import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObstacleDodgeView extends View {
    private static final int PLAYER_SIZE = 100;
    private static final int OBSTACLE_WIDTH = 200;
    private static final int OBSTACLE_HEIGHT = 50;
    private static final int OBSTACLE_GAP = 400;
    private static final int MOVE_SPEED = 10;
    private static final int MAX_OBSTACLES = 5;

    private Paint playerPaint;
    private Paint obstaclePaint;
    private RectF playerRect;
    private List<RectF> obstacleRects;
    private int score;
    private int highScore;
    private boolean isGameOver;
    private Handler handler;

    public ObstacleDodgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        playerPaint = new Paint();
        playerPaint.setColor(Color.BLUE);

        obstaclePaint = new Paint();
        obstaclePaint.setColor(Color.RED);

        playerRect = new RectF();
        obstacleRects = new ArrayList<>();

        handler = new Handler();
        startGame();
    }

    private void startGame() {
        playerRect.set(0, getHeight() / 2 - PLAYER_SIZE / 2,
                PLAYER_SIZE, getHeight() / 2 + PLAYER_SIZE / 2);
        obstacleRects.clear();
        score = 0;
        isGameOver = false;
        spawnObstacles();
        handler.post(gameRunnable);
    }

    private Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isGameOver) {
                update();
                invalidate();
                handler.postDelayed(this, 30);
            }
        }
    };

    private void update() {
        score++;

        for (RectF obstacle : obstacleRects) {
            obstacle.left -= MOVE_SPEED;
            obstacle.right -= MOVE_SPEED;

            if (obstacle.right < 0) {
                obstacleRects.remove(obstacle);
                score += 10;
                spawnObstacle();
                break;
            }

            if (obstacle.intersect(playerRect)) {
                isGameOver = true;
                if (score > highScore) {
                    highScore = score;
                }
                break;
            }
        }
    }

    private void spawnObstacles() {
        Random random = new Random();
        int startY = random.nextInt(getHeight() - OBSTACLE_GAP);
        for (int i = 0; i < MAX_OBSTACLES; i++) {
            float left = getWidth() + i * OBSTACLE_GAP;
            float top = startY;
            float right = left + OBSTACLE_WIDTH;
            float bottom = top + OBSTACLE_HEIGHT;
            RectF obstacleRect = new RectF(left, top, right, bottom);
            obstacleRects.add(obstacleRect);
        }
    }

    private void spawnObstacle() {
        Random random = new Random();
        int startY = random.nextInt(getHeight() - OBSTACLE_GAP);
        float left = obstacleRects.get(obstacleRects.size() - 1).right + OBSTACLE_GAP;
        float top = startY;
        float right = left + OBSTACLE_WIDTH;
        float bottom = top + OBSTACLE_HEIGHT;
        RectF obstacleRect = new RectF(left, top, right, bottom);
        obstacleRects.add(obstacleRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(playerRect, playerPaint);

        for (RectF obstacle : obstacleRects) {
            canvas.drawRect(obstacle, obstaclePaint);
        }

        if (isGameOver) {
            Paint textPaint = new Paint();
            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(100);
            canvas.drawText("Game Over", getWidth() / 2 - 200, getHeight() / 2, textPaint);
        }

        Paint scorePaint = new Paint();
        scorePaint.setColor(Color.BLACK);
        scorePaint.setTextSize(50);
        canvas.drawText("Score: " + score, 20, 50, scorePaint);
        canvas.drawText("High Score: " + highScore, 20, 100, scorePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isGameOver) {
                playerRect.top -= MOVE_SPEED;
                playerRect.bottom -= MOVE_SPEED;
            } else {
                startGame();
            }
        }
        return true;
    }
}
