package hvcnbcvt_uddd.googleapi.View.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import hvcnbcvt_uddd.googleapi.Model.dataloginresponse.User;
import hvcnbcvt_uddd.googleapi.R;

public class MarkerUserAdapter extends RecyclerView.Adapter<MarkerUserAdapter.MyViewHolder> {

    private List<User> users;

    public MarkerUserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public MarkerUserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_marker, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MarkerUserAdapter.MyViewHolder holder, int position) {
        User user = users.get(position);
        holder.txtUserName.setText(user.getName());
        holder.txtUserPhone.setText(user.getPhone());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public AppCompatTextView txtUserName, txtUserPhone;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txt_item_name);
            txtUserPhone = itemView.findViewById(R.id.txt_item_phone);
        }
    }
}

