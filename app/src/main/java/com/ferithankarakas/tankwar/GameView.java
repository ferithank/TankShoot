package com.ferithankarakas.tankwar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable{

    private Thread thread;
    private boolean isPlaying, isGameOver = false, isBang = false;
    private Background background1, background2;
    private int screenX, screenY, shootCount = 0, score = 0;
    private Paint paint;
    public static float screenRatioX, screenRatioY;
    private Tank tank;
    private Enemy[] enemys;
    private Random random;
    private SoundPool soundPool;
    private List<Bullet> bullets;
    private int sound;
    private SharedPreferences preferences;
    private GameActivity gameActivity;
    public GameView(GameActivity gameActivity, int screenX, int screenY) {
        super(gameActivity);
        preferences = gameActivity.getSharedPreferences("game", Context.MODE_PRIVATE);

        this.gameActivity = gameActivity;
        this.screenX = screenX;
        this.screenY = screenY;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();

            soundPool = new SoundPool.Builder().setAudioAttributes(audioAttributes).build();
        }
        else{
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0) ;
        }
        sound = soundPool.load(gameActivity, R.raw.shoot, 1);
        screenRatioY = 1920f / screenY; //Dikey düzlem
        screenRatioX = 1080f / screenX; //Yatay düzlem

        background1 = new Background(screenX, screenY, getResources());
        background2 = new Background(screenX, screenY, getResources());

        tank = new Tank(this, screenY, screenX, getResources());
        bullets = new ArrayList<>();
        background2.y = -screenY;

        paint = new Paint();
        paint.setTextSize(64);
        paint.setColor(Color.BLACK);

        enemys = new Enemy[4];


        for (int i = 0; i < 4; i++){
            Enemy enemy = new Enemy(getResources());
            enemys[i] = enemy;
        }
        random = new Random();
    }

    @Override
    public void run() {
        while (isPlaying){
            Update();
            Draw();
            Sleep();
        }
        while (!tank.exited)
            Draw();
        saveHighScore();
        waitBeforeExit();
    }

    private void Update() { // Ekran güncellemeleri


        background1.y += 10 * screenRatioY;
        background2.y += 10 * screenRatioY;

        if (background1.y > screenY){ // Arkaplan efekt
            background1.y = -screenY;
        }
        if (background2.y > screenY){
            background2.y = -screenY;
        }                             // Arkaplan efekt son

        if (tank.isMoveLeft)         // Tank sola kaydırma
            tank.x -= 20 * screenRatioX;
        else if (tank.isMoveRight)    // Tank sağa kaydırma
            tank.x += 20 * screenRatioX;

        if (tank.x < 0) // Tank en sola geldiğinde
            tank.x = 0;

        if (tank.x > screenX - tank.width)
            tank.x = screenX - tank.width;

        if (shootCount == 15){ //Merminin sürekli bir şekilde çıkmasını engellemek için
            newBullet();
            shootCount = 0;
        }

        shootCount++;
        List<Bullet> trash = new ArrayList<>();

        for (Bullet bullet : bullets){
            if (bullet.y + bullet.height < 0)
                trash.add(bullet);
            bullet.y -= 50 * screenRatioY;

            for (Enemy enemy : enemys){

                if (Rect.intersects(enemy.getCollisionShape(),
                        bullet.getCollisionShape())){

                    score++;
                    enemy.y = screenY + 500;
                    bullet.y = -500;
                    enemy.wasShot = true;
                }
            }
        }
        for (Bullet bullet : trash)
            bullets.remove(bullet);

        for (Enemy enemy : enemys){

            enemy.y += enemy.speed; //Düşmanın tanka yaklaşma hızı
            if(enemy.y + enemy.height > screenY){

                if (!enemy.wasShot){
                    isGameOver = true;
                    return;
                }

                int side = (int) (12 * screenRatioY);
                enemy.speed = random.nextInt(side);

                if (enemy.speed < 7 * screenRatioY)
                    enemy.speed = (int) (7 * screenRatioY);

                enemy.y = -enemy.height;
                enemy.x = random.nextInt(screenX - enemy.width);
                enemy.wasShot = false;
            }

            if (Rect.intersects(enemy.getCollisionShape(), tank.getCollisionShape())){
                isGameOver = true;
                return;
            }
        }
    }
    private void Draw() {

        if (getHolder().getSurface().isValid()){
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.bg, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.bg, background2.x, background2.y, paint);

            for (Enemy enemy : enemys){
                canvas.drawBitmap(enemy.getEnemy(), enemy.x, enemy.y, paint);
            }
            canvas.drawText("Skor : " + score, 100, 100, paint);
            if (isGameOver) {
                isPlaying = false;

                canvas.drawBitmap(tank.getDead(), tank.x, tank.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                return;
            }
            for (Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            canvas.drawBitmap(tank.getTank(), tank.x, tank.y, paint);
            getHolder().unlockCanvasAndPost(canvas);
        }

    }

    private void waitBeforeExit() { // Game over olduğunda 3 saniye bekler ve ana ekrana geri döner
        try {
            Thread.sleep(3000);
            gameActivity.startActivity(new Intent(gameActivity, MainActivity.class));
            gameActivity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void Sleep() { // Beklemeler
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void Resume(){
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }
    public void Pause(){
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < screenX / 2){
                    tank.isMoveLeft = true;
                    tank.isMoveRight = false;
                }
                else if (event.getX() > screenX / 2){
                    tank.isMoveRight = true;
                    tank.isMoveLeft = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                tank.isMoveLeft = false;
                tank.isMoveRight = false;

                break;
        }
        return true;
    }
    public void newBullet()
    {
        if(!preferences.getBoolean("isMute", false))
            soundPool.play(sound, 1, 1, 0, 0, 1);
        Bullet bullet = new Bullet(getResources());

        bullet.y = tank.y;
        bullet.x = tank.x + (tank.width / 2) - (bullet.width / 2);
        bullets.add(bullet);

    }
    private void saveHighScore() { //Eğer önceki skordan yüksek skor alınmış ise ana ekranda göstermek için
        if (preferences.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }
    }
}
