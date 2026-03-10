package com.ab15.ballthread;


import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    FrameLayout container;
    Button btnCreate;

    int direction = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.container);
        btnCreate = findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(v -> createBall());
    }

    private void createBall() {

        View ball = new View(this);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(randomColor());

        ball.setBackground(shape);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(120,120);

        ball.setLayoutParams(params);

        container.addView(ball);

        int type = direction % 4;

        new Thread(new BallThread(ball,type)).start();

        direction++;
    }

    class BallThread implements Runnable{

        View ball;
        int type;

        int x = 0;
        int y = 0;

        Handler handler = new Handler();

        BallThread(View ball,int type){
            this.ball = ball;
            this.type = type;
        }

        @Override
        public void run() {

            while(true){

                handler.post(() -> {

                    switch(type){

                        case 0: //Top to Bottom
                            y += 10;
                            ball.setY(y);
                            break;

                        case 1: //Bottom to Top
                            y -= 10;
                            ball.setY(800 + y);
                            break;

                        case 2: //Left to Right
                            x += 10;
                            ball.setX(x);
                            break;

                        case 3: //Right to Left
                            x -= 10;
                            ball.setX(800 + x);
                            break;
                    }

                });

                try{
                    Thread.sleep(50);
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private int randomColor(){

        Random r = new Random();

        return android.graphics.Color.rgb(
                r.nextInt(255),
                r.nextInt(255),
                r.nextInt(255)
        );
    }
}