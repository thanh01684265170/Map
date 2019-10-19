package hvcnbcvt_uddd.googleapi.View;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appolica.interactiveinfowindow.InfoWindow;
import com.appolica.interactiveinfowindow.InfoWindowManager;
import com.appolica.interactiveinfowindow.customview.TouchInterceptFrameLayout;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import hvcnbcvt_uddd.googleapi.Control.AddMarker;
import hvcnbcvt_uddd.googleapi.Model.DataSos;
import hvcnbcvt_uddd.googleapi.Model.MarkerManage;
import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.User;
import hvcnbcvt_uddd.googleapi.Model.datasosresponse.SosResponse;
import hvcnbcvt_uddd.googleapi.Model.datauserlistresponse.DataUsersSosResponse;
import hvcnbcvt_uddd.googleapi.R;
import hvcnbcvt_uddd.googleapi.View.fragment.PopupFragment;
import hvcnbcvt_uddd.googleapi.View.fragment.PopupListener;
import hvcnbcvt_uddd.googleapi.data.api.ApiBuilder;
import hvcnbcvt_uddd.googleapi.data.api.ApiInterface;
import hvcnbcvt_uddd.googleapi.data.database.PrefHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, View.OnClickListener, GoogleMap.OnMarkerClickListener, PopupListener {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int PATTERN_DASH_LENGTH_PX = 20;
    public static final int PATTERN_GAP_LENGTH_PX = 20;
    public static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    public static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);
    public static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);
    private static LatLng PLACE_YOU_GO;
    //Xử lý playmedia
    int check = 0;
    int nameCircle = 0;
    ArrayList<MarkerManage> arrayMarker;
    AddMarker addAddMarker = new AddMarker();
    MediaPlayer mediaPlayer;
    ApiInterface apiInterface;
    PrefHelper prefHelper;
    ImageView buttonSendHelp;
    private GoogleMap mMap;
    private ProgressDialog myProgressDialog;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 23487;
    private Polyline newPolyline;
    private LatLngBounds latlngBounds;
    private Marker marker = null;
    private Circle circle = null;
    private Location myLocation;
    private EditText edt_findAway;
    private ImageView img_gps;
    private ImageView imgProfile;
    private TextView txt_sos;
    private View btnSos;
    private float distance;
    private String phone;
    private String entityId = "5SRYWeGFNrFE8aUNcYnM";
    private String content;
    private InfoWindowManager infoWindowManager;
    private PopupFragment popupFragment;
    private InfoWindow formWindow;
    private String entityIDSOS = "";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<User> listUserHelp = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phone = getIntent().getStringExtra("PHONE");
        entityId = getIntent().getStringExtra("ENTITY_ID");
        content = getIntent().getStringExtra("CONTENT");

        initView();

        final TouchInterceptFrameLayout mapViewContainer =
                findViewById(R.id.mapViewContainer);

        infoWindowManager = new InfoWindowManager(getSupportFragmentManager());
        infoWindowManager.onParentViewCreated(mapViewContainer, savedInstanceState);

//        Cấp quyền truy cập với api 23 trở lên
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Khởi tạo google autocomplete
        if (!Places.isInitialized()) {
            Locale.setDefault(new Locale("vi", "vn"));
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.getDefault());
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

            Events();
            arrayMarker = addAddMarker.getArrayMarker();
            RightLocation();
        }
    }

    private void getSOS() {
        final String lat = getIntent().getStringExtra("LAT");
        final String lon = getIntent().getStringExtra("LON");

        Log.i("thanh", lat + "," + lon);

        if (lat != null & lon != null) {
            PLACE_YOU_GO = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
            marker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_32))
                    .title(entityId)
                    .position(PLACE_YOU_GO));

            marker.setVisible(false);

            //set possition pop up
            final int offsetX = (int) getResources().getDimension(R.dimen.marker_offset_x);
            final int offsetY = (int) getResources().getDimension(R.dimen.marker_offset_y);

            final InfoWindow.MarkerSpecification markerSpec =
                    new InfoWindow.MarkerSpecification(offsetX, offsetY);
            popupFragment = new PopupFragment(this);
            initDataInfoWindow();

            formWindow = new InfoWindow(marker, markerSpec, popupFragment);

            Glide.with(MapsActivity.this)
                    .asBitmap()
                    .load("https://scontent.fhan2-3.fna.fbcdn.net/v/t1.0-9/67818412_2217317141713228_6799879711511019520_n.jpg?_nc_cat=108&_nc_oc=AQnVX7HZ8wUh-2KKbDkL8Ea7RWkrelVBo89CQ1OkF0oXip06hkSr7uNskNgkVQuJ8I0&_nc_ht=scontent.fhan2-3.fna&oh=d161acc4a884f233272a6ab29d3b2b7b&oe=5E5FAF8D")
                    .fitCenter()
                    .circleCrop()
                    .into(new SimpleTarget<Bitmap>(80, 80) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                    .title(entityId)
                                    .position(PLACE_YOU_GO))
                            ;
                        }
                    });

            LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            latlngBounds = createLatLngBoundsObject(userLocation, PLACE_YOU_GO);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    latlngBounds, 800, 500, 50));
        }
    }

    //Handle show popup on click
    @Override
    public boolean onMarkerClick(Marker marker) {

        if (marker.getTitle().equalsIgnoreCase(entityId)) {
            infoWindowManager.toggle(formWindow, true);
            initDataInfoWindow();
        }

        return true;
    }

    private void initDataInfoWindow() {
        Log.d("Chinh1", "initDataInfoWindow");
        popupFragment.setTextViewContent(phone, content);
        apiInterface.getUsersSos(entityId).enqueue(new Callback<DataUsersSosResponse>() {
            @Override
            public void onResponse(Call<DataUsersSosResponse> call, Response<DataUsersSosResponse> response) {
                if (response.isSuccessful()) {
                    DataUsersSosResponse dataUsersSosResponse = response.body();
                    List<User> users = dataUsersSosResponse.getData().getUsers();
                    Log.d("Chinh1", "initDataInfoWindow onResponse: " + users.size());
                    popupFragment.setUserListData(users);
                }
            }

            @Override
            public void onFailure(Call<DataUsersSosResponse> call, Throwable t) {
                Log.d("Chinh1", "onFailure: " + t.toString());
            }
        });
    }

    private void initView() {
        buttonSendHelp = findViewById(R.id.img_send);
        prefHelper = new PrefHelper(this);
        edt_findAway = findViewById(R.id.edt_search);
        img_gps = findViewById(R.id.img_gps);
        imgProfile = findViewById(R.id.img_profile);
        btnSos = findViewById(R.id.button_sos);
        txt_sos = findViewById(R.id.txt_sos);
        apiInterface = ApiBuilder.getServiceApi(this);
    }

    private void Events() {
        buttonSendHelp.setOnClickListener(this);
        edt_findAway.setOnClickListener(this);
        img_gps.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        btnSos.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sos:
                sendSos();
                break;
            case R.id.edt_search:
                searchPlace();
                break;
            case R.id.img_gps:
                getDeviceLocation();
                break;
            case R.id.img_send:
                openSOSRequestActivity();
                break;
            case R.id.img_profile:
                openProfileActivity();
            default:
                break;
        }
    }

    private void listenListUsers(String entityIdSos) {
        Log.d("Mapsll", "listen");
        if (entityIdSos != null) {
            db.collection("entities").document(entityIdSos).collection("users")
                    .addSnapshotListener(MapsActivity.this, new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot snapshots,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Log.w("Mapsll", "listen:error", e);
                                return;
                            }

                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        User user = dc.getDocument().toObject(User.class);
                                        Log.d("Mapsll", "New: " + user.toString());
                                        listUserHelp.add(user);
                                        Log.d("Mapsll", "Size list: " + listUserHelp.size());
                                        reDrawMap(listUserHelp);
                                        break;
                                    case MODIFIED:
                                        Log.d("Mapsll", "Modified: " + dc.getDocument().getData());
                                        break;
                                    case REMOVED:
                                        Log.d("Mapsll", "Removed: " + dc.getDocument().getData());
                                        break;
                                }
                            }
                        }
                    });
        }
    }

    private void reDrawMap(List<User> users) {
        Log.d("Mapsll", "Size reDrawMap: " + listUserHelp.size());
        // Hiển thị marker
        for (int i = 0; i < listUserHelp.size(); i++) {
//            mMap.addMarker(new MarkerOptions().position(new LatLng(listUserHelp.get(i).getLocation().getLatitude(), listUserHelp.get(i).getLocation().getLongitude()))
//                    .title(arrayMarker.get(i).getTitle()).snippet(arrayMarker.get(i).getSnippet())
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_32)));

            final LatLng address = new LatLng(listUserHelp.get(i).getLocation().getLatitude(), listUserHelp.get(i).getLocation().getLongitude());
            Glide.with(MapsActivity.this)
                    .asBitmap()
                    .load("https://scontent.fhan2-4.fna.fbcdn.net/v/t1.0-9/68242494_2365249090357913_2795695690158702592_n.j" +
                            "pg?_nc_cat=105&_nc_oc=AQmqtmxHB_OimjxtSvt5hhpDxfRuDj_6cqCjUeAC9uGWnhyEtodREuHW5g4dj7nYyAk&_nc_ht=scont" +
                            "ent.fhan2-4.fna&oh=13fab8a3b68e2fc78bb5dfa3b4b1ee21&oe=5E27F384")
                    .fitCenter()
                    .circleCrop()
                    .into(new SimpleTarget<Bitmap>(80, 80) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            mMap.addMarker(new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromBitmap(resource))
                                    .title(entityId)
                                    .position(address));
                        }
                    });
        }


    }

    private void openProfileActivity() {
        Intent intent = new Intent(MapsActivity.this, ProfileActivity.class);
        MapsActivity.this.startActivity(intent);
    }

    private void openSOSRequestActivity() {
        Intent sosHelpActivityIntent = new Intent(this, SOSRequestActivity.class);
        sosHelpActivityIntent.putExtra("lat", myLocation.getLatitude());
        sosHelpActivityIntent.putExtra("lon", myLocation.getLongitude());
        startActivity(sosHelpActivityIntent);
    }

    private void sendRequestSos() {
        HashMap<String, Double> option = new HashMap<>();
        option.put("latitude", myLocation.getLatitude());
        option.put("longitude", myLocation.getLongitude());

        apiInterface.requestSOS(option).enqueue(new Callback<DataSos>() {
            @Override
            public void onResponse(Call<DataSos> call, Response<DataSos> response) {
                Log.d("MapsACtivityyy", "onResponse: ");
                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                entityIDSOS = response.body().getData().getEntity().getId();
                listenListUsers(entityIDSOS);
                Log.d("Mapsll", "onResponse: " + entityIDSOS);
            }

            @Override
            public void onFailure(Call<DataSos> call, Throwable t) {
                Log.d("MapsACtivityyy", "onFailure: ");
            }
        });
    }

    private void searchPlace() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("VN")
                .build(MapsActivity.this);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
    }

    //Handle noti
    private void sendSos() {
        if (txt_sos.getText().equals("SOS")) {
            sendRequestSos();
            txt_sos.setText("Hủy");
            //change color cricle
            if (circle != null) {
                circle.remove();
            }
            final LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15), 1500, null);
            circle = mMap.addCircle(new CircleOptions()
                    .center(userLocation)
                    .radius(500)
                    .strokeWidth(0f)
                    .fillColor(Color.argb(100, 250, 128, 114)));

        } else {
            sendSosCancel();
            mMap.clear();
            txt_sos.setText("SOS");
            getDeviceLocation();
        }
    }

    private void sendSosCancel() {
        String entityId = prefHelper.getString("AUTHORIZATION_KEY");
        HashMap<String, String> option = new HashMap<>();
        option.put("entityId", entityId);
        apiInterface.cancelSOS(option).enqueue(new Callback<DataSos>() {
            @Override
            public void onResponse(Call<DataSos> call, Response<DataSos> response) {
                Log.d("MapsACtivityyy", "onResponse: ");
                Toast.makeText(getApplicationContext(), "Canceled request!", Toast.LENGTH_LONG).show();
            }

            @Override

            public void onFailure(Call<DataSos> call, Throwable t) {
                Log.d("MapsACtivityyy", "onFailure: ");
            }
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (marker != null) {
                    marker.remove();
                }
                Place place = Autocomplete.getPlaceFromIntent(data);
                edt_findAway.setText(place.getName());
                marker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()).snippet("Nơi bạn đến."));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17));

                //Set vị trí vừa tìm được
                PLACE_YOU_GO = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                if (myLocation != null && PLACE_YOU_GO != null) {
                    findAway(myLocation.getLatitude(), myLocation.getLongitude(),
                            PLACE_YOU_GO.latitude, PLACE_YOU_GO.longitude);
                }

                //Hiển thị vùng bao quanh 2 điểm để hiển thị đủ trong màn hình
                LatLng userLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                latlngBounds = createLatLngBoundsObject(userLocation, PLACE_YOU_GO);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                        latlngBounds, 800, 500, 50));
//                mMap.setMyLocationEnabled(true);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Toast.makeText(this, status.getStatusMessage(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }


    public void findAway(double fromPositionDoubleLat,
                         double fromPositionDoubleLong, double toPositionDoubleLat,
                         double toPositionDoubleLong) {

        List<LatLng> path = new ArrayList();

        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey(getResources().getString(R.string.google_maps_key))
                .build();
        String start = fromPositionDoubleLat + "," + fromPositionDoubleLong;
        String end = toPositionDoubleLat + "," + toPositionDoubleLong;
        DirectionsApiRequest req = DirectionsApi.getDirections(context, start, end);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {

        }

        //Vẽ đường đi từ điểm đầu đến điểm cuối
        if (path.size() > 0) {
            if (newPolyline != null) {
                newPolyline.remove();
            }
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(10).pattern(PATTERN_POLYGON_ALPHA);
            newPolyline = mMap.addPolyline(opts);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Circle circle;
        mMap = googleMap;
        infoWindowManager.onMapReady(googleMap);
        //Lấy và hiển thị vị trí hiện tại
        getDeviceLocation();
        getSOS();


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
        googleMap.setOnMarkerClickListener(this);
    }

    //Lấy vị trí hiện tại
    private void getDeviceLocation() {
        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        myLocation = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

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
            if (circle != null) {
                circle.remove();
            }
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15), 1500, null);
            circle = mMap.addCircle(new CircleOptions()
                    .center(userLocation)
                    .radius(500)
                    .strokeWidth(0f)
                    .fillColor(Color.argb(100, 135, 206, 235)));
//            mMap.addMarker(new MarkerOptions().position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot1)));
            mMap.setMyLocationEnabled(true);
        }

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
        return r >= distance;
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

    @Override
    public void confirmCallBack() {
        //Dialog
        final ProgressDialog progressDialog = new ProgressDialog(MapsActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Đang gửi yêu cầu...");
        progressDialog.show();

        HashMap<String, String> option = new HashMap<>();
        option.put("entityId", entityId);
        option.put("accept", "true");
        apiInterface.responseSOS(option).enqueue(new Callback<SosResponse>() {
            @Override
            public void onResponse(Call<SosResponse> call, Response<SosResponse> response) {
                if (response.isSuccessful()) {
                    String responseMess = response.body().getMessage();
                    Toast.makeText(getApplicationContext(), responseMess, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<SosResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "request fail", Toast.LENGTH_SHORT).show();
                Log.d("chinh", "onFailure: " + t.toString());
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public void cancelCallBack() {
        marker.hideInfoWindow();
    }
}