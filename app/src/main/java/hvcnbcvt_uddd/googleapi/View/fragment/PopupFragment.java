package hvcnbcvt_uddd.googleapi.View.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.User;
import hvcnbcvt_uddd.googleapi.R;
import hvcnbcvt_uddd.googleapi.View.adapter.MarkerUserAdapter;

public class PopupFragment extends Fragment implements View.OnClickListener {

    private TextView txtSosMessage;
    private TextView txtSosPhone;
    private Button btnConfirm;
    private Button btnCancel;
    private RecyclerView rvUserHelper;
    private List<User> userList = new ArrayList<>();
    private MarkerUserAdapter markerUserAdapter;

    String content = "";
    String phone = "";

    PopupListener popupListener;

    public PopupFragment(PopupListener listener) {
        popupListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.popup_marker, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);

        handleEvent();
    }

    private void handleEvent() {
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    public void setUserListData(List<User> listData) {
        userList.clear();
        userList.addAll(listData);
        if (markerUserAdapter != null) {
            markerUserAdapter.notifyDataSetChanged();
        }
    }

    public void setTextViewContent(String phone, String content) {
        this.phone = phone;
        this.content = content;
        if (txtSosMessage != null && txtSosPhone != null) {
            txtSosMessage.setText(content);
            txtSosPhone.setText(phone);
        }
    }

    private void initView(View v) {
        txtSosMessage = v.findViewById(R.id.txt_sos_message);
        txtSosPhone = v.findViewById(R.id.txt_sos_phone);
        rvUserHelper = v.findViewById(R.id.rv_user_helper);
        btnConfirm = v.findViewById(R.id.btn_confirm);
        btnCancel = v.findViewById(R.id.btn_cancel);

        txtSosMessage.setText(content);
        txtSosPhone.setText(phone);

        markerUserAdapter = new MarkerUserAdapter(userList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvUserHelper.setLayoutManager(layoutManager);
        rvUserHelper.setItemAnimator(new DefaultItemAnimator());
        rvUserHelper.setAdapter(markerUserAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                handleCannelEvent();
                break;
            case R.id.btn_confirm:
                handleConfirmEvent();
                break;
            default:
                break;
        }
    }

    private void handleCannelEvent() {
        popupListener.cancelCallBack();
    }

    private void handleConfirmEvent() {
        popupListener.confirmCallBack();
    }
}
