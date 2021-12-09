package com.example.apptracnghiem;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.apptracnghiem.model.Category;
import com.example.apptracnghiem.model.Question;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

public class QuestionActivity extends AppCompatActivity {

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCategory;
    private TextView textViewCountDown;

    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button buttonConfirmNext;

    private CountDownTimer countDownTimer;
    private ArrayList<Question> questionList;
    private long timeLeftInMillis;
    private int questionCounter;
    private int questionSize;

    private int Score;
    private boolean answered;
    private Question currentQuestion;

    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        anhxa();

        //nhan du lieu chu de
        Intent intent = getIntent();
        int categoryID = intent.getIntExtra("idcategories",0);
        String categoryName = intent.getStringExtra("categoriesname");
        //hien thi chu de
        textViewCategory.setText("Chủ đề : " +categoryName);
        
        Database database = new Database(this);
        //danh sach list chua cau hoi
        questionList = database.getQuestions(categoryID);
        //lay kich co danh sach  = tong so cau hoi
        questionSize = questionList.size();
        //dao vi tri cac phan tu cau hoi
        Collections.shuffle(questionList);
        //show cau hoi va dap an
        showNextQuestion();
        //button xac nha, cau tiep hoan thanh
        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //neu chua tra loi cau hoi
                if(!answered){
                    //neu chon 1 trong 3 dap an
                    if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()){
                        //kiem tra dap an
                        checkAnswer();
                    }
                    else {
                        Toast.makeText(QuestionActivity.this,"Hãy chọn đáp án",Toast.LENGTH_SHORT).show();
                    }
                }
                //neu tra loi, thuc hien chuyen cau hoi
                else {
                    showNextQuestion();
                }
            }
        });
        

    }
    //hien thi cau hoi
    private void showNextQuestion() {
        //set lai mau den cho dap an
        rb1.setTextColor(Color.BLACK);
        rb2.setTextColor(Color.BLACK);
        rb3.setTextColor(Color.BLACK);
        rb4.setTextColor(Color.BLACK);
        //xoa chon
        rbGroup.clearCheck();
        //neu con cau hoi
        if (questionCounter < questionSize){
            //lay du lieu ow vi tri counter
            currentQuestion = questionList.get(questionCounter);
            //hien thi cau hoi
            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());
            //tong sau moi cau hoi
            questionCounter++;
            //set vi tri cau hoi hien tai
            textViewQuestionCount.setText("Câu hỏi : "+questionCounter+" / "+questionSize);
            //gia tri false, chua tra loi, dang show
            answered = false;
            //gan ten cho button
            buttonConfirmNext.setText("Xác Nhận");
            //thời gian chạy 30s
            timeLeftInMillis = 30000;
            //dem nguoc thoi gian tra loi
            startCountDown();
            
        }
        else {
            finisQuestion();
        }
    }
    //phuong thuc thoi gian dem nguoc
    private void startCountDown() {
        countDownTimer = new CountDownTimer(timeLeftInMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                //update time
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                //het gio
                timeLeftInMillis = 0;
                updateCountDownText();
                //phuong thuc kiem tra dap an
                checkAnswer();
            }
        }.start();
    }
    //kiem tra dap an
    private void checkAnswer() {
        //true da tra loi
        answered = true;
        //tra ve radiobutton trong fbgroup duoc check
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        //vi tri cua cau da chon
        int answer = rbGroup.indexOfChild(rbSelected) + 1;
        //neu tra loi dung dap an
        if (answer == currentQuestion.getAnswer()){
            //tang 10 diem
            Score = Score +  10;
            //hien thi
            textViewScore.setText("Điểm : "+Score);
        }
        //phuong thuc hien thi dap an
        showSolution();
    }
    //dap an
    private void showSolution() {
        //set mau cho radiobutton dap an
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);
        rb4.setTextColor(Color.RED);
        //kiem tra dap an set mau va hien thi dap an len man hinh
        switch (currentQuestion.getAnswer()){
            case 1:
                rb1.setTextColor(Color.GREEN);
                textViewQuestion.setText("Đáp án là A");
                break;
            case 2:
                rb2.setTextColor(Color.GREEN);
                textViewQuestion.setText("Đáp án là B");
                break;
            case 3:
                rb3.setTextColor(Color.GREEN);
                textViewQuestion.setText("Đáp án là C");
                break;
            case 4:
                rb4.setTextColor(Color.GREEN);
                textViewQuestion.setText("Đáp án là D");
                break;
        }
        //neu con cau tra loi thi button se settext la cau tiep
        if(questionCounter < questionSize){
            buttonConfirmNext.setText("Câu tiếp");
        }
        //setText hoan thanh
        else {
            buttonConfirmNext.setText("Hoàn thành");
        }
        //dùng thời gian lại
        countDownTimer.cancel();
    }

    //update thoi gian
    private void updateCountDownText() {
        //tinh phut
        int minutes = (int) ((timeLeftInMillis/1000)/60);
        //tinh giay
        int seconds = (int) ((timeLeftInMillis/1000)%60);
        //dinh dang kieu time
        String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        //hien thi len man hinh
        textViewCountDown.setText(timeFormatted);
        //neu thoi gian duoi 10s thi chuyen mau do
        if (timeLeftInMillis <10000){
            textViewCountDown.setTextColor(Color.RED);
        }
        //khong thi van mau den
        else {
            textViewCountDown.setTextColor(Color.BLACK);
        }
    }

    //thoat qua giao dien chinh
    private void finisQuestion() {
        //chua du lieu gui qua activity main
        Intent resultIntent = new Intent();
        resultIntent.putExtra("score",Score);
        setResult(RESULT_OK,resultIntent);
        finish();
    }
    //back
    @Override
    public void onBackPressed() {
        count++;
        if(count>=1){
            finisQuestion();
        }
        count = 0;
    }

    //anh xa id
    private void anhxa(){
        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCategory = findViewById(R.id.text_view_category);

        textViewCountDown = findViewById(R.id.text_view_countdown);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);

        buttonConfirmNext = findViewById(R.id.button_confim_next);
    }
}