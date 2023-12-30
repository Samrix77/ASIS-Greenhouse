package com.example.asisgreenhouse_v8;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

public class OrderAdapter extends ArrayAdapter<Order> {

    private DatabaseReference databaseOrders;
    private boolean isAdmin;
    Context mContext;

    public OrderAdapter(Context context, ArrayList<Order> orders, DatabaseReference dbOrders, Boolean isAdmin) {
        super(context, 0, orders);
        this.databaseOrders = dbOrders;
        this.isAdmin = isAdmin;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Order order = getItem(position);

        // Initialize Firebase Auth and get the current user
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_order, parent, false);
        }

        // Lookup view for data population
        LinearLayout lo_order_record = convertView.findViewById(R.id.lo_order_record);
        TextView tvOrderID = convertView.findViewById(R.id.tvOrderID);
        TextView tvOrderEmail = convertView.findViewById(R.id.tvOrderEmail);
        TextView tvOrderDatetime = convertView.findViewById(R.id.tvOrderDatetime);
        TextView tvOrderProductName = convertView.findViewById(R.id.tvOrderProductName);
        TextView tvOrderQuantity = convertView.findViewById(R.id.tvOrderQuantity);
        TextView tvOrderProductPrice = convertView.findViewById(R.id.tvOrderProductPrice);

        Button btnApprove = convertView.findViewById(R.id.btnApprove);
        Button btnDisapprove = convertView.findViewById(R.id.btnDisapprove);

        // Set visibility based on admin status
        btnApprove.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnDisapprove.setVisibility(isAdmin ? View.VISIBLE : View.GONE);

        // Check if the current user is the order owner or an admin
        if ((currentUser != null && order.getEmail().equalsIgnoreCase(currentUser.getEmail())) || isAdmin) {
            // Populate the data into the template view using the data object
            tvOrderID.setText(order.getId());
            tvOrderEmail.setText(order.getEmail());
            tvOrderDatetime.setText(order.getDatetime());
            tvOrderProductName.setText(order.getProductName());
            tvOrderQuantity.setText(order.getQuantity().toString());
            tvOrderProductPrice.setText("$" + order.getProductPrice());

            if(order.getApproved())
            {
                lo_order_record.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.green));
            }

            // Set up the disapprove button click listener
            btnDisapprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (order.getId() != null) {
                        databaseOrders.child(order.getId()).removeValue();
                        remove(order);
                        notifyDataSetChanged();
                    }
                }
            });

            // Set up the approve button click listener
            btnApprove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (order.getId() != null) {

                        if(currentUser == null){
                            Intent intent = new Intent(mContext, LoginActivity.class);
                            mContext.startActivity(intent);
                        }else{

                            databaseOrders.child(order.getId()).child("approved").setValue(true).addOnSuccessListener(aVoid -> {
                                // Handle success
                                Toast.makeText(mContext, "Order approved successfully", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                // Handle failure
                                Toast.makeText(mContext, "Error updating order", Toast.LENGTH_SHORT).show();
                            });

                            notifyDataSetChanged();

                        }
                    }
                }
            });

        } else {
            // Hide the order details or show some message if the current user is not the owner or an admin
            convertView.setVisibility(View.GONE);
            // Alternatively, you could set the visibility of individual views to GONE
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
