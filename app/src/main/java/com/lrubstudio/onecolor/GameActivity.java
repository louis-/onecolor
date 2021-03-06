package com.lrubstudio.onecolor;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class GameActivity extends Activity implements SurfaceHolder.Callback
{
    GameGrid _grid;
    String _level;
    GameGridDb _gameGridDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // full screen window
        setContentView(R.layout.activity_game);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //
        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);

        //
        Bundle bundle = getIntent().getExtras();
        _gameGridDb = new GameGridDb(this);
        _level = bundle.getString("level");

        // create a grid matching with level
        _grid = _gameGridDb.makeGameGrid(_level);

        //
        hideSystemUI();

        //
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        GameGrid.setDensityDpi(metrics.densityDpi);
    }

    private void hideSystemUI()
    {
        SurfaceView surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        if (surfaceView != null) {
            surfaceView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            hideSystemUI();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int frmt, int w, int h)
    {
        _grid.setDimensions(w, h);
        surfaceCreated(holder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        Canvas canvas = holder.lockCanvas();
        if (canvas != null)
        {
            _grid.setDimensions(canvas.getWidth(), canvas.getHeight());
            _grid.draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void waitThenFinish(final int waitMs)
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                // wait
                try { Thread.sleep(waitMs); }
                catch(InterruptedException ie) { }

                // then finish
                finish();
            }
        }).start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if(event.getActionMasked() == MotionEvent.ACTION_DOWN)
        {
            float x = event.getX();
            float y = event.getY();
            int pressed = _grid.press(x, y);
            switch(pressed)
            {
                case GameGrid.PRESS_GRID:
                    // play
                    _grid.playAt(x, y, _grid._currentColor);
                    // then refresh view
                    surfaceCreated(((SurfaceView)findViewById(R.id.surfaceView)).getHolder());
                    // game won
                    if (_grid._gameStatus == GameGrid.GAME_WON_STAR)
                    {
                        // transmit result to MainActivity
                        Intent intent = getIntent();
                        intent.putExtra("GameLevel", _level);
                        intent.putExtra("GameResult", GameGrid.GAME_WON_STAR);
                        setResult(RESULT_OK, intent);

                        // splash and bye
                        SplashDlg.run(getFragmentManager(), SplashDlg.SPLASH_STAR);
                        waitThenFinish(2000);
                    }
                    else if (_grid._gameStatus == GameGrid.GAME_WON_PASSED)
                    {
                        // transmit result to MainActivity
                        Intent intent = getIntent();
                        intent.putExtra("GameLevel", _level);
                        intent.putExtra("GameResult", GameGrid.GAME_WON_PASSED);
                        setResult(RESULT_OK, intent);

                        // splash and bye
                        SplashDlg.run(getFragmentManager(), SplashDlg.SPLASH_HALFSTAR);
                        waitThenFinish(2000);
                    }
                    else if (_grid._gameStatus == GameGrid.GAME_WON_NOT_PASSED)
                    {
                        // transmit result to MainActivity
                        Intent intent = getIntent();
                        intent.putExtra("GameLevel", _level);
                        intent.putExtra("GameResult", GameGrid.GAME_WON_NOT_PASSED);
                        setResult(RESULT_OK, intent);

                        // splash and bye
                        SplashDlg.run(getFragmentManager(), SplashDlg.SPLASH_FAILED);
                        waitThenFinish(2000);
                    }
                    break;
                case GameGrid.PRESS_CLEAR:
                    // bye
                    Intent intent = getIntent();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                    break;
                case GameGrid.PRESS_REPLAY:
                    // new
                    _grid = _gameGridDb.makeGameGrid(_level);
                    // then refresh view
                    surfaceCreated(((SurfaceView)findViewById(R.id.surfaceView)).getHolder());
                    break;
                default:
                    // color button pressed
                    if (pressed >= GameGrid.PRESS_COLOR && pressed <= (GameGrid.PRESS_COLOR+_grid._nbColors))
                    {
                        // select new color
                        _grid._currentColor = pressed - GameGrid.PRESS_COLOR;
                        // then refresh view
                        surfaceCreated(((SurfaceView)findViewById(R.id.surfaceView)).getHolder());
                    }
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public void showToast(String msg)
    {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
