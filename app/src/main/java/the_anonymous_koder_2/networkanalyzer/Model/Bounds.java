package the_anonymous_koder_2.networkanalyzer.Model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

/**
 * Created by Sahitya on 3/23/2018.
 */

public class Bounds {
    private int topLeftLatDeg;
    private int topLeftLatMin;
    private int topLeftLatSec;
    private int topRightLatDeg;
    private int topRightLatMin;
    private int topRightLatSec;
    private int bottomLeftLatDeg;
    private int bottomLeftLatMin;
    private int bottomLeftLatSec;
    private int bottomRightLatDeg;
    private int bottomRightLatMin;
    private int bottomRightLatSec;
    private int topLeftLngDeg;
    private int topLeftLngMin;
    private int topLeftLngSec;
    private int topRightLngDeg;
    private int topRightLngMin;
    private int topRightLngSec;
    private int bottomLeftLngDeg;
    private int bottomLeftLngMin;
    private int bottomLeftLngSec;
    private int bottomRightLngDeg;
    private int bottomRightLngMin;
    private int bottomRightLngSec;

    String TAG = "Bounds";

    private VisibleRegion visibleRegion;

    public Bounds(VisibleRegion visibleRegion) {
        this.visibleRegion = visibleRegion;
        setTopLeft(visibleRegion.farLeft);
        Log.d(TAG, "Bounds: FarLeft Cordinates"+visibleRegion.farLeft.longitude+" "+visibleRegion.farLeft.latitude);
        setTopRight(visibleRegion.farRight);
        setBottomLeft(visibleRegion.nearLeft);
        setBottomRight(visibleRegion.nearRight);
    }

    private void setBottomRight(LatLng value) {
        bottomRightLatDeg = (int)value.latitude;
        bottomRightLngDeg = (int)value.longitude;
        bottomRightLatMin = (int)((value.latitude % 1) * 60);
        bottomRightLngMin = (int)((value.longitude % 1) * 60);
        bottomRightLatSec = (int)((((value.latitude % 1) * 60) % 1) * 60);
        bottomRightLngSec = (int)((((value.longitude % 1) * 60) % 1) * 60);
    }

    private void setBottomLeft(LatLng value) {
        bottomLeftLatDeg = (int)value.latitude;
        bottomLeftLngDeg = (int)value.longitude;
        bottomLeftLatMin = (int)((value.latitude % 1) * 60);
        bottomLeftLngMin = (int)((value.longitude % 1) * 60);
        bottomLeftLatSec = (int)((((value.latitude % 1) * 60) % 1) * 60);
        bottomLeftLngSec = (int)((((value.longitude % 1) * 60) % 1) * 60);
    }

    private void setTopRight(LatLng value) {
        topRightLatDeg = (int)value.latitude;
        topRightLngDeg = (int)value.longitude;
        topRightLatMin = (int)((value.latitude % 1) * 60);
        topRightLngMin = (int)((value.longitude % 1) * 60);
        topRightLatSec = (int)((((value.latitude % 1) * 60) % 1) * 60);
        topRightLngSec = (int)((((value.longitude % 1) * 60) % 1) * 60);
    }

    private void setTopLeft(LatLng value) {
        topLeftLatDeg = (int)value.latitude;
        topLeftLngDeg = (int)value.longitude;
        topLeftLatMin = (int)((value.latitude % 1) * 60);
        topLeftLngMin = (int)((value.longitude % 1) * 60);
        topLeftLatSec = (int)((((value.latitude % 1) * 60) % 1) * 60);
        topLeftLngSec = (int)((((value.longitude % 1) * 60) % 1) * 60);
    }

    public int getTopLeftLatDeg() {
        return topLeftLatDeg;
    }

    public int getTopLeftLatMin() {
        return topLeftLatMin;
    }

    public int getTopLeftLatSec() {
        return topLeftLatSec;
    }

    public int getTopRightLatDeg() {
        return topRightLatDeg;
    }

    public int getTopRightLatMin() {
        return topRightLatMin;
    }

    public int getTopRightLatSec() {
        return topRightLatSec;
    }

    public int getBottomLeftLatDeg() {
        return bottomLeftLatDeg;
    }

    public int getBottomLeftLatMin() {
        return bottomLeftLatMin;
    }

    public int getBottomLeftLatSec() {
        return bottomLeftLatSec;
    }

    public int getBottomRightLatDeg() {
        return bottomRightLatDeg;
    }

    public int getBottomRightLatMin() {
        return bottomRightLatMin;
    }

    public int getBottomRightLatSec() {
        return bottomRightLatSec;
    }

    public int getTopLeftLngDeg() {
        return topLeftLngDeg;
    }

    public int getTopLeftLngMin() {
        return topLeftLngMin;
    }

    public int getTopLeftLngSec() {
        return topLeftLngSec;
    }

    public int getTopRightLngDeg() {
        return topRightLngDeg;
    }

    public int getTopRightLngMin() {
        return topRightLngMin;
    }

    public int getTopRightLngSec() {
        return topRightLngSec;
    }

    public int getBottomLeftLngDeg() {
        return bottomLeftLngDeg;
    }

    public int getBottomLeftLngMin() {
        return bottomLeftLngMin;
    }

    public int getBottomLeftLngSec() {
        return bottomLeftLngSec;
    }

    public int getBottomRightLngDeg() {
        return bottomRightLngDeg;
    }

    public int getBottomRightLngMin() {
        return bottomRightLngMin;
    }

    public int getBottomRightLngSec() {
        return bottomRightLngSec;
    }

    public VisibleRegion getVisibleRegion() {
        return visibleRegion;
    }
}
