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
import com.shashi.trivia.model.Score;
import com.shashi.trivia.util.Prefs;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private TextView questionTextView;
    private TextView questionCounterTextView;
    private TextView scoretextView;
    private TextView highestScore;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionindex = 0;
    private List<Question> questionList;
    private Prefs prefs;


    private Score score;
    private int scoreCounter = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        score = new Score();

        prefs = new Prefs(MainActivity.this);



        nextButton = findViewById(R.id.next_Button);
        prevButton = findViewById(R.id.prev_Button);
        trueButton = findViewById(R.id.true_Button);
        falseButton = findViewById(R.id.false_Button);
        questionCounterTextView = findViewById(R.id.counter_Text);
        questionTextView = findViewById(R.id.question_TextView);

        scoretextView = findViewById(R.id.score_textview);
        highestScore = findViewById(R.id.highest_score);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);



        questionCounterTextView.setText("");
        scoretextView.setText("");
        highestScore.setText("");


        questionList = new QuestionBank().getQuestions(new AnswerListAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {

                Log.d("SizeQuestion", String.valueOf(questionArrayList.size()));

                questionTextView.setText(questionArrayList.get(0).getAnswer());
                questionCounterTextView.setText((currentQuestionindex + 1) + " / " + questionArrayList.size());


                scoretextView.setText("Score 0");
                highestScore.setText("Best: " + prefs.getHighScore());

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
                goNext();
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


    private void addPoints() {
        scoreCounter += 5;
        score.setScore(scoreCounter);

        scoretextView.setText("Score: " + score.getScore());
    }

    private void detuctPoints() {
        if (scoreCounter != 0) {
            scoreCounter -= 2;
            score.setScore(scoreCounter);
        } else {
            scoreCounter = 0;
            score.setScore(scoreCounter);
        }
        scoretextView.setText("Score: " + score.getScore());
    }

    private void updateQuestion() {

        String question = questionList.get(currentQuestionindex).getAnswer();
        questionTextView.setText(question);
        questionCounterTextView.setText((currentQuestionindex + 1) + " / " + questionList.size());

    }

    private void checkAnswer(boolean userChoice) {

        boolean answerIsTrue = questionList.get(currentQuestionindex).isAnswerTrue();

        int toastMessageId = 0;

        if (userChoice == answerIsTrue) {
            addPoints();
            fadeview();
            toastMessageId = R.string.correct_answer;
        } else {
            detuctPoints();
            shakeAnimation();
            toastMessageId = R.string.wrong_answer;
        }


        Toast.makeText(MainActivity.this, toastMessageId, Toast.LENGTH_SHORT).show();
    }


    private void fadeview() {

        final CardView cardView = findViewById(R.id.cardView);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.5f);

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
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.shake_animation);

        final CardView cardView = findViewById(R.id.cardView);
        cardView.setAnimation(shake);


        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.RED);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.GRAY);
                goNext();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }


    private void goNext() {
        currentQuestionindex = (currentQuestionindex + 1) % questionList.size();
        updateQuestion();
    }

    @Override
    protected void onPause() {

        prefs.saveHighestScore(score.getScore());


        super.onPause();

    }
}
