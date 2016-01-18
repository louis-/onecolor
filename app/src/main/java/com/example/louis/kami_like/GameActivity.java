package com.example.louis.kami_like;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class GameActivity extends Activity implements SurfaceHolder.Callback
{
    GameGrid _grid;
    int _currentScreenWidth;
    int _currentScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_game);

        //
        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);
        surfaceView.setWillNotDraw(false);

        //
        Bundle bundle = getIntent().getExtras();
        GameGridDb gameGridDb = new GameGridDb(this);
        _grid = gameGridDb.makeGameGrid(bundle.getString("level"));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h)
    {
        _currentScreenWidth = w;
        _currentScreenHeight = h;
        surfaceCreated(holder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null)
        {
            _grid.draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            // first, play
            _grid.playAt(_currentScreenWidth, _currentScreenHeight, event.getX(), event.getY());

            // then refresh view
            surfaceCreated(((SurfaceView)findViewById(R.id.surfaceView)).getHolder());
        }
        return super.onTouchEvent(event);
    }

    public void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}