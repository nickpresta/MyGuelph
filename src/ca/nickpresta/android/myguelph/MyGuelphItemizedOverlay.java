
package ca.nickpresta.android.myguelph;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import java.util.ArrayList;

public class MyGuelphItemizedOverlay extends ItemizedOverlay<OverlayItem> {

    private final ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private final int mMarkerHeight;
    private final Context mContext;

    public MyGuelphItemizedOverlay(Drawable marker, Context context) {
        super(boundCenterBottom(marker));
        mMarkerHeight = ((BitmapDrawable) marker).getBitmap().getHeight();
        mContext = context;
        populate();
    }

    public void addOverlay(OverlayItem overlay) {
        mOverlays.add(overlay);
        populate();
    }

    @Override
    protected OverlayItem createItem(int i) {
        return mOverlays.get(i);
    }

    @Override
    public int size() {
        return mOverlays.size();
    }

    @Override
    public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
        super.draw(canvas, mapView, shadow);

        // cycle through all overlays
        for (OverlayItem item : mOverlays) {
            // Converts lat/lng-Point to coordinates on the screen
            GeoPoint point = item.getPoint();
            Point markerBottomCenterCoords = new Point();
            mapView.getProjection().toPixels(point, markerBottomCenterCoords);

            /* Find the width and height of the title */
            TextPaint paintText = new TextPaint();
            Paint paintRect = new Paint();

            Rect rect = new Rect();
            final int TEXT_SIZE = mContext.getResources().getDimensionPixelSize(
                    R.dimen.map_text_size);
            paintText.setTextSize(TEXT_SIZE);
            paintText.getTextBounds(item.getTitle(), 0, item.getTitle().length(), rect);

            final int TITLE_MARGIN = mContext.getResources().getDimensionPixelSize(
                    R.dimen.map_text_margins);
            rect.inset(-TITLE_MARGIN, -TITLE_MARGIN);
            rect.offsetTo(markerBottomCenterCoords.x - rect.width() / 2,
                    markerBottomCenterCoords.y - mMarkerHeight - rect.height());

            paintText.setTextAlign(Paint.Align.CENTER);
            paintText.setAntiAlias(true);
            paintText.setTextSize(TEXT_SIZE);
            paintText.setARGB(255, 255, 255, 255);
            paintRect.setARGB(130, 0, 0, 0);

            canvas.drawRoundRect(new RectF(rect), 2, 2, paintRect);
            canvas.drawText(item.getTitle(), rect.left + rect.width() / 2,
                    rect.bottom - TITLE_MARGIN, paintText);
        }
    }

    public void addOverlay(int latitude, int longitude, String title, String snippet) {
        OverlayItem item;

        GeoPoint geopoint = new GeoPoint(latitude, longitude);
        item = new OverlayItem(geopoint, title, snippet);
        mOverlays.add(item);
        populate();

    }

    public void clear() {
        for (OverlayItem item : mOverlays) {
            mOverlays.remove(item);
        }
    }

}
