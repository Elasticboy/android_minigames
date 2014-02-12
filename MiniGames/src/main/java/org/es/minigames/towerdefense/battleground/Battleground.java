package org.es.minigames.towerdefense.battleground;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import org.es.gameengine.drawable.DrawableElement;
import org.es.minigames.towerdefense.unit.Tower;

import java.util.HashSet;
import java.util.Set;

/**
 * Class that represents the battlefield.
 *
 * @author Cyril Leroux
 *         Created on 30/01/14.
 */
public class Battleground implements DrawableElement {

    private final float mColumnCount;
    private final float mRowCount;
    private final Tile[][] mTiles;
    private final Set<DrawableElement> mDrawables;
    private float mTileSize = 0;

    public Battleground(int columnCount, int rowCount) {
        mColumnCount = columnCount;
        mRowCount = rowCount;
        mTiles = BattlegroundHelper.initTiles(columnCount, rowCount);
        mDrawables = new HashSet<>();
    }

    @Override
    public void onUpdateSurfaceSize(int surfaceWidth, int surfaceHeight) {
        mTileSize = BattlegroundHelper.getTileSize(surfaceWidth, surfaceHeight, mColumnCount, mRowCount);
        BattlegroundHelper.updateTileSizes(mTiles, mTileSize);

        // update
        for (DrawableElement drawable : mDrawables) {
            drawable.onUpdateSurfaceSize(surfaceWidth, surfaceHeight);
        }

    }

    @Override
    public void draw(Canvas canvas) {

        // Draw the background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        canvas.drawRect(canvas.getClipBounds(), paint);

        // Draw the grid
        paint.setStrokeWidth(0.3f);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        for (Tile[] row : mTiles) {
            for (Tile tile : row) {
                canvas.drawRect(tile.getRect(), paint);
            }
        }

        // Draw the elements
        for (DrawableElement drawable : mDrawables) {
            drawable.draw(canvas);
        }

    }

    public Tile getTile(int x, int y) {
        return mTiles[y][x];
    }

    public void addTower(Tower tower, int x, int y) {
        mDrawables.add(tower);
        mTiles[y][x].bindUnit(tower);
    }
}
