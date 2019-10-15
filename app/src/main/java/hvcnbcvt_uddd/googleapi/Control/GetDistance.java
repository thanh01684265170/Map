package hvcnbcvt_uddd.googleapi.Control;

import android.os.AsyncTask;


import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.lang.ref.WeakReference;
import java.util.Map;

import hvcnbcvt_uddd.googleapi.View.MapsActivity;

public class GetDistance extends
        AsyncTask<Map<String, String>, Object, Integer> {
    public static final String USER_CURRENT_LAT = "user_current_lat";
    public static final String USER_CURRENT_LONG = "user_current_long";
    public static final String DESTINATION_LAT = "destination_lat";
    public static final String DESTINATION_LONG = "destination_long";
    public static final String DIRECTIONS_MODE = "directions_mode";
    private WeakReference<MapsActivity> activity;
    private Exception exception;

    // private ProgressDialog progressDialog;

    public GetDistance(MapsActivity activity) {
        super();
        this.activity = new WeakReference<MapsActivity>(activity);


    }

    public void onPreExecute() {

    }

    @Override
    protected void onPostExecute(Integer distance) {
        super.onPostExecute(distance);
        // progressDialog.dismiss();
        try {
            if (exception == null && distance != null
                    && activity != null) {
                activity.get().getDistance(distance);
            } else {
                processException();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    @Override
    protected Integer doInBackground(Map<String, String>... params) {
        Map<String, String> paramMap = params[0];
        try {
            LatLng fromPosition = new LatLng(Double.valueOf(paramMap
                    .get(USER_CURRENT_LAT)), Double.valueOf(paramMap
                    .get(USER_CURRENT_LONG)));
            LatLng toPosition = new LatLng(Double.valueOf(paramMap
                    .get(DESTINATION_LAT)), Double.valueOf(paramMap
                    .get(DESTINATION_LONG)));
            Direction md = new Direction();
            Document doc = md.getDocument(fromPosition, toPosition,
                    paramMap.get(DIRECTIONS_MODE));
            Integer distance = md.getDistanceValue(doc);
            return distance;
        } catch (Exception e) {
            exception = e;
            e.printStackTrace();
            return null;
        }
    }

    private void processException() {

    }
}
