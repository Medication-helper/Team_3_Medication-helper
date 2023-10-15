/****************************
 FirebaseUtils.java
 작성 팀 : [02-03]
 프로그램명 : Medication-Helper
 ***************************/

package com.cookandroid.medication_helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

public class FirebaseUtils {
    public static void updateSideCount(String sideType, int delta) { // sideType = 부작용 종류, delta = DB에서 증가시킬 수
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("SideCount").child(sideType); // Firebase의 SideCount DB와 연동

        /* ref 참조에 대해 트랜젝션 시작 */
        ref.runTransaction(new Transaction.Handler() {
            /* 트랜젝션 동안 수행되는 작업 */
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                Integer currentValue = mutableData.getValue(Integer.class); // 현재 SideCount 값이 있는지 확인
                if (currentValue == null) { // 없다면
                    mutableData.setValue(delta); // 새로운 값 설정
                } else { // 있다면
                    mutableData.setValue(currentValue + delta); // SideCount에 delta를 더함
                }

                return Transaction.success(mutableData); // 변경된 데이터 및 트랜젝션이 성공했음을 반환
            }

            /* 트랜젝션 완료 후 수행되는 기능이나 여기서는 딱히 필요없음. */
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean committed, @Nullable DataSnapshot dataSnapshot) {

            }
        });
    }
}
