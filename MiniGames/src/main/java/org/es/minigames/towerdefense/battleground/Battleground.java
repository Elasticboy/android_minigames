package org.es.minigames.towerdefense.battleground;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;

import org.es.engine.graphics.drawable.DrawableElement;
import org.es.engine.graphics.utils.DrawingParam;
import org.es.minigames.towerdefense.unit.Enemy;
import org.es.minigames.towerdefense.unit.Tower;

/**
 * Class that represents the battlefield.
 *
 * @author Cyril Leroux
 *         Created on 30/01/14.
 */
public class Battleground implements DrawableElement {

    private final float mColumnCount;
    private final float mRowCount;
    private final PointF mPosition;
    private final DrawingParam mDrawingParam;

    private final Tile[][] mTiles;
    private final Point[] mSpawnPoints;
    private final Point[] mGoals;

    public Battleground(int columnCount, int rowCount, Point[] spawnPoints, Point[] goals, Resources resources, DrawingParam drawingParam) {
        mColumnCount = columnCount;
        mRowCount = rowCount;
        mTiles = BattlegroundHelper.initTiles(columnCount, rowCount, resources);
        mSpawnPoints = spawnPoints;
        mGoals = goals;
        mPosition = new PointF(0, 0);
        mDrawingParam = drawingParam;
    }

    /**
     * The nearest goal point for the enemy.
     * The position is expressed in tiles. No coefficient applied.
     * @param enemy
     * @return The nearest goal point for the enemy.
     */
    public PointF getGoal(Enemy enemy) {
        // TODO return the closest goal
        Point pt = mGoals[0];
        Tile tile = mTiles[pt.y][pt.x];
        return new PointF(tile.getCenterX(), tile.getCenterY());
    }

    /** Spawn the enemy in the center of the selected spawn point. */
    // TODO Somehow allow to define out of range position but not too far from the border
    public void spawnEnemy(Enemy enemy, int spawnId) {
        Point spawn = mSpawnPoints[spawnId];
        Tile tile = mTiles[spawn.y][spawn.x];
        final float posX = tile.getCenterX() - enemy.getWidth() / 2f;
        final float posY = tile.getCenterY() - enemy.getHeight() / 2f;
        enemy.setPosition(posX, posY);
    }

    /** Add a tower to the center of the selected tile. */
    public boolean addTower(Tower tower, int columnId, int rowId) {

        Tile tile = mTiles[rowId][columnId];
        if (!tile.isBuildable()) {
            return false;
        }
        tile.bindUnit(tower);
        final float posX = tile.getCenterX() - tower.getWidth() / 2f;
        final float posY = tile.getCenterY() - tower.getHeight() / 2f;
        tower.setPosition(posX, posY);
        return true;
    }

    public Tile getTile(int x, int y) {
        return mTiles[y][x];
    }

    @Override
    public void onUpdateSurfaceSize(int surfaceWidth, int surfaceHeight) {
        float minSize = BattlegroundHelper.minTileSize(surfaceWidth, surfaceHeight, mColumnCount, mRowCount);
        mDrawingParam.setCoef(minSize);
        float offsetY = (surfaceHeight - getHeight() *  mDrawingParam.coef()) / 2f;
        mDrawingParam.setOffset(0, offsetY);
    }

    @Override
    public void draw(Canvas canvas, DrawingParam param) {

        // TODO to delete
        // Draw the background
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        canvas.drawRect(canvas.getClipBounds(), paint);

        for (Tile[] row : mTiles) {
            for (Tile tile : row) {
                tile.draw(canvas, param);
            }
        }
    }

    @Override
    public float getPosX() { return mPosition.x; }

    @Override
    public float getPosY() { return mPosition.y; }

    @Override
    public void setPosition(float x, float y) { mPosition.set(x, y); }

    @Override
    public float getWidth() { return mColumnCount; }

    @Override
    public float getHeight() { return mRowCount; }
}
