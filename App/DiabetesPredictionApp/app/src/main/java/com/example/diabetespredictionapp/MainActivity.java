package com.example.diabetespredictionapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diabetespredictionapp.ml.DiabetesModel;
import com.google.android.material.textfield.TextInputEditText;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {


    Button predict_button;
    TextInputEditText inputText1,inputText2, inputText3, inputText4, inputText5, inputText6;
    Boolean valid;
    protected int preProcess(float[] arr) throws IOException {
        valid = true;
//        AGE
        if (arr[3]<= 16 && arr[3]>0) arr[2]=0;
        else if(arr[3] > 16 && arr[3] <32) arr[3]=1;
        else if(arr[3] > 32 && arr[3] <48) arr[3]=2;
        else if(arr[3] > 48 && arr[3] <64) arr[3]=3;
        else if(arr[3] > 64 && arr[3] <120) arr[3]=4;
        else{
            inputText6.requestFocus();
            inputText6.setError("Are you a Ghost!!!");
            valid = false;
        }
//        Glucose
        if (arr[0]> 0 && arr[0]<80) arr[0]=0;
        else if(arr[0] > 80 && arr[0] <=100) arr[0]=1;
        else if(arr[0] > 100 && arr[0] <=125) arr[0]=2;
        else if(arr[0] > 125 && arr[0] <150) arr[0]=3;
        else if(arr[0] > 150) arr[0]=4;

//        BP
        if (arr[1]> 0 && arr[1]<=50) arr[1]=0;
        else if(arr[1] > 50 && arr[1] <=65) arr[1]=1;
        else if(arr[1] > 65 && arr[1] <=80) arr[1]=2;
        else if(arr[1] > 80 && arr[1] <100) arr[1]=3;
        else if(arr[1] > 100) arr[1]=4;


        float[] mean = {(float) 2.23778502, (float) 1.93322476, (float) 32.62351893, (float) 1.5732899};
        float[] std = {(float) 1.14806407, (float) 0.79724915, (float) 6.98896781, (float) 0.77438844};

        for(int i=0; i<arr.length; i++){
            arr[i] = (arr[i]-mean[i]) / std[i];
        }

//        try {
            DiabetesModel model = DiabetesModel.newInstance(MainActivity.this);
            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 4}, DataType.FLOAT32);
            inputFeature0.loadArray(arr);

            // Runs model inference and gets result.
            DiabetesModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();
            float[] r = outputFeature0.getFloatArray();
//        } catch (IOException e) {
//            // TODO Handle the exception
//        }

        if(r[0] > 0.5) return 1;
        else return 0;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText2 = findViewById(R.id.inputText2);
        inputText3 = findViewById(R.id.inputText3);
        inputText5 = findViewById(R.id.inputText5);
        inputText6 = findViewById(R.id.inputText6);
        predict_button = findViewById(R.id.diabPredictButton);

        predict_button.setOnClickListener(v -> {
            if (validateInfo(inputText2) && validateInfo(inputText3) && validateInfo(inputText5) && validateInfo(inputText6)){
                String inputText2str = inputText2.getText().toString();
                String inputText3str = inputText3.getText().toString();
                String inputText5str = inputText5.getText().toString();
                String inputText6str = inputText6.getText().toString();

                float input2 = Float.parseFloat(inputText2str);
                float input3 = Float.parseFloat(inputText3str);
                float input5 = Float.parseFloat(inputText5str);
                float input6 = Float.parseFloat(inputText6str);

                float[] intArray = {input2, input3, input5, input6};
                int resultArray = 0;
                try {
                    resultArray = preProcess(intArray);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(valid) {
                    System.out.println("==========================================================");
                    System.out.println(resultArray);
                    System.out.println("==========================================================");

                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("message_key", String.valueOf(resultArray));
                    startActivity(intent);
                }
            }else{
//                Toast.makeText(getApplicationContext(), "Check info again!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private Boolean validateInfo(TextInputEditText data){
        if (data.length()==0){
            data.requestFocus();
            data.setError("Field cannot be empty!!");
            return false;
        }else{
            return true;
        }
    }

}

