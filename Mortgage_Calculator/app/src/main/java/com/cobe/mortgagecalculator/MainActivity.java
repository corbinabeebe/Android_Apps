package com.cobe.mortgagecalculator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    //currency and percent formatter variables
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
    private static final NumberFormat integerFormat = NumberFormat.getIntegerInstance();

    private double purchaseAmount = 0.00; //purchase price of home
    private double downPayment= 0; //down payment
    private double interestRate = 0.00; //interest rate entered by user
    private int mortgageYears = 15; //starting year for seekbar

    private TextView seekBarYearTextView; // used for custom years on seek bar
    private TextView purchasePriceTextView; //used for the purchase price textview
    private TextView downPaymentTextView; //used for down payment textview
    private TextView interestRateTextView; //used ofr interest rate text view
    private TextView mPaymentCDTextView; //show monthly mortgage payment for 10 years
    private TextView mPaymentTenTextView; //show monthly mortgage payment for 20 years
    private TextView mPaymentTwentyTextView; //show monthly mortgage payment for 30 years
    private TextView mPaymentThirtyTextView; ////show monthly mortgage payment for amount of years on seekbar
    private Button calculateButton; //button used for calculate button

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //reference to manipulate TextViews
        seekBarYearTextView = findViewById(R.id.seekBarYearTextView);
        purchasePriceTextView = findViewById(R.id.purchasePriceTextView);
        downPaymentTextView = findViewById(R.id.downPaymentTextView);
        interestRateTextView = findViewById(R.id.interestRateTextView);

        mPaymentTenTextView = findViewById(R.id.mPaymentTenTextView);
        mPaymentTenTextView.setText(currencyFormat.format(0));

        mPaymentTwentyTextView = findViewById(R.id.mPaymentTwentyTextView);
        mPaymentTwentyTextView.setText(currencyFormat.format(0));

        mPaymentThirtyTextView = findViewById(R.id.mPaymentThirtyTextView);
        mPaymentThirtyTextView.setText(currencyFormat.format(0));

        mPaymentCDTextView = findViewById(R.id.mPaymentCDTextView);
        mPaymentCDTextView.setText(currencyFormat.format(0));

        //set EditText TextWatchers
        EditText purchasePriceEditText = findViewById(R.id.purchasePriceEditText);
        purchasePriceEditText.addTextChangedListener(purchasePriceEditTextWatcher);

        EditText downPaymentEditText = findViewById(R.id.downPaymentEditText);
        downPaymentEditText.addTextChangedListener(downPaymentEditTextWatcher);

        EditText interestRateEditText = findViewById(R.id.interestRateEditText);
        interestRateEditText.addTextChangedListener(interestRateEditTextWatcher);

        //set OnSeekBar listener
        SeekBar mortgageYearsSeekBar = findViewById(R.id.mortgageYearsSeekBar);
        mortgageYearsSeekBar.setOnSeekBarChangeListener(seekBarListener);

        //make calculate button clickable
        calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculateTenYears();
                calculateTwentyYears();
                calculateThirtyYears();
                calculateCustomYears(mortgageYears);
            }
        });
    }

    //calculate monthly payment totals
    private void calculateTenYears() {
        double totalMonths = 10 * 12;
        //format interest rate TextView
        interestRateTextView.setText(percentFormat.format(interestRate));

        //calculate mortgage over 10 years
        double loanAmount = purchaseAmount - downPayment;
        double totalInterest = loanAmount * interestRate;
        double totalLoan = loanAmount + totalInterest;
        double monthlyPayment = totalLoan / totalMonths;

        //display monthlyPayment for 10 year loan
        mPaymentTenTextView.setText(currencyFormat.format(monthlyPayment));
    }
    private void calculateTwentyYears() {
        double totalMonths = 20 * 12;
//        //format interest rate TextView
//        interestRateTextView.setText(percentFormat.format(interestRate));

        //calculate mortgage over 20 years
        double loanAmount = purchaseAmount - downPayment;
        double totalInterest = loanAmount * interestRate;
        double totalLoan = loanAmount + totalInterest;
        double monthlyPayment = totalLoan / totalMonths;

        //display monthlyPayment for 10 year loan
        mPaymentTwentyTextView.setText(currencyFormat.format(monthlyPayment));
    }
    private void calculateThirtyYears() {
        double totalMonths = 30* 12;

        //calculate mortgage over 30 years
        double loanAmount = purchaseAmount - downPayment;
        double totalInterest = loanAmount * interestRate;
        double totalLoan = loanAmount + totalInterest;
        double monthlyPayment = totalLoan / totalMonths;

        //display monthlyPayment for 10 year loan
        mPaymentThirtyTextView.setText(currencyFormat.format(monthlyPayment));
    }
    private void calculateCustomYears(int progress) {

        double totalMonths = (double) progress * 12;
        //format mortgagYyears TextView
        seekBarYearTextView.setText(integerFormat.format(mortgageYears));

        //calculate mortgage over 10 years
        double loanAmount = purchaseAmount - downPayment;
        double totalInterest = loanAmount * interestRate;
        double totalLoan = loanAmount + totalInterest;
        double monthlyPayment = totalLoan / totalMonths;

        //display monthlyPayment for custom year loan
        mPaymentCDTextView.setText(currencyFormat.format(monthlyPayment));

    }

    private final SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            mortgageYears = progress; //sets mortgage years equal to the progress of the seekbar
            calculateCustomYears(mortgageYears);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };

    private final TextWatcher purchasePriceEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try { // get purchase price total and display in currency format
                purchaseAmount = Double.parseDouble(charSequence.toString());
                purchasePriceTextView.setText(currencyFormat.format(purchaseAmount));
            }
            catch (NumberFormatException e) { //if charSequence is empty or non-numeric
                purchasePriceTextView.setText("");
                purchaseAmount = 0.0;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private final TextWatcher downPaymentEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try { // get purchase price total and display in currency format
                downPayment = Double.parseDouble(charSequence.toString());
                downPaymentTextView.setText(currencyFormat.format(downPayment));
            }
            catch (NumberFormatException e) { //if charSequence is empty or non-numeric
                downPaymentTextView.setText("");
                downPayment = 0.0;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private final TextWatcher interestRateEditTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try { // get purchase price total and display in currency format
                interestRate = Double.parseDouble(charSequence.toString()) / 100;
                percentFormat.setMinimumFractionDigits(2);
                interestRateTextView.setText(percentFormat.format(interestRate));
            }
            catch (NumberFormatException e) { //if charSequence is empty or non-numeric
                interestRateTextView.setText("");
                interestRate = 0.0;
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}
