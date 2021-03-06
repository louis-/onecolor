package com.lrubstudio.onecolor;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

/**
 * Created by louis on 15/01/16.
 */

public class GameGridDb
{
    GameGridDb(Context context)
    {
        _context = context;
    }

    GameGrid makeGameGrid(String level)
    {
        GameGrid grid = new GameGrid(_context);

        // set grid data from resources
        if (grid != null)
        {
            // colors
            for (int i = 1; i <= GameGrid.COLORS_MAX; i++)
            {
                String colorString = getString(level + "_color_" + i);
                if (colorString.length() >0)
                {
                    grid._colors[i-1] = getColor(colorString);
                    grid._nbColors++;
                }
                else
                    break;
            }

            grid._bkColor = getColor(getString(level + "_color_bkground"));
            grid._commandsColor = getColor(getString(level + "_color_commands"));

            // grid
            String boxes = getString(level + "_grid");
            grid._grid = new int[grid.GRID_LINES][grid.GRID_COLS];
            if (grid._grid != null)
                for(int i = 0; i < grid.GRID_LINES; i++)
                    for(int j = 0; j < grid.GRID_COLS; j++)
                        grid._grid[i][j] = Character.getNumericValue(boxes.charAt(i*grid.GRID_COLS+j))-Character.getNumericValue('0');

            // turns
            grid._turnsForStar = (int)getInteger(level + "_star");
            grid._turnsForPass = (int)getInteger(level + "_pass");
        }

        return grid;
    }

    //
    private Context _context;
    private static final int max_levels = 1;

    //
    private String getString(String resName)
    {
        int id = _context.getResources().getIdentifier(resName, "string", _context.getPackageName());
        if (id != 0)
            return _context.getResources().getString(id);
        else
            return "";
    }

    private int getInteger(String resName)
    {
        int id = _context.getResources().getIdentifier(resName, "integer", _context.getPackageName());
        if (id != 0)
            return _context.getResources().getInteger(id);
        else
            return 0;
    }

    private int getColor(String resName)
    {
        int color = 0;
        int idRes;

        try
        {
            // try a system color like "white" or "@color/material_grey_100"
            color = Color.parseColor(resName);
        }
        catch(IllegalArgumentException il)
        {
            // no, try an entry in colors.xml file
            idRes = _context.getResources().getIdentifier(resName, "color", _context.getPackageName());
            if (idRes != 0)
                color = _context.getResources().getColor(idRes);
        }

        return color;
    }

    private void showToast(String msg)
    {
        Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
    }
}
