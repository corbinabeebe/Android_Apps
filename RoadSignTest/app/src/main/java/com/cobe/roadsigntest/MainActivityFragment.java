package com.cobe.roadsigntest;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.support.v4.app.*;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * MainActivityFragment.java
 * Contains the Flag Quiz Logic
 **/
public class MainActivityFragment extends Fragment {

    private static final String TAG = "SignTest Activity";

    private static final int SIGNS_IN_TEST = 5;

    private List<String> fileNameList; // sign file names
    private List<String> testSignsList; //signs in current test
    private Set<String> typesSet; //type of signs in current test
    private String correctAnswer; //correct sign name for the current sign
    private int totalGuesses; //number of guesses made
    private int correctAnswers; //number of correct guesses
    private int guessRows; //number of rows to display guess buttons
    private SecureRandom random; //used to randomize the test
    private Handler handler; //used to delay loading of next sign
    private Animation shakeAnimation; //animation used for incorrect guess

    private LinearLayout testLinearLayout; //layout contains the quiz
    private TextView questionNumberTextView; //shows current question #
    private ImageView signImageView; //display sign
    private LinearLayout[] guessLinearLayouts; //rows of answer Buttons
    private TextView answerTextView; //display correct answer


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        fileNameList = new ArrayList<>();
        testSignsList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        //load the shake animation that's used for incorrect answers
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3); //animation repeats itself 3 times

        //get references to GUI components
        testLinearLayout = view.findViewById(R.id.testLinearLayout);
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        signImageView = view.findViewById(R.id.signImageView);
        guessLinearLayouts = new LinearLayout[4];

        guessLinearLayouts[0] = view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = view.findViewById(R.id.row4LinearLayout);

        answerTextView = view.findViewById(R.id.answerTextView);

        //configure listeners for the guess buttons
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        //set questionNumberTextView's text
        questionNumberTextView.setText(getString(R.string.question, 1, SIGNS_IN_TEST));

        return view; // return the fragment's view for display
    }

    public void updateGuessRows(SharedPreferences sharedPreferences) {
        //get the number of guess buttons that should be displayed
        String choices = sharedPreferences.getString(MainActivity.CHOICES, null);
        guessRows = Integer.parseInt(choices) / 2;

        //hide all guess button LinearLayouts
        for (LinearLayout layout : guessLinearLayouts) {
            layout.setVisibility(View.GONE);
        }

        //display appropriate guess button LinearLayouts
        for (int row = 0; row < guessRows; row++) {
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
        }
    }

    //update sign types for the test based on the values in SharedPreferences
    public void updateTypes(SharedPreferences sharedPreferences) {
        typesSet = sharedPreferences.getStringSet(MainActivity.TYPES, null);
    }

    //setup and start the next quiz
    public void resetQuiz() {
        //use AssetManager to get image file names for enabled sign types
        AssetManager assets = getActivity().getAssets();
        fileNameList.clear(); //empties list of image file names

        try {
            //loop through each type
            for (String type : typesSet) {
                //get list of all sign image files in this type
                String[] paths = assets.list(type);
                for (String path : paths) {
                    fileNameList.add(path.replace(".jpg", ""));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading image file names", e);
        }

        correctAnswers = 0; //reset number of correct answers
        totalGuesses = 0; //reset number of total guesses made
        testSignsList.clear(); //clear prior list of test signs

        int signCounter = 1;
        int numberOfSigns = fileNameList.size();

        //add SIGNS_IN_QUIZ random file names to the testSignsList
        while (signCounter <= SIGNS_IN_TEST) {
            int randomIndex = random.nextInt(numberOfSigns);

            //get random file file name
            String filename = fileNameList.get(randomIndex);

            //if type is enabled and it hasn't been chosen
            if (!testSignsList.contains(filename)) {
                testSignsList.add(filename); //add the file th the list
                ++signCounter;
            }
        }

        loadNextFlag();
    }

    //after the user guesses a correct sign, load the next sign
    private void loadNextFlag() {
        //get filename of the next sign and remove it from the list
        String nextImage = testSignsList.remove(0);
        correctAnswer = nextImage; //updated correct answer
        answerTextView.setText(""); //clear answer from answerTextView

        //display current question number
        questionNumberTextView.setText(getString(R.string.question, (correctAnswers + 1), SIGNS_IN_TEST));

        //extract the type from the next image's name
        String type = nextImage.substring(0, nextImage.indexOf('-'));

        //use AssetManager to load the next image from the assets folder
        AssetManager assets = getActivity().getAssets();

        //get InputStream to the asset representing the next sign and try to use the InputStream
        try (InputStream stream = assets.open(type + "/" + nextImage + ".jpg")) {
            //load the asset as a drawable and display the signImageView
            Drawable sign = Drawable.createFromStream(stream, nextImage);

            signImageView.setImageDrawable(sign);
        } catch (IOException e) {
            Log.e(TAG, "Error Loading" + nextImage, e);
        }

        Collections.shuffle(fileNameList); //shuffle file names

        //put the correct answer at the end of the fileNameList
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));
        //add selected amount of guess Buttons based on the value of guessRows
        for (int row = 0; row < guessRows; row++) {
            //place Buttons in the correct linearlayout
            for (int column = 0; column < guessLinearLayouts[row].getChildCount(); column++) {
                // get button reference to configure
                Button newGuessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                //get the sign name and set it as newGuessButton's text
                String filename = fileNameList.get((row * 2) + column);
                newGuessButton.setText(getSignName(filename));
            }
        }

        //randomly replace one button with the correct answer
        int row = random.nextInt(guessRows); //pick a random row
        int column = random.nextInt(2); //pick a random column

        LinearLayout randomRow = guessLinearLayouts[row]; //get the row
        String countryName = getSignName(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(countryName);
    }

    //parse sign file name and return sign name
    private String getSignName(String name) {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }

    //animates the entire testLinearLayout on or off screen
    private void animate(boolean animateOut) {
        //parent animation into the UI for the first flag
        if (correctAnswers == 0) {
            return;
        }

        //calculate center x and center y
        int centerX = (testLinearLayout.getLeft() + testLinearLayout.getRight()) / 2;
        int centerY = (testLinearLayout.getTop() + testLinearLayout.getBottom() / 2);

        //calculate animations radius
        int radius = Math.max(testLinearLayout.getWidth(), testLinearLayout.getHeight());

        Animator animator;

        //if the testLinearLayout should animate out rather than in
        if (animateOut) {
            //create circular reveal animation
            animator = ViewAnimationUtils.createCircularReveal(testLinearLayout, centerX, centerY, radius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                //called when animation finishes
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadNextFlag();
                }
            });
        } else { //if the testLinearLayout should animate in
            animator = ViewAnimationUtils.createCircularReveal(testLinearLayout, centerX, centerY, 0, radius);
        }
        animator.setDuration(300);
        animator.start();
    }

    //called when a guess Button is touched
    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Button guessButton = ((Button) view);
            String guess = guessButton.getText().toString();
            String answer = getSignName(correctAnswer);
            ++totalGuesses; //increment total number of guess by 1
            if (guess.equals(answer)) {//if guess is correct
                ++correctAnswers; //increment correct answer by 1

                //display correct answer in green
                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(getResources().getColor(R.color.correct_answer, getContext().getTheme()));

                disableButtons(); //disables all buttons

                //if user has correct answers in SIGNS_IN_TEST signs
                if (correctAnswers == SIGNS_IN_TEST) {
                    //DialogFragment to display test stats and start new test
                    DialogFragment testResults = new DialogFragment() {
                        //create an AlertDialog and return it

                        @NonNull
                        @Override
                        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getString(R.string.test_results))
                                    .setMessage(
                                    getString(R.string.results,
                                            totalGuesses,
                                            (1000 / (double) totalGuesses)
                                            )

                            )
                            .setPositiveButton(R.string.reset_quiz, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    resetQuiz();
                                }
                            });
                            return builder.create();
                        }
                    };

                    //use fragment manager to display the dialogfragment
                    testResults.setCancelable(false);
                    testResults.show(getFragmentManager(), "test results");
                }
                else {// answer is correct but quiz not over
                    //load the next flag after 1 second delay
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animate(true); // animate the flag off the screen
                        }
                    }, 1000); //milliseconds for 1-second delay
                }
            } else {
                //answer was incorrect
                signImageView.startAnimation(shakeAnimation); //play shake

                //display "Incorrect!" in red
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer, getContext().getTheme()));
                guessButton.setEnabled(true); //disable incorrect answer
            }
        }
    };

    //utility method that disables all answer Buttons
    private void disableButtons() {
        for(int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for(int i = 0; i < guessRow.getChildCount(); i++) {
                guessRow.getChildAt(i).setEnabled(false);
            }
        }
    }


}
