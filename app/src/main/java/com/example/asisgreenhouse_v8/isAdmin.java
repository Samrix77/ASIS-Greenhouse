package com.example.asisgreenhouse_v8;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class isAdmin {

    public interface AdminCheckCallback {
        void onCheckCompleted(boolean isAdmin);
    }

    public void checkIfUserIsAdmin(FirebaseAuth auth, AdminCheckCallback callback) {
        FirebaseUser currentUser = auth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            DatabaseReference userInfoRef = databaseReference.child("UserInfo");

            userInfoRef.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            // Check if the 'admin' attribute exists and is set to 1
                            Integer adminValue = child.child("admin").getValue(Integer.class);
                            boolean isAdmin = adminValue != null && adminValue.equals(1);
                            callback.onCheckCompleted(isAdmin);
                            return;
                        }
                    }
                    callback.onCheckCompleted(false);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle the error or pass the callback
                    callback.onCheckCompleted(false);
                }
            });
        } else {
            // User is not authenticated or email is null
            callback.onCheckCompleted(false);
        }
    }
}
