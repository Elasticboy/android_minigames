package org.es.minigames.towerdefense.battleground;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.es.minigames.R;

/**
 * A factory to build land tiles.
 *
 * @author Cyril Leroux
 *         Created on 17/02/14.
 */
public class TileFactory {

    private static Bitmap mGrass = null;

    public static Tile createTile(int columnId, int rowId, Tile.Type type, Resources resources) {

        switch (type) {
            case GRASS:
                default:
                return createGrassTile(columnId, rowId, resources);
        }
    }

    private static Tile createGrassTile(int columnId, int rowId, Resources  resources) {

        if (mGrass == null) {
            mGrass = BitmapFactory.decodeResource(resources, R.drawable.tile_grass_004);
        }
        return new Tile(columnId, rowId, mGrass);
    }
}