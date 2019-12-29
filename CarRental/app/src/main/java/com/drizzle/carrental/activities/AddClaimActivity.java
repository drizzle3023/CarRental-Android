package com.drizzle.carrental.activities;

import android.app.Activity;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.drizzle.carrental.R;
import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.enumerators.DamagedPart;
import com.drizzle.carrental.models.Claim;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AddClaimActivity extends Activity implements View.OnClickListener {


    public enum ClaimCurrentStep {

        NEW("NEW ", 0),
        WHAT_HAPPENED_EDITING("WHAT_HAPPENED_EDITING", 1),
        ANSWERED_WHATHAPPENED("ANSWERED_WHATHAPPENED", 2),
        ANSWERED_WHENHAPPENED_EDITING("ANSWERED_WHENHAPPENED_EDITING", 3),
        ANSWERED_WHENHAPPENED("ANSWERED_WHENHAPPENED", 4),
        ANSWERED_WHEREHAPPENED("ANSWERED_WHEREHAPPENED", 5),
        ANSWERED_WHATPARTDAMAGED("ANSWERED_WHATPARTDAMAGED ", 6),
        VIDEO_CAPTURED("VIDEO_CAPTURED ", 7),
        ANSWERED_ELSE("ANSWERED_ELSE ", 8);

        private String stringValue;
        private int intValue;

        private ClaimCurrentStep(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        @Override
        public String toString() {
            return stringValue;
        }
    }


    /**
     * UI Control Handlers
     */
    private ImageButton buttonBack;
    private Button buttonSave;

    private LinearLayout layoutQuestionWhatHappened;
    private LinearLayout layoutAnswerWhatHappened;
    private LinearLayout layoutAnswerWhatHappenedSaved;

    private ImageView imageViewAnswerWhatHappendIcon;
    private TextView textViewAnswerWhatHappenedTitle;
    private ImageButton imageButtonEditAnswerWhatHappened;
    private TextView textViewAnswerWhatHappenedDescription;

    private EditText editTextAnswerWhatHappenedDescription;
    private ImageButton imageButtonClearTextAnswerWhatHappenedDescription;
    private Button buttonDoneTextAnswerWhatHappenedDescription;

    private LinearLayout layoutQuestionWhenHappened;
    private LinearLayout layoutAnswerWhenHappened;
    private TextView textViewQuestionWhenHappened;
    private ImageView imageViewAnswerWhenHappendIcon;
    private TextView textViewAnswerWhenHappened;
    private ImageButton imageButtonEditAnswerWhenHappened;

    private LinearLayout layoutQuestionWhereHappened;
    private LinearLayout layoutAnwerWhereHappened;
    private ImageView imageViewAnswerWhereHappendIcon;
    private TextView textViewQuestionWhereHappened;
    private TextView textViewAnswerWhereHappened;
    private ImageButton imageButtonEditAnswerWhereHappened;
    private ImageView imageViewAnswerWhereHappened;

    private LinearLayout layoutQuestionDamagedPart;
    private LinearLayout layoutAnwerDamagedPart;
    private ImageView imageViewAnswerDamagedPartIcon;
    private TextView textviewQuestionDamagedPart;
    private TextView textViewAnswerDamagedPart;
    private ImageButton imageButtonEditDamagedPart;
    private ImageView imageViewAnswerDamagedPart;

    private LinearLayout layoutQuestionTakeVideo;
    private LinearLayout layoutAnwerTakeVideo;
    private ImageView imageViewAnswerTakeVideoIcon;
    private TextView textViewQuestionTakeVideo;
    private TextView textViewAnswerTakeVideo;
    private ImageButton imageButtonEditTakeVideo;
    private ImageView imageViewAnswerTakeVideo;

    private LinearLayout layoutQuestionElse;
    private LinearLayout layoutAnwerElse;
    private LinearLayout layoutAnwerElseSaved;
    private ImageView imageViewAnswerElseIcon;
    private TextView textViewQuestionElse;
    private TextView textViewAnswerElse;
    private ImageButton imageButtonEditElse;
    private EditText editTextAnswerElse;
    private Button buttonDoneElse;

    private Button buttonSubmitClaim;

    private ClaimCurrentStep claimCurrentStep = ClaimCurrentStep.NEW;

    private boolean isModified = false; //indicates if any content is changed or not

    private Claim claim;

    /**
     * get control handlers by id and add listenres
     */
    private void getControlHandlersAndLinkActions() {

        buttonBack = findViewById(R.id.button_back);
        buttonSave = findViewById(R.id.button_save);

        layoutQuestionWhatHappened = findViewById(R.id.layout_question_what_happened);
        layoutAnswerWhatHappened = findViewById(R.id.layout_answer_what_happened);
        imageViewAnswerWhatHappendIcon = findViewById(R.id.imageview_answer_what_happened_icon);
        textViewAnswerWhatHappenedTitle = findViewById(R.id.textview_answer_what_happend_title);
        imageButtonEditAnswerWhatHappened = findViewById(R.id.imagebutton_edit_answer_what_happend);
        textViewAnswerWhatHappenedDescription = findViewById(R.id.textview_answer_what_happend_saved_description);
        layoutAnswerWhatHappenedSaved = findViewById(R.id.layout_answer_what_happened_saved);

        editTextAnswerWhatHappenedDescription = findViewById(R.id.edittext_answer_what_happend_description);
        imageButtonClearTextAnswerWhatHappenedDescription = findViewById(R.id.imagebutton_clear_answer_what_happened_description);
        buttonDoneTextAnswerWhatHappenedDescription = findViewById(R.id.button_done_answer_what_happend_description);

        layoutQuestionWhenHappened = findViewById(R.id.layout_question_when_happened);
        layoutAnswerWhenHappened = findViewById(R.id.layout_answer_when_happened);
        imageViewAnswerWhenHappendIcon = findViewById(R.id.imageview_answer_when_happened_icon);
        textViewAnswerWhenHappened = findViewById(R.id.textview_answer_when_happend);
        imageButtonEditAnswerWhenHappened = findViewById(R.id.imagebutton_edit_when_happend);
        textViewQuestionWhenHappened = findViewById(R.id.textview_question_when_happened);

        layoutQuestionWhereHappened = findViewById(R.id.layout_question_where_happened);
        layoutAnwerWhereHappened = findViewById(R.id.layout_answer_where_happened);
        imageViewAnswerWhereHappendIcon = findViewById(R.id.imageview_answer_where_happened_icon);
        textViewAnswerWhereHappened = findViewById(R.id.textview_answer_where_happened);
        imageButtonEditAnswerWhereHappened = findViewById(R.id.imagebutton_edit_answer_where_happened);
        imageViewAnswerWhereHappened = findViewById(R.id.mapview_answer_where_happened);
        textViewQuestionWhereHappened = findViewById(R.id.textview_question_where_happened);

        layoutQuestionDamagedPart = findViewById(R.id.layout_question_damaged_part);
        layoutAnwerDamagedPart = findViewById(R.id.layout_answer_damaged_part);
        imageViewAnswerDamagedPartIcon = findViewById(R.id.imageview_answer_damaged_part_icon);
        textViewAnswerDamagedPart = findViewById(R.id.textview_answer_damaged_part);
        imageButtonEditDamagedPart = findViewById(R.id.imagebutton_edit_answer_damaged_part);
        imageViewAnswerDamagedPart = findViewById(R.id.imageview_answer_damaged_part);
        textviewQuestionDamagedPart = findViewById(R.id.textview_question_damaged_parts);

        layoutQuestionTakeVideo = findViewById(R.id.layout_question_take_video);
        layoutAnwerTakeVideo = findViewById(R.id.layout_answer_take_video);
        imageViewAnswerTakeVideoIcon = findViewById(R.id.imageview_answer_take_video_icon);
        textViewAnswerTakeVideo = findViewById(R.id.textview_answer_take_video);
        imageButtonEditTakeVideo = findViewById(R.id.imagebutton_edit_answer_take_video);
        imageViewAnswerTakeVideo = findViewById(R.id.imageview_answer_take_video);
        textViewQuestionTakeVideo = findViewById(R.id.textview_question_take_video);

        layoutQuestionElse = findViewById(R.id.layout_question_else);
        layoutAnwerElse = findViewById(R.id.layout_answer_else);
        layoutAnwerElseSaved = findViewById(R.id.layout_answer_else_saved);
        imageViewAnswerElseIcon = findViewById(R.id.imageview_answer_else_icon);
        textViewAnswerElse = findViewById(R.id.textview_answer_else);
        imageButtonEditElse = findViewById(R.id.imagebutton_edit_answer_else);
        editTextAnswerElse = findViewById(R.id.edittext_answer_else);
        buttonDoneElse = findViewById(R.id.button_done_answer_else);
        buttonSubmitClaim = findViewById(R.id.button_submit);
        textViewQuestionElse = findViewById(R.id.textview_question_else);

        buttonBack.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        textViewAnswerWhatHappenedTitle.setOnClickListener(this);
        imageButtonEditAnswerWhatHappened.setOnClickListener(this);
        imageButtonClearTextAnswerWhatHappenedDescription.setOnClickListener(this);
        buttonDoneTextAnswerWhatHappenedDescription.setOnClickListener(this);

        textViewAnswerWhenHappened.setOnClickListener(this);
        imageButtonEditAnswerWhenHappened.setOnClickListener(this);

        textViewAnswerWhereHappened.setOnClickListener(this);
        imageButtonEditAnswerWhereHappened.setOnClickListener(this);
        imageViewAnswerWhereHappened.setOnClickListener(this);

        textViewAnswerDamagedPart.setOnClickListener(this);
        imageButtonEditDamagedPart.setOnClickListener(this);
        imageViewAnswerDamagedPart.setOnClickListener(this);

        textViewAnswerTakeVideo.setOnClickListener(this);
        imageButtonEditTakeVideo.setOnClickListener(this);
        imageViewAnswerTakeVideo.setOnClickListener(this);

        textViewAnswerElse.setOnClickListener(this);
        imageButtonEditElse.setOnClickListener(this);
        buttonDoneElse.setOnClickListener(this);

    }

    private void setClaim(Claim claim) {

        this.claim = claim;
    }

    private void prepareTestData() {

        Claim claim = new Claim();

        claim.setWhatHappened("Car glass is broken.");
        claim.setWhenHappened(new GregorianCalendar());

        Location loc = new Location("dummyprovider");
        loc.setLatitude(20.3);
        loc.setLongitude(52.6);

        claim.setWhereHappened(loc);
        claim.setAddressHappened("Independence Street, No 37");

        List<DamagedPart> damagedParts = new ArrayList<>();
        damagedParts.add(DamagedPart.LEFT_BACK);
        damagedParts.add(DamagedPart.LEFT_FENDER_PANEL);
        claim.setDamagedPart(damagedParts);

        claim.setExtraDescription("Extra description");
        claim.setClaimState(ClaimState.INCOMPLETE);

        setClaim(claim);

        claimCurrentStep = ClaimCurrentStep.NEW;
    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_claim);

        getControlHandlersAndLinkActions();

        prepareTestData();

        updateViewContent();
    }

    /**
     * update View content according to the claim state
     */
    public void updateViewContent() {

        updateSaveButton();
        updateAnswerWhatHappenedLayout();
        updateWhenHappenedLayout();
    }

    private void updateSaveButton() {

        if (!isModified) {

            buttonSave.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            buttonSave.setBackground(getResources().getDrawable(R.drawable.file_a_claim_save_button_inactived, null));
        } else {

            buttonSave.setTextColor(getResources().getColor(R.color.colorValid, null));
            buttonSave.setBackground(getResources().getDrawable(R.drawable.file_a_claim_save_button_actived, null));
        }
    }

    private void updateAnswerWhatHappenedLayout() {

        if (claimCurrentStep == ClaimCurrentStep.NEW) {

            layoutQuestionWhatHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhatHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhatHappenedSaved.setVisibility(View.VISIBLE);
            textViewAnswerWhatHappenedDescription.setVisibility(View.GONE);
            imageButtonEditAnswerWhatHappened.setVisibility(View.VISIBLE);
            editTextAnswerWhatHappenedDescription.setVisibility(View.GONE);
            imageButtonClearTextAnswerWhatHappenedDescription.setVisibility(View.GONE);
            buttonDoneTextAnswerWhatHappenedDescription.setVisibility(View.GONE);

            layoutQuestionWhatHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_enabled, null));
            layoutAnswerWhatHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_new, null));
            imageViewAnswerWhatHappendIcon.setImageResource(R.drawable.file_a_claim_answer_what_happened_icon_enabled);
            textViewAnswerWhatHappenedTitle.setText(getResources().getText(R.string.file_a_claim_answer_what_happened_text_title));
            imageButtonEditAnswerWhatHappened.setImageResource(R.drawable.file_a_claim_answer_edit);
            textViewAnswerWhatHappenedTitle.setTextColor(Color.WHITE);

        } else if (claimCurrentStep == ClaimCurrentStep.WHAT_HAPPENED_EDITING) {

            layoutQuestionWhatHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhatHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhatHappenedSaved.setVisibility(View.VISIBLE);
            textViewAnswerWhatHappenedDescription.setVisibility(View.GONE);
            imageButtonEditAnswerWhatHappened.setVisibility(View.GONE);
            editTextAnswerWhatHappenedDescription.setVisibility(View.VISIBLE);
            imageButtonClearTextAnswerWhatHappenedDescription.setVisibility(View.VISIBLE);
            buttonDoneTextAnswerWhatHappenedDescription.setVisibility(View.VISIBLE);

            layoutQuestionWhatHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_enabled, null));
            layoutAnswerWhatHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_saved, null));
            imageViewAnswerWhatHappendIcon.setImageResource(R.drawable.file_a_claim_answer_what_happened_icon_editing);

        } else {

            layoutQuestionWhatHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhatHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhatHappenedSaved.setVisibility(View.VISIBLE);
            textViewAnswerWhatHappenedDescription.setVisibility(View.VISIBLE);
            imageButtonEditAnswerWhatHappened.setVisibility(View.VISIBLE);
            editTextAnswerWhatHappenedDescription.setVisibility(View.GONE);
            imageButtonClearTextAnswerWhatHappenedDescription.setVisibility(View.GONE);
            buttonDoneTextAnswerWhatHappenedDescription.setVisibility(View.GONE);

            layoutQuestionWhatHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_disabled, null));
            layoutAnswerWhatHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_saved, null));
            imageViewAnswerWhatHappendIcon.setImageResource(R.drawable.file_a_claim_answer_what_happened_icon_saved);
            imageButtonEditAnswerWhatHappened.setImageResource(R.drawable.icon_edit);
            textViewAnswerWhatHappenedTitle.setText(claim.getWhatHappened());
            textViewAnswerWhatHappenedDescription.setText(claim.getWhatHappened());
        }
    }

    private void updateWhenHappenedLayout() {

        if (claimCurrentStep == ClaimCurrentStep.ANSWERED_WHATHAPPENED) {

            layoutQuestionWhenHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhenHappened.setVisibility(View.VISIBLE);
            imageButtonEditAnswerWhenHappened.setVisibility(View.VISIBLE);

            layoutAnswerWhenHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_new, null));
            imageViewAnswerWhenHappendIcon.setImageResource(R.drawable.file_a_claim_calendar_icon_editing);
            textViewAnswerWhenHappened.setText(R.string.claim_question_when_happened);
            imageButtonEditAnswerWhenHappened.setImageResource(R.drawable.file_a_claim_answer_edit);
            textViewQuestionWhenHappened.setTextColor(getResources().getColor(R.color.colorNormalText, null));

        } else if (claimCurrentStep == ClaimCurrentStep.ANSWERED_WHENHAPPENED_EDITING) {

            layoutQuestionWhenHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhenHappened.setVisibility(View.VISIBLE);
            imageButtonEditAnswerWhenHappened.setVisibility(View.GONE);

            layoutAnswerWhenHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_saved, null));
            imageViewAnswerWhenHappendIcon.setImageResource(R.drawable.file_a_claim_calendar_icon_saved);
            textViewAnswerWhenHappened.setText(claim.getDateString());
            textViewQuestionWhenHappened.setTextColor(getResources().getColor(R.color.colorNormalText, null));
        } else {

            layoutQuestionWhenHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhenHappened.setVisibility(View.VISIBLE);
            imageButtonEditAnswerWhenHappened.setVisibility(View.VISIBLE);

            layoutQuestionWhenHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_disabled, null));
            layoutAnswerWhenHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_saved, null));
            imageViewAnswerWhenHappendIcon.setImageResource(R.drawable.file_a_claim_calendar_icon_editing);
            textViewAnswerWhenHappened.setText(R.string.claim_question_when_happened);
            imageButtonEditAnswerWhenHappened.setImageResource(R.drawable.file_a_claim_answer_edit);
            textViewQuestionWhenHappened.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewAnswerWhenHappened.setTextColor(getResources().getColor(R.color.colorInvalid, null));
        }
    }

    private void updateWhereHappenedLayout() {


    }


    /**
     * OnClick Handlers
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.button_back:
                finish();
                break;


        }
    }


}
