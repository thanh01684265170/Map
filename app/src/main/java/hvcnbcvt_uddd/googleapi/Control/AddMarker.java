package hvcnbcvt_uddd.googleapi.Control;

import java.util.ArrayList;

import hvcnbcvt_uddd.googleapi.Model.MarkerManage;
import hvcnbcvt_uddd.googleapi.R;

public class AddMarker {

    ArrayList<MarkerManage> arrayMarker;

    public ArrayList<MarkerManage> getArrayMarker() {
        arrayMarker = new ArrayList<>();
        arrayMarker.add(new MarkerManage(21.010885, 105.536139, "Thanh cafe", "Cafe đủ loại", R.raw.ve_phia_mua));
        arrayMarker.add(new MarkerManage(20.961814, 105.794403, "Home", "305-CT3", R.raw.yeu_la_tha_thu));
        arrayMarker.add(new MarkerManage(21.007746, 105.530661, "Test", "Trên đường", R.raw.voi_vang));
        arrayMarker.add(new MarkerManage(21.009837, 105.531239, "Test2", "Test giao diem", R.raw.xin_dung_lang_im));
        arrayMarker.add(new MarkerManage(20.980517, 105.787277, "Tòa A1", "Nơi làm việc", R.raw.faded));
        return arrayMarker;
    }
}
