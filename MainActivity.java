package com.example.ocksangyun.elevator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.Comparator;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button startbtn;
    private Spinner startSpinner, endSpinner;
    private ArrayAdapter<CharSequence> arrayAdapter;
    private int startFloor, endFloor;
    private int[] buttonPressedArray;
    private Integer[][] result_time;
    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private CountDownTimer countDownTimer, countDownTimer1, countDownTimer2;

    static final String IP = "http://192.168.0.9";
    static final int CLOSING_TIME = 10;
    static final int MOVING_TIME = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startbtn = findViewById(R.id.startbtn);
        startSpinner = findViewById(R.id.startSpinner);
        endSpinner = findViewById(R.id.endSpinner);
        arrayAdapter = ArrayAdapter.createFromResource(this, R.array.floorArray, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startSpinner.setAdapter(arrayAdapter);
        endSpinner.setAdapter(arrayAdapter);

        textView1 = findViewById(R.id.resultTV1);
        textView2 = findViewById(R.id.resultTV2);
        textView3 = findViewById(R.id.resultTV3);


        startSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                startFloor = (int) parent.getSelectedItemId() + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        endSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                endFloor = (int) parent.getSelectedItemId() + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAsyncTask myAsyncTask = new MyAsyncTask();
                myAsyncTask.execute();
            }
        });
    }

    public class MyAsyncTask extends AsyncTask<String, Void, Elevator[]> {
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);

        OkHttpClient client = new OkHttpClient();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("\t로딩중");
            progressDialog.show();
        }

        @Override
        protected Elevator[] doInBackground(String... params) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(IP + "/PHP_connection.php").newBuilder();
            String url = urlBuilder.build().toString();

            Request request = new Request.Builder().url(url).build();

            try {
                Response response = client.newCall(request).execute();
                Gson gson = new GsonBuilder().create();
                JsonParser parser = new JsonParser();
                JsonElement rootObject = parser.parse(response.body().charStream()).getAsJsonObject().get("result");
                Elevator[] posts = gson.fromJson(rootObject, Elevator[].class);
                return posts;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Elevator[] result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            result_time = new Integer[10][2];

            for (int i = 0; i < 10; i++) {
                result_time[i][0] = i;
            }

            for (int i = 0; i < 10; i++) {
                getPressed(result[i], i);
            }
            sortArray(result_time);

           // textView1.setText("       " + result_time[9][1] + "초 후 도착 / " + result_time[9][0] + "번 엘리베이터");
            countDownTimer(result_time[6][1]);
            countDownTimer.start();
            CountDownTimer1(result_time[5][1]);
            countDownTimer1.start();
            CountDownTimer2(result_time[4][1]);
            countDownTimer2.start();



            if (endFloor == startFloor) {
                Toast.makeText(getApplicationContext(), "출발층과 도착층이 같습니다", Toast.LENGTH_SHORT).show();
                textView1.setText("");
                textView2.setText("");
                textView3.setText("");
            }

            textView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(MainActivity.this, Detail.class);
                    intent1.putExtra("elev_num", result_time[6][0]);
                    intent1.putExtra("elev_weight", result[result_time[6][0]].getWeight());
                    startActivity(intent1);
                }
            });
            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(MainActivity.this, Detail.class);
                    intent1.putExtra("elev_num", result_time[5][0]);
                    intent1.putExtra("elev_weight", result[result_time[5][0]].getWeight());
                    startActivity(intent1);
                }
            });
            textView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(MainActivity.this, Detail.class);
                    intent1.putExtra("elev_num", result_time[4][0]);
                    intent1.putExtra("elev_weight", result[result_time[4][0]].getWeight());
                    startActivity(intent1);
                }
            });
        }
    }

    protected void getPressed(Elevator result, int index) {
        buttonPressedArray = new int[18];
        int elevState = result.getUPorDown(); // 0: 정지, 1: 하행, 2: 상행
        int elevNow = 0;
        String elevNowString = result.getNow(); // 엘리베이터 현재위치
        switch (elevNowString) {
            case "B6":
                elevNow = 1;
                break;
            case "B5":
                elevNow = 2;
                break;
            case "B4":
                elevNow = 3;
                break;
            case "B3":
                elevNow = 4;
                break;
            case "B2":
                elevNow = 5;
                break;
            case "B1":
                elevNow = 6;
                break;
            case "A1":
                elevNow = 7;
                break;
            case "A2":
                elevNow = 8;
                break;
            case "A3":
                elevNow = 9;
                break;
            case "A4":
                elevNow = 10;
                break;
            case "A5":
                elevNow = 11;
                break;
            case "A6":
                elevNow = 12;
                break;
            case "A7":
                elevNow = 13;
                break;
            case "A8":
                elevNow = 14;
                break;
            case "A9":
                elevNow = 15;
                break;
            case "A10":
                elevNow = 16;
                break;
            case "A11":
                elevNow = 17;
                break;
            case "A12":
                elevNow = 18;
                break;
        }
        // 눌려있는 버튼 찾아서 buttonPressedArray에 넣는다
        if (result.getB6() == 1) {
            buttonPressedArray[0] = 1;
        }
        if (result.getB5() == 1) {
            buttonPressedArray[1] = 1;
        }
        if (result.getB4() == 1) {
            buttonPressedArray[2] = 1;
        }
        if (result.getB3() == 1) {
            buttonPressedArray[3] = 1;
        }
        if (result.getB2() == 1) {
            buttonPressedArray[4] = 1;
        }
        if (result.getB1() == 1) {
            buttonPressedArray[5] = 1;
        }
        if (result.getA1() == 1) {
            buttonPressedArray[6] = 1;
        }
        if (result.getA2() == 1) {
            buttonPressedArray[7] = 1;
        }
        if (result.getA3() == 1) {
            buttonPressedArray[8] = 1;
        }
        if (result.getA4() == 1) {
            buttonPressedArray[9] = 1;
        }
        if (result.getA5() == 1) {
            buttonPressedArray[10] = 1;
        }
        if (result.getA6() == 1) {
            buttonPressedArray[11] = 1;
        }
        if (result.getA7() == 1) {
            buttonPressedArray[12] = 1;
        }
        if (result.getA8() == 1) {
            buttonPressedArray[13] = 1;
        }
        if (result.getA9() == 1) {
            buttonPressedArray[14] = 1;
        }
        if (result.getA10() == 1) {
            buttonPressedArray[15] = 1;
        }
        if (result.getA11() == 1) {
            buttonPressedArray[16] = 1;
        }
        if (result.getA12() == 1) {
            buttonPressedArray[17] = 1;
        }
        calculateTIme(buttonPressedArray, elevState, index, elevNow);
    }

    public void calculateTIme(int[] buttonPressedArray, int elevState, int index, int elevNow) {

        int pressedBetween = 0;
        int mostHigh = 0;
        int mostLow = 0;

        for (int i = 17; i >= 0; i--) {
            if (buttonPressedArray[i] == 1) {
                mostHigh = i + 1;
                break;
            }
        }

        for (int i = 0; i <= 17; i++) {
            if (buttonPressedArray[i] == 1) {
                mostLow = i + 1;
                break;
            }
        }
        if (startFloor > endFloor) {
            for (int i = endFloor; i < startFloor - 1; i++) {
                if (buttonPressedArray[i] == 1) {
                    pressedBetween++;
                }
            }
        } else if (endFloor > startFloor) {
            for (int i = startFloor; i < endFloor - 1; i++) {
                if (buttonPressedArray[i] == 1) {
                    pressedBetween++;
                }
            }
        }
        if (elevNow >= startFloor) {
            if (elevState == 0) {
                result_time[index][1] = MOVING_TIME * (elevNow - startFloor);
            } else if (elevState == 1) {
                result_time[index][1] = MOVING_TIME * (elevNow - startFloor) + CLOSING_TIME * pressedBetween;
            } else if (elevState == 2) {
                result_time[index][1] = MOVING_TIME * (mostHigh - elevNow) + CLOSING_TIME * (pressedBetween + 1) + MOVING_TIME * (mostHigh - startFloor);
            }
        } else if (elevNow < startFloor) {
            if (elevState == 0) {
                result_time[index][1] = MOVING_TIME * (startFloor - elevNow);
            } else if (elevState == 1) {
                result_time[index][1] = MOVING_TIME * (startFloor - elevNow) + CLOSING_TIME * (pressedBetween + 1) + MOVING_TIME * (startFloor - mostLow);
            } else if (elevState == 2) {
                result_time[index][1] = MOVING_TIME * (startFloor - elevNow) + CLOSING_TIME * pressedBetween;
            }
        }


    }

    public void sortArray(Integer[][] arr) {
        Arrays.sort(arr, new Comparator<Object[]>() {
            @Override
            public int compare(Object[] o1, Object[] o2) {
                if (((Comparable) o1[1]).compareTo(o2[1]) < 0)
                    return 1;
                else
                    return -1;
            }
        });
    }

    public void countDownTimer(final int time) {
        countDownTimer = new CountDownTimer(time * 1000, 1000) {
            int time_in = time;

            @Override
            public void onTick(long millisUntilFinished) {
                textView1.setText("       " + time_in + "초 후 도착 / " + result_time[6][0] + "번 엘리베이터");
                time_in--;
            }

            @Override
            public void onFinish() {
                textView1.setText("                          도착");
            }
        };
    }
    public void CountDownTimer1(final int time) {
        countDownTimer1 = new CountDownTimer(time * 1000, 1000) {
            int time_in = time;
            @Override
            public void onTick(long millisUntilFinished) {
                textView2.setText("       " + time_in + "초 후 도착 / " + result_time[5][0] + "번 엘리베이터");
                time_in--;
            }

            @Override
            public void onFinish() {
                textView2.setText("                          도착");
            }
        };
    }
    public void CountDownTimer2(final int time) {
        countDownTimer2 = new CountDownTimer(time * 1000, 1000) {
            int time_in = time;
            @Override
            public void onTick(long millisUntilFinished) {
                textView3.setText("       " + time_in + "초 후 도착 / " + result_time[4][0] + "번 엘리베이터");
                time_in--;
            }

            @Override
            public void onFinish() {
                textView3.setText("                          도착");
            }
        };
    }
}


