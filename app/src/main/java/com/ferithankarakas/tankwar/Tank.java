package com.ferithankarakas.tankwar;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.ferithankarakas.tankwar.GameView.screenRatioX;
import static com.ferithankarakas.tankwar.GameView.screenRatioY;

public class Tank {

    public boolean isMoveLeft = false, isMoveRight = false, exited = false;
    float x, y;
    private int col=0, row=0;
    int width, height;
    Bitmap tank, dead;
    private GameView gameView;

    Tank (GameView gameView, int screenY, int screenX, Resources res){

        this.gameView = gameView;

        tank = BitmapFactory.decodeResource(res, R.drawable.tank);

        width = tank.getWidth();
        height = tank.getHeight();

        width /= 4;
        height /= 4;

        width *= (int) screenRatioX;
        height *= (int) screenRatioY;
        tank = Bitmap.createScaledBitmap(tank, width, height, false);

        dead = BitmapFactory.decodeResource(res, R.drawable.bang);

        y = screenY - height - 64;
        x = (screenX / 2) - (width / 2);
    }
    Bitmap getTank(){ //Getter

        return tank;
    }
    Rect getCollisionShape()
    {
        int gx = (int) x;
        int gy = (int) y;
        return new Rect(gx, gy, gx + width, gy + height);
    }
    Bitmap getDead(){

        int deadWidth = dead.getWidth() / 6;
        int deadHeight = dead.getHeight() / 6;

        Bitmap bang = Bitmap.createBitmap(dead, col* deadWidth, row* deadHeight ,deadWidth,deadHeight);
        col++;
        if(col == 6)
        {
            col = 0;
            row++;
            if(row == 6)
                exited = true;

        }
        return bang;
        }

    }
