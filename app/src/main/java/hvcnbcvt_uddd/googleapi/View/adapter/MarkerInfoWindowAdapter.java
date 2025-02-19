package hvcnbcvt_uddd.googleapi.View.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import hvcnbcvt_uddd.googleapi.R;

public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;

    public MarkerInfoWindowAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoContents(Marker arg0) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.popup_marker, null);
        Button btn_confirm = v.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"Đồng ý",Toast.LENGTH_LONG);
            }
        });
        return v;
    }
}
