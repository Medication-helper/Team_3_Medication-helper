package com.cookandroid.medication_helper;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class FirebaseUtils {
    public static void updateMedicineUsage(String medicineId, int delta) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("MedicineUsage").child(medicineId);

        ref.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    mutableData.setValue(delta);
                } else {
                    mutableData.setValue(currentValue + delta);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "updateMedicineUsage:onComplete:" + databaseError);
            }
        });
    }
}
