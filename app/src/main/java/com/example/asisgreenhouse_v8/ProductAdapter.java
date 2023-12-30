package com.example.asisgreenhouse_v8;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> {

    private DatabaseReference databaseProducts;
    private boolean isAdmin;
    Context mContext;


    public ProductAdapter(Context context, ArrayList<Product> products, DatabaseReference dbProducts, Boolean isAdmin) {
        super(context, 0, products);
        this.databaseProducts = dbProducts;
        this.isAdmin = isAdmin;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Product product = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_product, parent, false);
        }

        // Lookup view for data population
        TextView tvProductName = convertView.findViewById(R.id.tvProductName);
        TextView tvProductPrice = convertView.findViewById(R.id.tvProductPrice);
        ImageView ivProduct = convertView.findViewById(R.id.ivProduct);


        // Using Glide to load the image from a URL into the ImageView
        Glide.with(getContext())
                .load(product.getPhoto()) // make sure 'getPhoto()' method returns a valid image URL
                .placeholder(R.drawable.image_waiting) // Optional: a placeholder image
                .error(R.drawable.image_not_found) // Optional: an error image if the URL fails to load
                .into(ivProduct);


        Button btnDelete = convertView.findViewById(R.id.btnDelete);
        Button btnOrder = convertView.findViewById(R.id.btnOrder);
        ImageView etProductQuantity_add = convertView.findViewById(R.id.etProductQuantity_add);
        ImageView etProductQuantity_subtract = convertView.findViewById(R.id.etProductQuantity_subtract);
        EditText etProductQuantity = convertView.findViewById(R.id.etProductQuantity);
        btnDelete.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        btnOrder.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
        etProductQuantity.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
        etProductQuantity_add.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
        etProductQuantity_subtract.setVisibility(isAdmin ? View.GONE : View.VISIBLE);

        // Populate the data into the template view using the data object
        tvProductName.setText(product.getName());
        tvProductPrice.setText("$" + product.getPrice());
        // If you have an image URL, you can use a library like Picasso or Glide to load the image:
        // Glide.with(context).load(product.getPhoto()).into(ivProduct);


        etProductQuantity_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer theQua = Integer.parseInt(etProductQuantity.getText().toString()) + 1;
                etProductQuantity.setText(theQua.toString());
            }
        });

        etProductQuantity_subtract.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer theQua = Integer.parseInt(etProductQuantity.getText().toString());
                theQua = theQua - 1;
                if (theQua == 0 || theQua < 0)
                {
                    theQua = 1;
                }
                etProductQuantity.setText(theQua.toString());
            }
        });


        // Set up the delete button click listener
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product.getId() != null) {
                    databaseProducts.child(product.getId()).removeValue();
                    remove(product);
                    notifyDataSetChanged();
                }
            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product.getId() != null) {

                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();

                    if(currentUser == null){
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(intent);
                    }else{

                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference OrdersDatabaseReference = database.getReference("Orders");


                    Calendar calendar = Calendar.getInstance();
                    // Create a date format
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    // Format the current date and time
                    String formattedDateTime = dateFormat.format(calendar.getTime());


                    // Create a new product
                        String id = OrdersDatabaseReference.push().getKey(); // Generate a unique ID for the product
                        Order order = new Order();
                        order.setId(id);
                        order.setEmail(currentUser.getEmail().toString());
                        order.setProductName(product.getName());
                        Integer theQuantity = Integer.parseInt(etProductQuantity.getText().toString());
                        order.setQuantity(theQuantity);
                        double totalPrice = product.getPriceAsDouble() * theQuantity;
                        order.setProductPrice(String.valueOf(totalPrice));
                        order.setDatetime(formattedDateTime);
                        order.setApproved(false);
                        OrdersDatabaseReference.child(id).setValue(order);

                        Toast.makeText(mContext, "your order placed successfully", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }
}

