package com.example.lab_1_2_mayurpatel_c0828800_android.adaptors;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab_1_2_mayurpatel_c0828800_android.EditActivity;
import com.example.lab_1_2_mayurpatel_c0828800_android.R;
import com.example.lab_1_2_mayurpatel_c0828800_android.constants.Constants;
import com.example.lab_1_2_mayurpatel_c0828800_android.database.AppDatabase;
import com.example.lab_1_2_mayurpatel_c0828800_android.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdaptor extends RecyclerView.Adapter<ProductAdaptor.MyViewHolder> {
    private Context context;
    private List<Product> mProductList;

    public ProductAdaptor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdaptor.MyViewHolder myViewHolder, int i) {
        myViewHolder.name.setText(mProductList.get(i).getName());
        myViewHolder.description.setText(mProductList.get(i).getDescription());
        myViewHolder.price.setText(mProductList.get(i).getPrice());
        myViewHolder.latitude.setText(mProductList.get(i).getLatitude());
        myViewHolder.longitude.setText(mProductList.get(i).getLongitude());
    }

    @Override
    public int getItemCount() {
        if (mProductList == null) {
            return 0;
        }
        return mProductList.size();

    }

    public void setTasks(List<Product> productList) {
        mProductList = productList;
        notifyDataSetChanged();
    }

    public List<Product> getTasks() {
        return mProductList;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, description, price, latitude, longitude;
        ImageView editImage;
        AppDatabase mDb;

        MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            mDb = AppDatabase.getInstance(context);
            name = itemView.findViewById(R.id.productName);
            description = itemView.findViewById(R.id.productDescription);
            price = itemView.findViewById(R.id.productPrice);
            longitude = itemView.findViewById(R.id.PLon);
            latitude = itemView.findViewById(R.id.Plat);
            editImage = itemView.findViewById(R.id.edit_Image);
            editImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int elementId = mProductList.get(getAdapterPosition()).getId();
                    Intent i = new Intent(context, EditActivity.class);
                    i.putExtra(Constants.UPDATE_Product_Id, elementId);
                    context.startActivity(i);
                }
            });
        }
    }
    //filter the search
    public void filterTodos(List<Product> newPendingTodoModels){
        mProductList =new ArrayList<>();
        mProductList.addAll(newPendingTodoModels);
        notifyDataSetChanged();
    }
}