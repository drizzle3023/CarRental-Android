package com.drizzle.carrental.activities;

import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.drizzle.carrental.R;
import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.enumerators.DamagedPart;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.models.Claim;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class AddClaimActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {


    public enum ClaimCurrentStep {

        NEW("NEW ", 0),
        WHAT_HAPPENED_EDITING("WHAT_HAPPENED_EDITING", 1),
        ANSWERED_WHATHAPPENED("ANSWERED_WHATHAPPENED", 2),
        ANSWERED_WHENHAPPENED("ANSWERED_WHENHAPPENED", 4),
        ANSWERED_WHEREHAPPENED("ANSWERED_WHEREHAPPENED", 5),
        ANSWERED_WHATPARTDAMAGED("ANSWERED_WHATPARTDAMAGED ", 6),
        ANSWERED_TAKE_VIDEO("VIDEO_CAPTURED ", 7),
        ELSE_ANSWER_EDITING("ELSE_ANSWER_EDITING", 8),
        ANSWERED_ELSE("ANSWERED_ELSE ", 9);

        private String stringValue;
        private int intValue;

        private ClaimCurrentStep(String toString, int value) {
            stringValue = toString;
            intValue = value;
        }

        public int getIntValue() {

            return intValue;
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
    private Button buttonEditAnswerWhatHappened;
    private TextView textViewAnswerWhatHappenedDescription;

    private EditText editTextAnswerWhatHappenedDescription;
    private ImageButton imageButtonClearTextAnswerWhatHappenedDescription;
    private Button buttonDoneTextAnswerWhatHappenedDescription;

    private LinearLayout layoutQuestionWhenHappened;
    private LinearLayout layoutAnswerWhenHappened;
    private TextView textViewQuestionWhenHappened;
    private ImageView imageViewAnswerWhenHappenedIcon;
    private TextView textViewAnswerWhenHappened;
    private Button buttonEditAnswerWhenHappened;

    private LinearLayout layoutQuestionWhereHappened;
    private LinearLayout layoutAnswerWhereHappened;
    private ImageView imageViewAnswerWhereHappenedIcon;
    private TextView textViewQuestionWhereHappened;
    private TextView textViewAnswerWhereHappened;
    private Button buttonEditAnswerWhereHappened;
    private SupportMapFragment mapViewAnswerWhereHappened;

    private LinearLayout layoutQuestionDamagedPart;
    private LinearLayout layoutAnswerDamagedPart;
    private ImageView imageViewAnswerDamagedPartIcon;
    private TextView textViewQuestionDamagedPart;
    private TextView textViewAnswerDamagedPart;
    private Button buttonEditDamagedPart;
    private FrameLayout frameLayoutDamagedZoneImage;
    private ImageView imageViewDamagedZoneImage;
    private TextView textViewDamagedZoneCount;


    private LinearLayout layoutQuestionTakeVideo;
    private LinearLayout layoutAnswerTakeVideo;
    private ImageView imageViewAnswerTakeVideoIcon;
    private TextView textViewQuestionTakeVideo;
    private TextView textViewAnswerTakeVideo;
    private Button buttonEditTakeVideo;
    private ImageView imageViewAnswerTakeVideo;

    private LinearLayout layoutQuestionElse;
    private LinearLayout layoutAnwerElse;
    private TextView textViewQuestionElse;
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
        buttonEditAnswerWhatHappened = findViewById(R.id.imagebutton_edit_answer_what_happend);
        textViewAnswerWhatHappenedDescription = findViewById(R.id.textview_answer_what_happend_saved_description);
        layoutAnswerWhatHappenedSaved = findViewById(R.id.layout_answer_what_happened_saved);

        editTextAnswerWhatHappenedDescription = findViewById(R.id.edittext_answer_what_happend_description);
        imageButtonClearTextAnswerWhatHappenedDescription = findViewById(R.id.imagebutton_clear_answer_what_happened_description);
        buttonDoneTextAnswerWhatHappenedDescription = findViewById(R.id.button_done_answer_what_happend_description);

        layoutQuestionWhenHappened = findViewById(R.id.layout_question_when_happened);
        layoutAnswerWhenHappened = findViewById(R.id.layout_answer_when_happened);
        imageViewAnswerWhenHappenedIcon = findViewById(R.id.imageview_answer_when_happened_icon);
        textViewAnswerWhenHappened = findViewById(R.id.textview_answer_when_happend);
        buttonEditAnswerWhenHappened = findViewById(R.id.imagebutton_edit_when_happend);
        textViewQuestionWhenHappened = findViewById(R.id.textview_question_when_happened);

        layoutQuestionWhereHappened = findViewById(R.id.layout_question_where_happened);
        layoutAnswerWhereHappened = findViewById(R.id.layout_answer_where_happened);
        imageViewAnswerWhereHappenedIcon = findViewById(R.id.imageview_answer_where_happened_icon);
        textViewAnswerWhereHappened = findViewById(R.id.textview_answer_where_happened);
        buttonEditAnswerWhereHappened = findViewById(R.id.imagebutton_edit_answer_where_happened);
        textViewQuestionWhereHappened = findViewById(R.id.textview_question_where_happened);
        mapViewAnswerWhereHappened = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapview_answer_where_happened);
        mapViewAnswerWhereHappened.getMapAsync(this);

        layoutQuestionDamagedPart = findViewById(R.id.layout_question_damaged_part);
        layoutAnswerDamagedPart = findViewById(R.id.layout_answer_damaged_part);
        imageViewAnswerDamagedPartIcon = findViewById(R.id.imageview_answer_damaged_part_icon);
        textViewAnswerDamagedPart = findViewById(R.id.textview_answer_damaged_part);
        buttonEditDamagedPart = findViewById(R.id.imagebutton_edit_answer_damaged_part);
        textViewQuestionDamagedPart = findViewById(R.id.textview_question_damaged_parts);
        frameLayoutDamagedZoneImage = findViewById(R.id.framelayout_damaged_part_image);
        imageViewDamagedZoneImage = findViewById(R.id.imageview_answer_damaged_part);
        textViewDamagedZoneCount = findViewById(R.id.textview_damaged_part_count);

        layoutQuestionTakeVideo = findViewById(R.id.layout_question_take_video);
        layoutAnswerTakeVideo = findViewById(R.id.layout_answer_take_video);
        imageViewAnswerTakeVideoIcon = findViewById(R.id.imageview_answer_take_video_icon);
        textViewAnswerTakeVideo = findViewById(R.id.textview_answer_take_video);
        buttonEditTakeVideo = findViewById(R.id.imagebutton_edit_answer_take_video);
        imageViewAnswerTakeVideo = findViewById(R.id.imageview_answer_take_video);
        textViewQuestionTakeVideo = findViewById(R.id.textview_question_take_video);

        layoutQuestionElse = findViewById(R.id.layout_question_else);
        layoutAnwerElse = findViewById(R.id.layout_answer_else);

        editTextAnswerElse = findViewById(R.id.edittext_answer_else);
        buttonDoneElse = findViewById(R.id.button_done_answer_else);
        textViewQuestionElse = findViewById(R.id.textview_question_else);

        buttonSubmitClaim = findViewById(R.id.button_submit);

        buttonBack.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        textViewAnswerWhatHappenedTitle.setOnClickListener(this);
        buttonEditAnswerWhatHappened.setOnClickListener(this);
        imageButtonClearTextAnswerWhatHappenedDescription.setOnClickListener(this);
        buttonDoneTextAnswerWhatHappenedDescription.setOnClickListener(this);

        textViewAnswerWhenHappened.setOnClickListener(this);
        buttonEditAnswerWhenHappened.setOnClickListener(this);

        textViewAnswerWhereHappened.setOnClickListener(this);
        buttonEditAnswerWhereHappened.setOnClickListener(this);

        textViewAnswerDamagedPart.setOnClickListener(this);
        buttonEditDamagedPart.setOnClickListener(this);

        textViewAnswerTakeVideo.setOnClickListener(this);
        buttonEditTakeVideo.setOnClickListener(this);
        imageViewAnswerTakeVideo.setOnClickListener(this);

        buttonDoneElse.setOnClickListener(this);
        buttonSubmitClaim.setOnClickListener(this);

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
        claim.setDamagedParts(damagedParts);

        claim.setExtraDescription("Extra description");
        claim.setClaimState(ClaimState.INCOMPLETE);
        claim.setImageURL("https://png.pngtree.com/element_our/20190523/ourlarge/pngtree-car-driving-box-type-long-motor-vehicle-line-image_1088711.jpg");
        setClaim(claim);

        claimCurrentStep = ClaimCurrentStep.ANSWERED_ELSE;
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
        updateWhereHappenedLayout();
        updateDamagedPartLayout();
        updateTakeVideoLayout();
        updateElseLayout();
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
            buttonEditAnswerWhatHappened.setVisibility(View.VISIBLE);
            editTextAnswerWhatHappenedDescription.setVisibility(View.GONE);
            imageButtonClearTextAnswerWhatHappenedDescription.setVisibility(View.GONE);
            buttonDoneTextAnswerWhatHappenedDescription.setVisibility(View.GONE);

            layoutQuestionWhatHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_enabled, null));
            layoutAnswerWhatHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_new, null));
            imageViewAnswerWhatHappendIcon.setImageResource(R.drawable.file_a_claim_answer_what_happened_icon_enabled);
            textViewAnswerWhatHappenedTitle.setText(getResources().getText(R.string.file_a_claim_answer_what_happened_text_title));
            buttonEditAnswerWhatHappened.setText(getResources().getText(R.string.drop_down_list_symbol_character));
            buttonEditAnswerWhatHappened.setBackgroundResource(0);
            textViewAnswerWhatHappenedTitle.setTextColor(Color.WHITE);

        } else if (claimCurrentStep == ClaimCurrentStep.WHAT_HAPPENED_EDITING) {

            layoutQuestionWhatHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhatHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhatHappenedSaved.setVisibility(View.VISIBLE);
            textViewAnswerWhatHappenedDescription.setVisibility(View.GONE);
            buttonEditAnswerWhatHappened.setVisibility(View.GONE);
            editTextAnswerWhatHappenedDescription.setVisibility(View.VISIBLE);
            imageButtonClearTextAnswerWhatHappenedDescription.setVisibility(View.VISIBLE);
            buttonDoneTextAnswerWhatHappenedDescription.setVisibility(View.VISIBLE);

            layoutQuestionWhatHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_enabled, null));
            layoutAnswerWhatHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_saved, null));
            imageViewAnswerWhatHappendIcon.setImageResource(R.drawable.file_a_claim_answer_what_happened_icon_editing);
            buttonEditAnswerWhatHappened.setBackgroundResource(R.drawable.icon_edit);
            buttonEditAnswerWhatHappened.setText("");

        } else {

            layoutQuestionWhatHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhatHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhatHappenedSaved.setVisibility(View.VISIBLE);
            textViewAnswerWhatHappenedDescription.setVisibility(View.VISIBLE);
            buttonEditAnswerWhatHappened.setVisibility(View.VISIBLE);
            editTextAnswerWhatHappenedDescription.setVisibility(View.GONE);
            imageButtonClearTextAnswerWhatHappenedDescription.setVisibility(View.GONE);
            buttonDoneTextAnswerWhatHappenedDescription.setVisibility(View.GONE);

            layoutQuestionWhatHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_disabled, null));
            layoutAnswerWhatHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_saved, null));
            imageViewAnswerWhatHappendIcon.setImageResource(R.drawable.file_a_claim_answer_what_happened_icon_saved);
            buttonEditAnswerWhatHappened.setBackgroundResource(R.drawable.icon_edit);
            buttonEditAnswerWhatHappened.setText("");
            textViewAnswerWhatHappenedTitle.setText(claim.getWhatHappened());
            textViewAnswerWhatHappenedDescription.setText(claim.getWhatHappened());
        }
    }

    private void updateWhenHappenedLayout() {

        if (claimCurrentStep == ClaimCurrentStep.ANSWERED_WHATHAPPENED) {

            layoutQuestionWhenHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhenHappened.setVisibility(View.VISIBLE);
            buttonEditAnswerWhenHappened.setVisibility(View.VISIBLE);

            layoutQuestionWhenHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_enabled, null));
            textViewQuestionWhenHappened.setTextColor(getResources().getColor(R.color.colorNormalText, null));

            layoutAnswerWhenHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_new, null));
            imageViewAnswerWhenHappenedIcon.setImageResource(R.drawable.file_a_claim_calendar_icon_editing);
            textViewAnswerWhenHappened.setText(getResources().getString(R.string.file_a_claim_answer_when_happened_text_title));
            textViewAnswerWhenHappened.setTextColor(Color.WHITE);
            buttonEditAnswerWhenHappened.setText(getResources().getString(R.string.drop_down_list_symbol_character));
            buttonEditAnswerWhenHappened.setBackgroundResource(0);

        } else if (claimCurrentStep.getIntValue() >= ClaimCurrentStep.ANSWERED_WHENHAPPENED.getIntValue()) {

            layoutQuestionWhenHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhenHappened.setVisibility(View.VISIBLE);
            buttonEditAnswerWhenHappened.setVisibility(View.VISIBLE);

            layoutQuestionWhenHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_enabled, null));
            textViewQuestionWhenHappened.setTextColor(getResources().getColor(R.color.colorNormalText, null));

            layoutAnswerWhenHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_saved, null));
            imageViewAnswerWhenHappenedIcon.setImageResource(R.drawable.file_a_claim_calendar_icon_saved);
            textViewAnswerWhenHappened.setText(claim.getDateString());
            textViewAnswerWhenHappened.setTextColor(getResources().getColor(R.color.colorNormalText, null));

            buttonEditAnswerWhenHappened.setBackground(getResources().getDrawable(R.drawable.icon_edit));
            buttonEditAnswerWhenHappened.setText("");

        } else {

            layoutQuestionWhenHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhenHappened.setVisibility(View.VISIBLE);
            buttonEditAnswerWhenHappened.setVisibility(View.VISIBLE);

            layoutQuestionWhenHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_disabled, null));
            textViewQuestionWhenHappened.setTextColor(getResources().getColor(R.color.colorInvalid, null));

            layoutAnswerWhenHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_disabled, null));
            imageViewAnswerWhenHappenedIcon.setImageResource(R.drawable.file_a_claim_calendar_icon_editing);
            textViewAnswerWhenHappened.setText(getResources().getString(R.string.file_a_claim_answer_when_happened_text_title));
            textViewAnswerWhenHappened.setTextColor(getResources().getColor(R.color.colorInvalid, null));

            buttonEditAnswerWhenHappened.setText(getResources().getString(R.string.drop_down_list_symbol_character));
            buttonEditAnswerWhenHappened.setBackgroundResource(0);
            buttonEditAnswerWhenHappened.setText("");
        }
    }

    private void updateWhereHappenedLayout() {

        if (claimCurrentStep == ClaimCurrentStep.ANSWERED_WHENHAPPENED) {

            layoutQuestionWhereHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhereHappened.setVisibility(View.VISIBLE);
            imageViewAnswerWhereHappenedIcon.setVisibility(View.VISIBLE);
            textViewAnswerWhereHappened.setVisibility(View.VISIBLE);
            buttonEditAnswerWhereHappened.setVisibility(View.VISIBLE);
            textViewQuestionWhereHappened.setVisibility(View.VISIBLE);
            if (mapViewAnswerWhereHappened.getView() != null) {
                mapViewAnswerWhereHappened.getView().setVisibility(View.GONE);
            }


            layoutQuestionWhereHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_enabled, null));
            textViewQuestionWhereHappened.setText(getResources().getString(R.string.claim_question_where_happened));
            textViewQuestionWhereHappened.setTextColor(getResources().getColor(R.color.colorNormalText, null));

            layoutAnswerWhereHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_new, null));
            imageViewAnswerWhereHappenedIcon.setImageResource(R.drawable.file_a_claim_icon_location_ready);
            textViewAnswerWhereHappened.setText(getResources().getString(R.string.file_a_claim_answer_where_happened_text_title));
            textViewAnswerWhereHappened.setTextColor(Color.WHITE);
            buttonEditAnswerWhereHappened.setText("");
            buttonEditAnswerWhereHappened.setBackgroundResource(0);

        } else if (claimCurrentStep.getIntValue() >= ClaimCurrentStep.ANSWERED_WHEREHAPPENED.getIntValue()) {

            layoutQuestionWhereHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhereHappened.setVisibility(View.VISIBLE);
            imageViewAnswerWhereHappenedIcon.setVisibility(View.VISIBLE);
            textViewAnswerWhereHappened.setVisibility(View.VISIBLE);
            buttonEditAnswerWhereHappened.setVisibility(View.VISIBLE);
            textViewQuestionWhereHappened.setVisibility(View.VISIBLE);
            if (mapViewAnswerWhereHappened.getView() != null) {
                mapViewAnswerWhereHappened.getView().setVisibility(View.VISIBLE);
            }


            layoutQuestionWhereHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_enabled, null));
            textViewQuestionWhereHappened.setText(getResources().getString(R.string.claim_question_where_happened));
            textViewQuestionWhereHappened.setTextColor(getResources().getColor(R.color.colorNormalText, null));

            layoutAnswerWhenHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_saved, null));
            imageViewAnswerWhereHappenedIcon.setImageResource(R.drawable.file_a_claim_icon_location_saved);
            textViewAnswerWhereHappened.setText(claim.getAddressHappened());
            textViewAnswerWhereHappened.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            buttonEditAnswerWhereHappened.setBackgroundResource(R.drawable.icon_edit);
            buttonEditAnswerWhereHappened.setText("");

        } else {

            layoutQuestionWhereHappened.setVisibility(View.VISIBLE);
            layoutAnswerWhereHappened.setVisibility(View.VISIBLE);
            imageViewAnswerWhereHappenedIcon.setVisibility(View.VISIBLE);
            textViewAnswerWhereHappened.setVisibility(View.VISIBLE);
            buttonEditAnswerWhereHappened.setVisibility(View.VISIBLE);
            textViewQuestionWhereHappened.setVisibility(View.VISIBLE);
            if (mapViewAnswerWhereHappened.getView() != null) {
                mapViewAnswerWhereHappened.getView().setVisibility(View.GONE);
            }


            layoutQuestionWhereHappened.setBackground(getResources().getDrawable(R.drawable.file_a_claim_question_disabled, null));
            textViewQuestionWhereHappened.setText(getResources().getString(R.string.claim_question_where_happened));
            textViewQuestionWhereHappened.setTextColor(getResources().getColor(R.color.colorInvalid, null));

            layoutAnswerWhereHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_disabled, null));
            imageViewAnswerWhereHappenedIcon.setImageResource(R.drawable.file_a_claim_icon_location_ready);
            textViewAnswerWhereHappened.setText(getResources().getString(R.string.file_a_claim_answer_where_happened_text_title));
            textViewAnswerWhereHappened.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            buttonEditAnswerWhereHappened.setText("");
            buttonEditAnswerWhereHappened.setBackgroundResource(0);
        }
    }

    private void updateDamagedPartLayout() {

        if (claimCurrentStep == ClaimCurrentStep.ANSWERED_WHEREHAPPENED) {

            layoutQuestionDamagedPart.setVisibility(View.VISIBLE);
            layoutAnswerDamagedPart.setVisibility(View.VISIBLE);
            imageViewAnswerDamagedPartIcon.setVisibility(View.VISIBLE);
            textViewQuestionDamagedPart.setVisibility(View.VISIBLE);
            textViewAnswerDamagedPart.setVisibility(View.VISIBLE);
            buttonEditDamagedPart.setVisibility(View.VISIBLE);
            frameLayoutDamagedZoneImage.setVisibility(View.GONE);

            layoutQuestionDamagedPart.setBackgroundResource(R.drawable.file_a_claim_question_enabled);
            layoutAnswerDamagedPart.setBackgroundResource(R.drawable.claim_answer_new);
            imageViewAnswerDamagedPartIcon.setImageResource(R.drawable.claim_select_damaged_part_icon);
            textViewQuestionDamagedPart.setText(getResources().getString(R.string.claim_question_damaged_part));
            textViewQuestionDamagedPart.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            textViewAnswerDamagedPart.setText(getResources().getString(R.string.file_a_claim_answer_damaged_part));
            textViewAnswerDamagedPart.setTextColor(Color.WHITE);
            buttonEditDamagedPart.setBackgroundResource(0);
            buttonEditDamagedPart.setText("");

        } else if (claimCurrentStep.getIntValue() >= ClaimCurrentStep.ANSWERED_WHATPARTDAMAGED.getIntValue()) {

            layoutQuestionDamagedPart.setVisibility(View.VISIBLE);
            layoutAnswerDamagedPart.setVisibility(View.VISIBLE);
            imageViewAnswerDamagedPartIcon.setVisibility(View.GONE);
            textViewQuestionDamagedPart.setVisibility(View.VISIBLE);
            textViewAnswerDamagedPart.setVisibility(View.VISIBLE);
            buttonEditDamagedPart.setVisibility(View.VISIBLE);
            frameLayoutDamagedZoneImage.setVisibility(View.VISIBLE);

            layoutQuestionDamagedPart.setBackgroundResource(R.drawable.file_a_claim_question_enabled);
            layoutAnswerDamagedPart.setBackgroundResource(R.drawable.claim_answer_damaged_zone_saved);
            textViewQuestionDamagedPart.setText(getResources().getString(R.string.claim_question_damaged_part));
            textViewAnswerDamagedPart.setText(claim.getDateString());
            textViewAnswerDamagedPart.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewAnswerDamagedPart.setTypeface(null, Typeface.ITALIC);
            buttonEditDamagedPart.setText("");
            buttonEditDamagedPart.setBackgroundResource(R.drawable.icon_edit);

            int damagedZoneCount = 0;
            if (claim.getDamagedParts() != null) {
                damagedZoneCount = claim.getDamagedParts().size();
            }

            if (damagedZoneCount > 1) {
                String strCount = "+" + (damagedZoneCount - 1);
                textViewDamagedZoneCount.setText(strCount);
            } else {
                textViewDamagedZoneCount.setVisibility(View.GONE);
            }

            if (damagedZoneCount > 0) {

                String strZoneName = claim.getDamagedParts().get(0).toString() + "_selected";

                int resID = getResources().getIdentifier(strZoneName, "drawable", getPackageName());
                imageViewDamagedZoneImage.setImageResource(resID);
            }


        } else {

            layoutQuestionDamagedPart.setVisibility(View.VISIBLE);
            layoutAnswerDamagedPart.setVisibility(View.VISIBLE);
            imageViewAnswerDamagedPartIcon.setVisibility(View.VISIBLE);
            textViewQuestionDamagedPart.setVisibility(View.VISIBLE);
            textViewAnswerDamagedPart.setVisibility(View.VISIBLE);
            buttonEditDamagedPart.setVisibility(View.VISIBLE);
            frameLayoutDamagedZoneImage.setVisibility(View.GONE);

            layoutQuestionDamagedPart.setBackgroundResource(R.drawable.file_a_claim_question_disabled);
            layoutAnswerDamagedPart.setBackgroundResource(R.drawable.claim_answer_disabled);
            imageViewAnswerDamagedPartIcon.setImageResource(R.drawable.claim_select_damaged_part_icon);
            textViewQuestionDamagedPart.setText(getResources().getString(R.string.claim_question_damaged_part));
            textViewAnswerDamagedPart.setText(getResources().getString(R.string.file_a_claim_answer_damaged_part));
            buttonEditDamagedPart.setText("");
            buttonEditDamagedPart.setBackgroundResource(0);

        }
    }

    private void updateTakeVideoLayout() {

        if (claimCurrentStep == ClaimCurrentStep.ANSWERED_WHATPARTDAMAGED) {

            layoutQuestionTakeVideo.setVisibility(View.VISIBLE);
            layoutAnswerTakeVideo.setVisibility(View.VISIBLE);
            imageViewAnswerTakeVideoIcon.setVisibility(View.VISIBLE);
            textViewQuestionTakeVideo.setVisibility(View.VISIBLE);
            textViewAnswerTakeVideo.setVisibility(View.VISIBLE);
            buttonEditTakeVideo.setVisibility(View.VISIBLE);
            imageViewAnswerTakeVideo.setVisibility(View.GONE);

            layoutQuestionTakeVideo.setBackgroundResource(R.drawable.file_a_claim_question_enabled);
            layoutAnswerTakeVideo.setBackgroundResource(R.drawable.claim_answer_new);
            imageViewAnswerTakeVideoIcon.setBackgroundResource(R.drawable.file_a_claim_take_video_icon_ready);
            textViewQuestionTakeVideo.setText(getResources().getString(R.string.claim_question_take_a_video));
            textViewQuestionTakeVideo.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            textViewAnswerTakeVideo.setText(getResources().getString(R.string.file_a_claim_answer_take_video));
            textViewAnswerTakeVideo.setTextColor(Color.WHITE);
            buttonEditTakeVideo.setText("");

        } else if (claimCurrentStep.getIntValue() >= ClaimCurrentStep.ANSWERED_TAKE_VIDEO.getIntValue()) {

            layoutQuestionTakeVideo.setVisibility(View.VISIBLE);
            layoutAnswerTakeVideo.setVisibility(View.VISIBLE);
            imageViewAnswerTakeVideoIcon.setVisibility(View.GONE);
            textViewQuestionTakeVideo.setVisibility(View.VISIBLE);
            textViewAnswerTakeVideo.setVisibility(View.VISIBLE);
            buttonEditTakeVideo.setVisibility(View.VISIBLE);
            imageViewAnswerTakeVideo.setVisibility(View.VISIBLE);

            layoutQuestionTakeVideo.setBackgroundResource(R.drawable.file_a_claim_question_enabled);
            layoutAnswerTakeVideo.setBackgroundResource(R.drawable.claim_answer_saved);
            imageViewAnswerTakeVideoIcon.setBackgroundResource(R.drawable.file_a_claim_take_video_icon_ready);
            textViewQuestionTakeVideo.setText(getResources().getString(R.string.claim_question_take_a_video));
            textViewQuestionTakeVideo.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            textViewAnswerTakeVideo.setText(claim.getDateString());
            textViewAnswerTakeVideo.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewAnswerTakeVideo.setTypeface(null, Typeface.ITALIC);
            buttonEditTakeVideo.setText("");
            buttonEditTakeVideo.setBackgroundResource(R.drawable.icon_edit);
            Picasso.get().load(claim.getImageURL()).placeholder(null).into(imageViewAnswerTakeVideo);

        } else {


            layoutQuestionTakeVideo.setVisibility(View.VISIBLE);
            layoutAnswerTakeVideo.setVisibility(View.VISIBLE);
            imageViewAnswerTakeVideoIcon.setVisibility(View.VISIBLE);
            textViewQuestionTakeVideo.setVisibility(View.VISIBLE);
            textViewAnswerTakeVideo.setVisibility(View.VISIBLE);
            buttonEditTakeVideo.setVisibility(View.GONE);
            imageViewAnswerTakeVideo.setVisibility(View.GONE);

            layoutQuestionTakeVideo.setBackgroundResource(R.drawable.file_a_claim_question_disabled);
            layoutAnswerTakeVideo.setBackgroundResource(R.drawable.claim_answer_disabled);
            imageViewAnswerTakeVideoIcon.setBackgroundResource(R.drawable.file_a_claim_take_video_icon_ready);
            textViewQuestionTakeVideo.setText(getResources().getString(R.string.claim_question_take_a_video));
            textViewQuestionTakeVideo.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewAnswerTakeVideo.setText(getResources().getString(R.string.file_a_claim_answer_take_video));
            textViewAnswerTakeVideo.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewAnswerTakeVideo.setTypeface(null, Typeface.ITALIC);

        }

    }

    private void updateElseLayout() {

        if (claimCurrentStep == ClaimCurrentStep.ANSWERED_TAKE_VIDEO) {


            layoutQuestionElse.setBackgroundResource(R.drawable.file_a_claim_question_enabled);
            layoutAnwerElse.setBackgroundResource(R.drawable.claim_answer_saved);
            editTextAnswerElse.setText("");
            buttonDoneElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewQuestionElse.setText(getResources().getString(R.string.claim_question_else));
            textViewQuestionElse.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            editTextAnswerElse.setEnabled(true);

        }
        else if (claimCurrentStep == ClaimCurrentStep.ELSE_ANSWER_EDITING) {

            layoutQuestionElse.setBackgroundResource(R.drawable.file_a_claim_question_enabled);
            layoutAnwerElse.setBackgroundResource(R.drawable.claim_answer_saved);
            editTextAnswerElse.setText("");
            buttonDoneElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewQuestionElse.setText(getResources().getString(R.string.claim_question_else));
            textViewQuestionElse.setTextColor(getResources().getColor(R.color.colorNormalText, null));

            editTextAnswerElse.setEnabled(true);
        }
        else if (claimCurrentStep == ClaimCurrentStep.ANSWERED_ELSE) {

            layoutQuestionElse.setBackgroundResource(R.drawable.file_a_claim_question_enabled);
            layoutAnwerElse.setBackgroundResource(R.drawable.claim_answer_saved);
            buttonDoneElse.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            textViewQuestionElse.setText(getResources().getString(R.string.claim_question_else));
            textViewQuestionElse.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            editTextAnswerElse.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            editTextAnswerElse.setText(claim.getExtraDescription());
            if (claim.getExtraDescription().isEmpty()) {

                buttonDoneElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            }
            else {
                buttonDoneElse.setTextColor(getResources().getColor(R.color.colorNormalBlue, null));
            }
            editTextAnswerElse.setEnabled(true);
        }
        else {

            layoutQuestionElse.setBackgroundResource(R.drawable.file_a_claim_question_disabled);
            layoutAnwerElse.setBackgroundResource(R.drawable.claim_answer_disabled);
            buttonDoneElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewQuestionElse.setText(getResources().getString(R.string.claim_question_else));
            textViewQuestionElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            editTextAnswerElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            editTextAnswerElse.setEnabled(false);
            buttonDoneElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));

        }
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

    @Override
    public void onMapReady(GoogleMap googleMap) {


        if (claim.getWhatHappened() != null) {
            googleMap.addMarker(new MarkerOptions().position(new LatLng(claim.getWhereHappened().getLatitude(), claim.getWhereHappened().getLongitude())).title("Marker"));

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(claim.getWhereHappened().getLatitude(), claim.getWhereHappened().getLongitude()), Constants.DEFAULT_MAP_ZOOM_LEVEL));

        }
    }

}
