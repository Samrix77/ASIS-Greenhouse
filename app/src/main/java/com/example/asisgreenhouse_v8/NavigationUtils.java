// NavigationUtils.java
package com.example.asisgreenhouse_v8;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationUtils {

    public static void setupBottomNavigation(BottomNavigationView bottomNavigationView, Context context, int currentScreen) {
        // Set Home selected
        bottomNavigationView.setSelectedItemId(currentScreen);

        // Perform item selected listener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.store){
                    context.startActivity(new Intent(context, StoreActivity.class));
                    return true;
                }
                else if (itemId == R.id.notification){
                    return true;
                }
                else if (itemId == R.id.home){
                    context.startActivity(new Intent(context, MainActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}
