package com.example.ocksangyun.elevator;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

public class Detail extends AppCompatActivity {
    Intent intent;
    int elev_num;
    int elev_time;
    int elev_weight;

    private ImageView imageView;
    private TextView title, elev_avail, leftWeight;

    static final int Max_Weight = 1600;
    static final int weightPerson = 70;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        intent = getIntent();
        elev_num = intent.getIntExtra("elev_num", 0);
        elev_weight = intent.getIntExtra("elev_weight", 0);

        title = findViewById(R.id.title);
        elev_avail = findViewById(R.id.elev_avail);
        leftWeight = findViewById(R.id.leftWeight);
        imageView = findViewById(R.id.imageView);

        title.setText(elev_num + "번 엘리베이터 정보");
        if (elev_num == 0) {
            elev_avail.setText("B6, B5, B4, B3, 1, 3, 5, 7, 9");
            imageView.setImageResource(R.drawable.ic_0th);

        }
        if (elev_num == 1) {
            elev_avail.setText("(1, 3, 5, 7, 9)");
            imageView.setImageResource(R.drawable.ic_1th);
        }
        if (elev_num == 2) {
            elev_avail.setText("(1, 2, 4, 6, 8)");
            imageView.setImageResource(R.drawable.ic_2th);
        }
        if (elev_num == 3) {
            elev_avail.setText("(B6, B5, B4, B3, 1, 4, 6, 8)");
            imageView.setImageResource(R.drawable.ic_3th);
        }
        if (elev_num == 4) {
            elev_avail.setText("(B3, B2, B1, 1, 4, 6, 8)");
            imageView.setImageResource(R.drawable.ic_4th);
        }
        if (elev_num == 5) {
            elev_avail.setText("(B3, B2, B1, 1, 3, 5, 7, 9)");
            imageView.setImageResource(R.drawable.ic_5th);
        }
        if (elev_num == 6) {
            elev_avail.setText("(1, 5, 8, 11)");
            imageView.setImageResource(R.drawable.ic_6th);
        }
        if (elev_num == 7) {
            elev_avail.setText("(1, 4, 7, 10)");
            imageView.setImageResource(R.drawable.ic_7th);
        }
        if (elev_num == 8) {
            elev_avail.setText("(B3, B2, B1, 1, 3, 6, 9, 12)");
            imageView.setImageResource(R.drawable.ic_8th);
        }
        if (elev_num == 9) {
            elev_avail.setText("(B3, B2, B1, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)");
            imageView.setImageResource(R.drawable.ic_9th);
        }
        leftWeight.setText("남은 무게: " + (Max_Weight - elev_weight) + "kg (약 " + ""+ (Max_Weight - elev_weight) / weightPerson + "명 탑승 가능)");
    }
}
