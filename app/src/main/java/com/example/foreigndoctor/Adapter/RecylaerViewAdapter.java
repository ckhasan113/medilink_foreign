package com.example.foreigndoctor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foreigndoctor.R;
import com.example.foreigndoctor.pojo.DoctorDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecylaerViewAdapter extends RecyclerView.Adapter<RecylaerViewAdapter.MyViewHolder> {


    private Context context;
    private List<DoctorDetails> doctorDetails;

    private DoctorDetailsListener listener;

    public RecylaerViewAdapter(Context context, List<DoctorDetails> doctorDetails) {
        this.context = context;
        this.doctorDetails = doctorDetails;
        listener = (DoctorDetailsListener) context;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView DoctorName, DEgree,Specielity;
        public ImageView DoctorImage;

        LinearLayout getDetailsLayout;


        public MyViewHolder(View view) {
            super(view);

            getDetailsLayout = itemView.findViewById(R.id.getDoctorDetails);

            DoctorName = (TextView) view.findViewById(R.id.DoctorName22);
            DEgree = (TextView) view.findViewById(R.id.DoctorTitel22);
            Specielity = (TextView) view.findViewById(R.id.DoctorSpeciality22);
            DoctorImage = (ImageView) view.findViewById(R.id.DoctorImage);

        }
    }


    @NonNull
    @Override
    public RecylaerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecylaerViewAdapter.MyViewHolder holder, int position) {

        final DoctorDetails doctor = doctorDetails.get(position);


        holder.DoctorName.setText(doctor.getName());
        holder.DEgree.setText(doctor.getDegree());
        holder.Specielity.setText(doctor.getSpeciality());
        Picasso.get().load(doctor.getImageURI()).into(holder.DoctorImage);

        holder.getDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDoctorDetails(doctor);
            }
        });

    }

    @Override
    public int getItemCount() {
        return doctorDetails.size();
    }

    public interface DoctorDetailsListener{
        void onDoctorDetails(DoctorDetails doctorDetails);
    }
}
