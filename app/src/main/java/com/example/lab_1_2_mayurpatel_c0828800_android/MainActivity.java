package com.example.lab_1_2_mayurpatel_c0828800_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.example.lab_1_2_mayurpatel_c0828800_android.adaptors.ProductAdaptor;
import com.example.lab_1_2_mayurpatel_c0828800_android.database.AppDatabase;
import com.example.lab_1_2_mayurpatel_c0828800_android.database.AppExecutors;
import com.example.lab_1_2_mayurpatel_c0828800_android.model.Product;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton floatingActionButton;
    private RecyclerView mRecyclerView;
    private ProductAdaptor mAdapter;
    private ProductAdaptor msearchAdapter;
    private AppDatabase mDb;
    private ArrayList<Product> productssss;

//    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        floatingActionButton = findViewById(R.id.addFAB);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EditActivity.class));
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = new ProductAdaptor(this);
        mRecyclerView.setAdapter(mAdapter);
        mDb = AppDatabase.getInstance(getApplicationContext());
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            // Called when a user swipes left or right on a ViewHolder
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Here is where you'll implement swipe to delete
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<Product> tasks = mAdapter.getTasks();
                        mDb.productDao().delete(tasks.get(position));
                        retrieveTasks();
                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);



    }


    @Override
    protected void onResume() {
        super.onResume();
        retrieveTasks();
    }

    private void retrieveTasks() {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Product> products = mDb.productDao().loadAllProduct();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.setTasks(products);
                    }
                });
            }
        });
    }
        // when I click search bar app crashes don't know why
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        MenuItem menuItem = menu.findItem(R.id.action_search);
//        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                newText = newText.toLowerCase();
//                ArrayList<Product> newProducts = new ArrayList<>();
//                for (Product prod : Collections.unmodifiableList(productssss)) {
//                    String getProductName = prod.getName().toLowerCase();
//                    String getProductDescription = prod.getDescription().toLowerCase();
//                    String getPrice = prod.getPrice().toLowerCase();
//                    String getLatitude = prod.getLatitude().toLowerCase();
//                    String getLongitude = prod.getLongitude().toLowerCase();
//
//                    if (getProductName.contains(newText) || getProductDescription.contains(newText) || getPrice.contains(newText) || getLatitude.contains(newText)|| getLongitude.contains(newText))
//                    {
//                        newProducts.add(prod);
//                    }
//                }
//                msearchAdapter.filterTodos(newProducts);
//                msearchAdapter.notifyDataSetChanged();
//                return true;
//            }
//        });
//        return super.onCreateOptionsMenu(menu);
//    }
}

