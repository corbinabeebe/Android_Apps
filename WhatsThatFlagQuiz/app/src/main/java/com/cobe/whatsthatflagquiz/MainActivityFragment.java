package com.cobe.whatsthatflagquiz;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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
import java.lang.reflect.AnnotatedElement;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A fragment used for the flag quiz
 */
public class MainActivityFragment extends Fragment {
    //strings to use for logging error messages
    private static final String TAG = "WTFQuiz Activity";

    private static final int FLAGS_IN_QUIZ = 10;

    private List<String> fileNameList; // flag file names
    private List<String> quizCountriesList; //countries in current quiz
    private Set<String> regionsSet; //regions of the world in the quiz
    private String correctAnswer; //correct country for current flag
    private int totalGuesses; //number of guesses made
    private int correctAnswers; //number of correct guesses
    private int guessRows; //number of rows displaying guess buttons
    private SecureRandom random; //randomizes quiz
    private Handler handler; //used to delay loading next flag
    private Animation shakeAnimation; //animation for incorrect guesses

    private LinearLayout quizLinearLayout; //layout that contains the quiz
    private TextView questionNumberTextView; //shows current question #
    private ImageView flagImageView; //shows image of a flag
    private LinearLayout[] guessLinearLayouts; //rows od answer buttons
    private TextView answerTextView; //displays correct answer

    public MainActivityFragment() {
    }

    //configures MainActivityFragment when View is created
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        fileNameList = new ArrayList<>();
        quizCountriesList = new ArrayList<>();
        random = new SecureRandom();
        handler = new Handler();

        //load shake animation for incorrect answers
        shakeAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.incorrect_shake);
        shakeAnimation.setRepeatCount(3);

        //get references for GUI components
        quizLinearLayout = view.findViewById(R.id.quizLinearLayout);
        questionNumberTextView = view.findViewById(R.id.questionNumberTextView);
        flagImageView = view.findViewById(R.id.flagImageView);

        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = view.findViewById(R.id.row4LinearLayout);

        answerTextView = view.findViewById(R.id.answerTextView);

        //configure listeners for guess buttons
        for (LinearLayout row : guessLinearLayouts) {
            for (int column = 0; column < row.getChildCount(); column++) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        questionNumberTextView.setText(getString(R.string.question, 1, FLAGS_IN_QUIZ));
        return view; //return the fragment's view for the display
    }

    //update guessRows based on value in Shared Preferences
    public void updateGuessRows(SharedPreferences sharedPreferences) {
        //get the number of guess buttons to display
        String choices =
                sharedPreferences.getString(MainActivity.CHOICES, null);

        guessRows = Integer.parseInt(choices) / 2;

        //hide all linear layout guess buttons
        for (LinearLayout layout : guessLinearLayouts) {
            layout.setVisibility((View.GONE));
        }

        //displays LinearLayouts to align with amount of buttons
        for (int row = 0; row < guessRows; row++) {
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
        }
    }

    public void updateRegions(SharedPreferences sharedPreferences) {
        regionsSet = sharedPreferences.getStringSet(MainActivity.REGIONS, null);
    }

    public void resetQuiz() {
        //use AssetManager to get image file names for enabled regions
        AssetManager assets = getActivity().getAssets();
        fileNameList.clear(); //empty list of image file names

        try {
            //loop through each region
            for (String region : regionsSet) {
                //get list of all flag image files in teh specified region
                String[] paths = assets.list(region);

                for (String path : paths) {
                    fileNameList.add(path.replace(".png", ""));
                }
            }
        } catch (IOException exception) {
            Log.e(TAG, "Error loading image files", exception);
        }

        correctAnswers = 0; //sets total correctAnswers back to 0
        totalGuesses = 0; //sets total number of guesses back to 0
        quizCountriesList.clear(); //clears previous list of quiz countries

        int flagCounter = 1;
        int numberOfFlags = fileNameList.size();

        //add flags in quiz randomly to the quizCountriesList
        while (flagCounter <= FLAGS_IN_QUIZ) {
            int randomIndex = random.nextInt(numberOfFlags);

            //get the random file name
            String filename = fileNameList.get(randomIndex);

            //if the region is enabled and hasnt been chosen
            if (!quizCountriesList.contains(filename)) {
                quizCountriesList.add(filename); //adds the file to the list
                ++flagCounter;
            }
        }

        loadNextFlag(); //start the quiz by loading the first flag
    }

    //after the user guesses a correct flag, load the next flag
    private void loadNextFlag() {
        //get file name of the next flag and remove from the list
        String nextImage = quizCountriesList.remove(0);
        correctAnswer = nextImage; //updates correct answer
        answerTextView.setText(""); // clears answerTextView

        //display current question #
        questionNumberTextView.setText(getString(
                R.string.question, (correctAnswers + 1), FLAGS_IN_QUIZ));

        //extract the region from the next image
        String region = nextImage.substring(0, nextImage.indexOf("-"));

        //use AssetManager to load next image from assets folder
        AssetManager assets = getActivity().getAssets();

        //get an InputStream to the asset representing the next flag and try to use inputstream
        try (InputStream stream =
                     assets.open(region + "/" + nextImage + ".png")) {
            //load the asset as drawable and display on flagImageView
            Drawable flag = Drawable.createFromStream(stream, nextImage);
            flagImageView.setImageDrawable(flag);

            animate(false); //animate the flag onto the screen
        }
        catch (IOException exception) {
            Log.e(TAG, "Error loading " + nextImage, exception);
        }

        Collections.shuffle(fileNameList); //shuffles file names

        //put the correct answer at the end of fileNameList
        int correct = fileNameList.indexOf(correctAnswer);
        fileNameList.add(fileNameList.remove(correct));

        //add 2, 4, 6 or 8 guess buttons based on guessRows value
        for (int row = 0; row < guessRows; row++) {
            //place buttons in current table row
            for (int column = 0;
                 column < guessLinearLayouts[row].getChildCount();
                 column++) {
                //get reference button to configure
                Button newGuessButton =
                        (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                //get country name and set it as newGuessButton's text
                String filename = fileNameList.get((row * 2) + column);
                newGuessButton.setText(getCountryName(filename));
            }
        }

        //randomly replace one button with the correct answer
        int row = random.nextInt(guessRows); //pick a random row
        int column = random.nextInt(2); //pick random column
        LinearLayout randomRow = guessLinearLayouts[row]; //gets the row
        String countryName = getCountryName(correctAnswer);
        ((Button) randomRow.getChildAt(column)).setText(countryName);
    }

    //parse the country flag file name and return the country name
    private String getCountryName(String name) {
        return name.substring(name.indexOf('-') + 1).replace('_', ' ');
    }

    //animates the quizLinearLayout on or off the screen
    private void animate(boolean animateOut) {
        //prevents animation into the UI for the first flag
        if (correctAnswers == 0) {
            return;
        }

        //calculate center x and center y
        int centerX = (quizLinearLayout.getLeft() + quizLinearLayout.getRight()) / 2;
        int centerY = (quizLinearLayout.getTop() + quizLinearLayout.getBottom()) / 2;

        //calculate animation radius
        int radius = Math.max(quizLinearLayout.getWidth(), quizLinearLayout.getHeight());

        Animator animator;

        //if quizLinearLayout should animate out or in
        if (animateOut) {
            //create circular animation
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, radius, 0);
            animator.addListener(new AnimatorListenerAdapter() {
                //called when animation finishes
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadNextFlag();
                }
            });
        } else { //quizLinearLayout will animate in
            animator = ViewAnimationUtils.createCircularReveal(quizLinearLayout, centerX, centerY, 0, radius);
        }

        animator.setDuration(600);
        animator.start();
    }

    //called when guess Button is touched
    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();
            String answer = getCountryName(correctAnswer);
            ++totalGuesses;

            if (guess.equals(answer)) {//if the guess is correct
                ++correctAnswers; //increment correctAnswers by 1

                //display correct answer in green text
                answerTextView.setText(answer + "!");
                answerTextView.setTextColor(
                        getResources().getColor(R.color.correct_answer, getContext().getTheme()));

                disableButtons(); //disable all guess buttons

                //if user has correctly identified FLAGS_IN_QUIZ flags
                if (correctAnswers == FLAGS_IN_QUIZ) {
                    //DialogFragments to display quiz stats and start new quiz
                    DialogFragment quizResults =
                            new DialogFragment() {
                        //create an AlertDialog and return it
                        @Override
                        public Dialog onCreateDialog(Bundle bundle) {
                            AlertDialog.Builder builder =
                                    new AlertDialog.Builder(getActivity());
                            builder.setMessage(
                                    getString(R.string.results,
                                            totalGuesses,
                                            (1000 / (double) totalGuesses)));

                            //Reset quiz button
                            builder.setPositiveButton(R.string.reset_quiz,
                                    new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int id) {
                                    resetQuiz();
                                }
                            });

                            return builder.create(); // return Alert Dialog
                        }
                    };
                    //use Fragment Manager to display the dialogFragment
                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(), "quiz results");
                }
                else { //answer is correct but quiz is not over
                    //load the next flag after a 2-second delay
                    handler.postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    animate(true); //animate the flag off the screen
                                }
                            }, 2000);
                }
            } else {//answer is incorrect
                flagImageView.startAnimation(shakeAnimation); //play shake animation

                //display "Incorrect!" in red
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer, getContext().getTheme()));
                guessButton.setEnabled(true);
            }
        }
    };

    private void disableButtons() {
        for(int row = 0; row < guessRows; row++) {
            LinearLayout guessRow = guessLinearLayouts[row];
            for(int i = 0; i < guessRow.getChildCount(); i++) {
                guessRow.getChildAt(i).setEnabled(false);
            }
        }
    }


}
