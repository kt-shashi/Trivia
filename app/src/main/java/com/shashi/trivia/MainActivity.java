package com.shashi.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.shashi.trivia.data.AnswerListAsyncResponse;
import com.shashi.trivia.data.QuestionBank;
import com.shashi.trivia.model.Question;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView questionTextView;
    private TextView questionCounterTextView;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionindex = 0;
    private List<Question> questionList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        nextButton = findViewById(R.id.next_Button);
        prevButton = findViewById(R.id.prev_Button);
        trueButton = findViewById(R.id.true_Button);
        falseButton = findViewById(R.id.false_Button);
        questionCounterTextView = findViewById(R.id.counter_Text);
        questionTextView = findViewById(R.id.question_TextView);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);


        questionTextView.setText("");
        questionCounterTextView.setText("");

        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                questionTextView.setText(questionArrayList.get(currentQuestionindex).getAnswer());
                questionCounterTextView.setText((currentQuestionindex+1)+" / "+questionArrayList.size());

                Log.d("Inside", "onCreate: " + questionArrayList);

            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev_Button:
                if (currentQuestionindex > 0) {
                    currentQuestionindex = (currentQuestionindex - 1) % questionList.size();
                    updateQuestion();
                }
                break;
            case R.id.next_Button:
                currentQuestionindex = (currentQuestionindex + 1) % questionList.size();
                updateQuestion();
                break;
            case R.id.true_Button:
                checkAnswer(true);
                updateQuestion();
                break;
            case R.id.false_Button:
                checkAnswer(false);
                updateQuestion();
                break;
        }
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionindex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText((currentQuestionindex+1)+" / "+questionList.size());
    }

    private void checkAnswer(boolean userChoice){

        boolean answerIsTrue=questionList.get(currentQuestionindex).isAnswerTrue();

        int toastMessageId=0;

        if(userChoice==answerIsTrue){
            fadeview();
            toastMessageId=R.string.correct_answer;
        }
        else{
            shakeAnimation();
            toastMessageId=R.string.wrong_answer;
        }

        Toast.makeText(MainActivity.this,toastMessageId,Toast.LENGTH_SHORT).show();
    }


    private void fadeview(){

        final CardView cardView=findViewById(R.id.cardView);

        AlphaAnimation alphaAnimation=new AlphaAnimation(1.0f,0.5f);

        alphaAnimation.setDuration(250);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);


        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                cardView.setCardBackgroundColor(Color.GRAY);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void shakeAnimation(){
        Animation shake= AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);

        final CardView cardView=findViewById(R.id.cardView);
        cardView.setAnimation(shake);


        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.GRAY);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
