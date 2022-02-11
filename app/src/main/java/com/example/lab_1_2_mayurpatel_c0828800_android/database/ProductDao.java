package com.example.lab_1_2_mayurpatel_c0828800_android.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.lab_1_2_mayurpatel_c0828800_android.model.Product;

import java.util.List;
@Dao
public interface ProductDao {
    @Query("SELECT * FROM PRODUCT ORDER BY ID")
    List<Product> loadAllProduct();

    @Insert
    void insertProduct(Product product);

    @Update
    void updateProduct(Product product);

    @Delete
    void delete(Product product);

    @Query("SELECT * FROM PRODUCT WHERE id = :id")
    Product loadProductById(int id);
}
