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
import com.example.foreigndoctor.pojo.ForeignBooking;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PatientRecylaerViewAdapter extends RecyclerView.Adapter<PatientRecylaerViewAdapter.MyViewHolder>{

    private Context context;
    private List<ForeignBooking> foreignBookings;

    private FHDetailsListener listener;

    public PatientRecylaerViewAdapter(Context context, List<ForeignBooking> foreignBookings) {
        this.context = context;
        this.foreignBookings = foreignBookings;
        listener = (FHDetailsListener) context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView PatientName,date,REfDoc;
        public ImageView patientImage;

        LinearLayout getDetailsLayout;


        public MyViewHolder(View view) {
            super(view);

            getDetailsLayout = itemView.findViewById(R.id.ReqDetails);

            PatientName = (TextView) view.findViewById(R.id.PatientName);
            REfDoc = (TextView) view.findViewById(R.id.refDoc);
            date = (TextView) view.findViewById(R.id.DateTV);
            patientImage = (ImageView) view.findViewById(R.id.PatientImage);

        }
    }

    @NonNull
    @Override
    public PatientRecylaerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.req_item, parent, false);

        return new PatientRecylaerViewAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientRecylaerViewAdapter.MyViewHolder holder, int position) {

        final ForeignBooking foreignBooking = foreignBookings.get(position);


        holder.PatientName.setText(foreignBooking.getPatientDetails().getFirstName()+" "+foreignBooking.getPatientDetails().getLastName());
        holder.REfDoc.setText(foreignBooking.getForeignDoctorDetails().getName());
        holder.date.setText(foreignBooking.getBookingDate());
        Picasso.get().load(foreignBooking.getPatientDetails().getImageURI()).into(holder.patientImage);


        holder.getDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFHDetails(foreignBooking);
            }
        });


    }

    @Override
    public int getItemCount() {
        return foreignBookings.size();
    }

    public interface FHDetailsListener{

        void onFHDetails(ForeignBooking foreignBooking);
    }

}
