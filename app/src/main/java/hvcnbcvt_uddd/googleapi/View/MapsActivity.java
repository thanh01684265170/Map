package hvcnbcvt_uddd.googleapi.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hvcnbcvt_uddd.googleapi.Control.AddMarker;
import hvcnbcvt_uddd.googleapi.Control.Direction;
import hvcnbcvt_uddd.googleapi.Control.GetDirections;
import hvcnbcvt_uddd.googleapi.Control.GetDistance;
import hvcnbcvt_uddd.googleapi.Model.MarkerManage;
import hvcnbcvt_uddd.googleapi.R;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private ProgressDialog myProgressDialog;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static LatLng PLACE_YOU_GO;
    private Polyline newPolyline;
    private LatLngBounds latlngBounds;

    private EditText edt_findAway;
    private TextView tv_Distance, tv_Duration;
    private ImageView img_gps, img_compass, img_camera;
    private float distance;

    //Xử lý playmedia
    int check = 0;
    int nameCircle = 0;

    ArrayList<MarkerManage> arrayMarker;
    AddMarker addAddMarker = new AddMarker();
    MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        Cấp quyền truy cập với api 23 trở lên
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            //Tạo Progress Bar
            myProgressDialog = new ProgressDialog(this);
            myProgressDialog.setTitle("Đang tải Map ...");
            myProgressDialog.setMessage("Vui lòng chờ...");
            myProgressDialog.setCancelable(true);
            //Hiển thị Progress Bar
            myProgressDialog.show();
            //Lấy đối tượng Google Map ra:
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            //thiết lập sự kiện đã tải Map thành công
            mapFragment.getMapAsync(this);

            Controls();
            Events();
            arrayMarker = addAddMarker.getArrayMarker();
            RightLocation();
        }
    }

    private void Events() {
        edt_findAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(MapsActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                } catch (GooglePlayServicesNotAvailableException e) {
                }
            }
        });

        img_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDeviceLocation();
            }
        });

        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, ARActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Controls() {
        edt_findAway = (EditText) findViewById(R.id.edt_findAway);
        tv_Distance = (TextView) findViewById(R.id.tv_Distance);
        tv_Duration = (TextView) findViewById(R.id.tv_Duration);
        img_gps = (ImageView) findViewById(R.id.img_gps);
        img_compass = (ImageView) findViewById(R.id.img_compass);
        img_camera = (ImageView) findViewById(R.id.img_camera);
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                edt_findAway.setText(place.getName());
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()).snippet("Nơi bạn đến."));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17));

                //Set vị trí vừa tìm được
                PLACE_YOU_GO = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);

                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                @SuppressLint("MissingPermission") Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);


                if (myLocation == null) {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                    String provider = lm.getBestProvider(criteria, true);
                    myLocation = lm.getLastKnownLocation(provider);
                }

                if (myLocation != null && PLACE_YOU_GO != null) {
                    findDirections(myLocation.getLatitude(), myLocation.getLongitude(),
                            PLACE_YOU_GO.latitude, PLACE_YOU_GO.longitude,
                            Direction.MODE_DRIVING);
                }

                //Hiển thị vùng bao quanh 2 điểm để hiển thị đủ trong màn hình
                LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                latlngBounds = createLatLngBoundsObject(userLocation, PLACE_YOU_GO);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                        latlngBounds, 400, 400, 80));
                mMap.setMyLocationEnabled(true);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Circle circle;
        mMap = googleMap;
        //Lấy và hiển thị vị trí hiện tại
        getDeviceLocation();

        // Hiển thị marker
        for (int i = 0; i < arrayMarker.size(); i++) {
            circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(arrayMarker.get(i).getLatitude(), arrayMarker.get(i).getLongitute()))
                    .radius(240)
                    .strokeColor(Color.YELLOW)
                    .fillColor(Color.argb(100, 255, 0, 0))
                    .strokeWidth(2));
            mMap.addMarker(new MarkerOptions().position(new LatLng(arrayMarker.get(i).getLatitude(), arrayMarker.get(i).getLongitute()))
                    .title(arrayMarker.get(i).getTitle()).snippet(arrayMarker.get(i).getSnippet())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_32)));
        }

        // kết thúc dialog nếu load bản đồ xong
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                myProgressDialog.dismiss();
            }
        });
    }

    //Lấy vị trí hiện tại
    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if (myLocation == null) {
            Criteria criteria = new Criteria();
            //cho biết độ chính xác gần đúng trước đó nếu không tìm được vị trí hiện tại
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            myLocation = lm.getLastKnownLocation(provider);
        }

        if (myLocation != null) {
            LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            //Hiệu ứng quay camera về vị trí hiện tại
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15), 1500, null);
        }

    }

    // in ra khoang cach
    public void getDistance(Integer distance) {
        float dis;
        dis = ((float) Math.round((((float) distance) / 1000) * 10)) / 10;
        tv_Distance.setText(dis + " km");
        int h = (int) (dis / 40);
        int m = Math.round(((dis / 40) - (float) h) * 60);
        if (h > 0) {
            if (m > 0)
                tv_Duration.setText(h + " giờ " + m + " phút");
            else
                tv_Duration.setText(h + " giờ");
        } else {
            tv_Duration.setText(m + " phút");
        }
    }

    //Vẽ đường đi từ điểm đầu đến điểm cuối
    public void handleGetDirectionsResult(ArrayList<LatLng> directionPoints) {
        PolylineOptions rectLine = new PolylineOptions().width(5).color(
                Color.RED);

        for (int i = 0; i < directionPoints.size(); i++) {
            rectLine.add(directionPoints.get(i));
        }
        if (newPolyline != null) {
            newPolyline.remove();
        }
        newPolyline = mMap.addPolyline(rectLine);
    }


    //Vẽ bản đồ chứa đủ cả 2 điểm
    private LatLngBounds createLatLngBoundsObject(LatLng firstLocation,
                                                  LatLng secondLocation) {
        if (firstLocation != null && secondLocation != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(firstLocation).include(secondLocation);

            return builder.build();
        }
        return null;
    }

    //Tìm đường
    public void findDirections(double fromPositionDoubleLat,
                               double fromPositionDoubleLong, double toPositionDoubleLat,
                               double toPositionDoubleLong, String mode) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirections.USER_CURRENT_LAT,
                String.valueOf(fromPositionDoubleLat));
        map.put(GetDirections.USER_CURRENT_LONG,
                String.valueOf(fromPositionDoubleLong));
        map.put(GetDirections.DESTINATION_LAT,
                String.valueOf(toPositionDoubleLat));
        map.put(GetDirections.DESTINATION_LONG,
                String.valueOf(toPositionDoubleLong));
        map.put(GetDirections.DIRECTIONS_MODE, mode);

        GetDirections asyncTask = new GetDirections(this);
        asyncTask.execute(map);

        GetDistance getDistance = new GetDistance(this);
        getDistance.execute(map);
    }


    // Lắng nghe sự thay đổi vị trí
    private void RightLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
    }

    //Hàm so sánh khoảng cách
    private boolean compareLocation(double lat, double log, double r) {
        //check xem đã được người dùng cấp phép chưa
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            myLocation = lm.getLastKnownLocation(provider);
        }


        Location newLocation = new Location("go");
        newLocation.setLatitude(lat);
        newLocation.setLongitude(log);
        distance = myLocation.distanceTo(newLocation);
        if (r >= distance) {
            return true;
        } else {
            return false;
        }
    }

    //Hàm tìm điểm gần nhất
    public int nearPoitMost() {
        int a = 0;
        float min;

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location myLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (myLocation == null) {
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            String provider = lm.getBestProvider(criteria, true);
            myLocation = lm.getLastKnownLocation(provider);
        }
        Location newLocation = new Location("find");
        newLocation.setLatitude(arrayMarker.get(0).getLatitude());
        newLocation.setLongitude(arrayMarker.get(0).getLongitute());
        min = myLocation.distanceTo(newLocation);

        for (int i = 1; i < arrayMarker.size(); i++) {
            Location FindPoint = new Location("findpoint");
            FindPoint.setLatitude(arrayMarker.get(i).getLatitude());
            FindPoint.setLongitude(arrayMarker.get(i).getLongitute());
            distance = myLocation.distanceTo(FindPoint);
            if (distance < min) {
                min = distance;
                a = i;
            }
        }
        return a;
    }

    //Kiểm tra xự thay đổi vị trí
    @Override
    public void onLocationChanged(Location location) {
        int i = nearPoitMost();
        //Kiểm tra xem có nằm trong bán kính của điểm gần nhất ko
        if (compareLocation(arrayMarker.get(i).getLatitude(), arrayMarker.get(i).getLongitute(), 240)) {
            Log.d("test", "kiemtra = " + check + "Trong vong");
            if (check == 0) {
                nameCircle = i;
                check = 1;
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
                mediaPlayer = MediaPlayer.create(MapsActivity.this, arrayMarker.get(i).getFile());
                mediaPlayer.start();
            }
        } else {
            check = 0;
        }
        if (nameCircle != i) {
            //kiểm tra sự thay đổi khi chuyển giao 2 vòng trùng nhau
            check = 0;
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            //Tạo Progress Bar
            myProgressDialog = new ProgressDialog(this);
            myProgressDialog.setTitle("Đang tải Map ...");
            myProgressDialog.setMessage("Vui lòng chờ...");
            myProgressDialog.setCancelable(true);
            //Hiển thị Progress Bar
            myProgressDialog.show();
            //Lấy đối tượng Google Map ra:
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            //thiết lập sự kiện đã tải Map thành công
            mapFragment.getMapAsync(this);

            Controls();
            Events();
            arrayMarker = addAddMarker.getArrayMarker();
            RightLocation();
        } else {
            Toast.makeText(this, "chưa có permisson", Toast.LENGTH_LONG).show();
        }
    }

    //Check vị trí
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Thông báo cho người dùng biết
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

/*                Hiển thị lời giải thích cho người dùng * không đồng bộ * - không chặn
                Chủ đề này đang chờ phản hồi của người dùng! Sau khi người dùng
                Xem giải thích, hãy thử lại để yêu cầu sự cho phép.
                  Nhắc người dùng khi giải thích đã được hiển thị        */

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
//                Không cần giải thích, chúng tôi có thể yêu cầu sự cho phép.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
}
