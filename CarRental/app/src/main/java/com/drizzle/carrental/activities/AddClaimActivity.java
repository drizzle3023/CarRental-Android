package com.drizzle.carrental.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.location.Location;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.drizzle.carrental.R;
import com.drizzle.carrental.api.VolleyMultipartRequest;
import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.enumerators.DamagedPart;
import com.drizzle.carrental.globals.AppHelper;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.Globals;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.globals.Utils;
import com.drizzle.carrental.models.Claim;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import wseemann.media.FFmpegMediaMetadataRetriever;

import static com.drizzle.carrental.activities.BaseCameraActivity.getImageFilePath;
import static com.drizzle.carrental.activities.BaseCameraActivity.getVideoFilePath;

public class AddClaimActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, Callback<ResponseBody> {

    final int ADD_LOCATION_REQUEST_CODE = 1;
    public static final int MY_CAMERA_ACTIVITY_REQUEST_CODE = 2;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 88888;

    @Override
    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

    }

    @Override
    public void onFailure(Call<ResponseBody> call, Throwable t) {
        hideWaitingScreen();
        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
    }

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

    ProgressDialog progressDialog;

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
    private GoogleMap googleMapWhereHappened;

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

    private ScrollView scrollView;

    private ClaimCurrentStep claimCurrentStep = ClaimCurrentStep.NEW;

    private ClaimCurrentStep claimCurrentStepTemp = ClaimCurrentStep.NEW;

    private boolean isEditable = false; //indicates if any content is changed or not

    private Claim claim;

    private boolean isSaveOrSubmit = false;

    /**
     * get control handlers by id and add listenres
     */
    private void getControlHandlersAndLinkActions() {

        scrollView = findViewById(R.id.scrollView);

        buttonBack = findViewById(R.id.button_back_to_onboarding);
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

        ArrayList<DamagedPart> damagedParts = new ArrayList<>();
        damagedParts.add(DamagedPart.LEFT_BACK);
        damagedParts.add(DamagedPart.LEFT_FENDER_PANEL);
        claim.setDamagedParts(damagedParts);

        claim.setExtraDescription("Extra description");
        claim.setClaimState(ClaimState.INCOMPLETE);
        claim.setImageURL("https://png.pngtree.com/element_our/20190523/ourlarge/pngtree-car-driving-box-type-long-motor-vehicle-line-image_1088711.jpg");
        setClaim(claim);

        ArrayList<DamagedPart> temp = new ArrayList<>();
        temp.add(DamagedPart.LEFT_QUARTER_PANEL);
        temp.add(DamagedPart.RIGHT_QUARTER_PANEL);
        temp.add(DamagedPart.RIGHT_ROOF);
        claim.setDamagedParts(temp);

        //claim.setVideoURL(getAndroidMoviesFolder().getAbsolutePath() + "/" + Constants.VEHICLE_VIDEO_FILE_NAME);
        //claim.setVideoURL("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");

        claimCurrentStep = ClaimCurrentStep.ANSWERED_ELSE;
    }

    public static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            //Utils.appendLog(System.err.toString());
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());
        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }

        return bitmap;
    }

    public Bitmap retriveVideoThumbnailFromURL(String videoPath) {


        FFmpegMediaMetadataRetriever mmr = new FFmpegMediaMetadataRetriever();
        mmr.setDataSource(videoPath);
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
        mmr.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
        Bitmap b = mmr.getFrameAtTime(2000000, FFmpegMediaMetadataRetriever.OPTION_CLOSEST); // frame at 2 seconds
        byte[] artwork = mmr.getEmbeddedPicture();

        mmr.release();

        return b;
    }


    private void setCurrentClaimStep() {


        if (claim.getWhatHappened() == null || claim.getWhatHappened().isEmpty()) {

            claimCurrentStep = ClaimCurrentStep.NEW;
        } else {

            claimCurrentStep = ClaimCurrentStep.ANSWERED_WHATHAPPENED;
        }

        if (claim.getWhenHappened() != null) {

            claimCurrentStep = ClaimCurrentStep.ANSWERED_WHENHAPPENED;
        }

        if (claim.getAddressHappened() != null && !claim.getAddressHappened().isEmpty()) {

            claimCurrentStep = ClaimCurrentStep.ANSWERED_WHEREHAPPENED;
        }

        if (claim.getDamagedParts() != null && !claim.getDamagedParts().isEmpty()) {

            claimCurrentStep = ClaimCurrentStep.ANSWERED_WHATPARTDAMAGED;
        }

        if (claim.getVideoURL() != null && !claim.getVideoURL().isEmpty()) {

            claimCurrentStep = ClaimCurrentStep.ANSWERED_TAKE_VIDEO;
        }

        if (claim.getExtraDescription() != null && !claim.getExtraDescription().isEmpty()) {

            claimCurrentStep = ClaimCurrentStep.ANSWERED_ELSE;
        }

        if (claim.getClaimState() == null || claim.getClaimState() == ClaimState.INCOMPLETE) {
            isEditable = true;
        } else {

            isEditable = false;
        }

    }

    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_claim);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        claim = Globals.selectedClaim;

        setCurrentClaimStep();

        getControlHandlersAndLinkActions();

        //prepareTestData();

        updateViewContent();

    }

    /**
     * update View content according to the claim state
     */
    public void updateViewContent() {

        updateButtons();
        updateAnswerWhatHappenedLayout();
        updateWhenHappenedLayout();
        updateWhereHappenedLayout();
        updateDamagedPartLayout();
        updateTakeVideoLayout();
        updateElseLayout();

        if (isEditable) {

            buttonEditAnswerWhatHappened.setVisibility(View.VISIBLE);
            buttonEditAnswerWhenHappened.setVisibility(View.VISIBLE);
            buttonEditAnswerWhereHappened.setVisibility(View.VISIBLE);
            buttonEditTakeVideo.setVisibility(View.GONE);
            buttonEditDamagedPart.setVisibility(View.VISIBLE);

        } else {

            buttonEditAnswerWhatHappened.setVisibility(View.GONE);
            buttonEditAnswerWhenHappened.setVisibility(View.GONE);
            buttonEditAnswerWhereHappened.setVisibility(View.GONE);
            buttonEditTakeVideo.setVisibility(View.GONE);
            buttonEditDamagedPart.setVisibility(View.GONE);

            editTextAnswerElse.setEnabled(false);
        }
    }

    private void updateButtons() {

        if (isEditable) {

//            buttonSave.setTextColor(getResources().getColor(R.color.colorInvalid, null));
//            buttonSave.setBackground(getResources().getDrawable(R.drawable.file_a_claim_save_button_inactived, null));

            buttonSave.setVisibility(View.VISIBLE);

            if (claimCurrentStep.getIntValue() >= ClaimCurrentStep.ANSWERED_TAKE_VIDEO.getIntValue()) {
                buttonSubmitClaim.setBackgroundResource(R.drawable.active_button);
                buttonSubmitClaim.setEnabled(true);
            } else {
                buttonSubmitClaim.setBackgroundResource(R.drawable.inactive_button);
                buttonSubmitClaim.setEnabled(false);
            }

        } else {
            buttonSave.setVisibility(View.GONE);
            buttonSave.setTextColor(getResources().getColor(R.color.colorValid, null));
            buttonSave.setBackground(getResources().getDrawable(R.drawable.file_a_claim_save_button_actived, null));

            buttonSubmitClaim.setBackgroundResource(R.drawable.inactive_button);
            buttonSubmitClaim.setEnabled(false);

        }


    }

    private void updateAnswerWhatHappenedLayout() {

        textViewAnswerWhatHappenedTitle.setClickable(true);
        buttonDoneTextAnswerWhatHappenedDescription.setClickable(true);
        buttonEditAnswerWhatHappened.setClickable(true);

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
            textViewAnswerWhatHappenedTitle.setText(getResources().getText(R.string.file_a_claim_answer_what_happened_text_title_select_answer));
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
            textViewAnswerWhatHappenedTitle.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            textViewAnswerWhatHappenedTitle.setText(getResources().getString(R.string.file_a_claim_answer_what_happened_text_title_your_description));
            layoutAnswerWhatHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_saved, null));
            imageViewAnswerWhatHappendIcon.setImageResource(R.drawable.file_a_claim_answer_what_happened_icon_editing);

            buttonEditAnswerWhatHappened.setBackgroundResource(R.drawable.ic_edit_black_24dp);
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
            buttonEditAnswerWhatHappened.setBackgroundResource(R.drawable.ic_edit_black_24dp);
            buttonEditAnswerWhatHappened.setText("");

            if (claim.getWhatHappened() != null) {

                if (!claim.getWhatHappened().equals(getResources().getString(R.string.claim_reason_car_accident))
                        && !claim.getWhatHappened().equals(getResources().getString(R.string.claim_reason_car_stolen))
                        && !claim.getWhatHappened().equals(getResources().getString(R.string.claim_reason_rock_hit_glass))
                        && !claim.getWhatHappened().equals(getResources().getString(R.string.claim_reason_natural_hazard))) {

                    textViewAnswerWhatHappenedTitle.setText(getResources().getString(R.string.file_a_claim_answer_what_happened_text_title_your_description));
                    textViewAnswerWhatHappenedDescription.setText(claim.getWhatHappened());
                } else {
                    textViewAnswerWhatHappenedTitle.setText(claim.getWhatHappened());
                    textViewAnswerWhatHappenedDescription.setVisibility(View.GONE);
                }
            }

            textViewAnswerWhatHappenedTitle.setTextColor(getResources().getColor(R.color.colorNormalText, null));

        }
    }

    private void updateWhenHappenedLayout() {

        textViewAnswerWhenHappened.setClickable(true);
        buttonEditAnswerWhenHappened.setClickable(true);

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
            imageViewAnswerWhenHappenedIcon.setImageResource(R.drawable.ic_icon_date_transparent);
            textViewAnswerWhenHappened.setText(claim.getDateString());
            textViewAnswerWhenHappened.setTextColor(getResources().getColor(R.color.colorNormalText, null));

            buttonEditAnswerWhenHappened.setBackground(getResources().getDrawable(R.drawable.ic_edit_black_24dp, null));
            buttonEditAnswerWhenHappened.setText("");

        } else if (claimCurrentStep.getIntValue() == ClaimCurrentStep.WHAT_HAPPENED_EDITING.getIntValue()) {


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

            textViewAnswerWhenHappened.setClickable(false);
            buttonEditAnswerWhenHappened.setClickable(false);

        }
    }

    private void updateWhereHappenedLayout() {

        textViewAnswerWhereHappened.setClickable(true);
        buttonEditAnswerWhereHappened.setClickable(true);

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

            layoutAnswerWhereHappened.setBackground(getResources().getDrawable(R.drawable.claim_answer_saved, null));
            imageViewAnswerWhereHappenedIcon.setImageResource(R.drawable.file_a_claim_icon_location_saved);
            textViewAnswerWhereHappened.setText(claim.getAddressHappened());
            textViewAnswerWhereHappened.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            buttonEditAnswerWhereHappened.setBackgroundResource(R.drawable.ic_edit_black_24dp);
            buttonEditAnswerWhereHappened.setText("");

        } else if (claimCurrentStep.getIntValue() == ClaimCurrentStep.WHAT_HAPPENED_EDITING.getIntValue()) {


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


            textViewAnswerWhereHappened.setClickable(false);
            buttonEditAnswerWhereHappened.setClickable(false);

        }
    }

    private void updateDamagedPartLayout() {

        textViewAnswerDamagedPart.setClickable(true);
        buttonEditDamagedPart.setClickable(true);

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
            buttonEditDamagedPart.setBackgroundResource(R.drawable.ic_edit_black_24dp);

            int damagedZoneCount = 0;
            if (claim.getDamagedParts() != null) {
                damagedZoneCount = claim.getDamagedParts().size();
            }

            if (damagedZoneCount > 1) {
                String strCount = "+" + (damagedZoneCount - 1);
                textViewDamagedZoneCount.setText(strCount);
                textViewDamagedZoneCount.setVisibility(View.VISIBLE);
            } else {
                textViewDamagedZoneCount.setVisibility(View.GONE);
            }

            if (damagedZoneCount > 0) {

                String strZoneName = claim.getDamagedParts().get(0).toString() + "_selected";

                int resID = getResources().getIdentifier(strZoneName, "drawable", getPackageName());
                imageViewDamagedZoneImage.setImageResource(resID);
            }


        } else if (claimCurrentStep.getIntValue() == ClaimCurrentStep.WHAT_HAPPENED_EDITING.getIntValue()) {


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
            textViewQuestionDamagedPart.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewAnswerDamagedPart.setText(getResources().getString(R.string.file_a_claim_answer_damaged_part));
            textViewAnswerDamagedPart.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            buttonEditDamagedPart.setText("");
            buttonEditDamagedPart.setBackgroundResource(0);

            textViewAnswerDamagedPart.setClickable(false);
            buttonEditDamagedPart.setClickable(false);


        }
    }

    private void updateTakeVideoLayout() {

        textViewAnswerTakeVideo.setClickable(true);
        buttonEditTakeVideo.setClickable(true);
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
            buttonEditTakeVideo.setBackgroundResource(R.drawable.ic_edit_black_24dp);

            Picasso picasso = Picasso.get();
            picasso.invalidate(claim.getImageURL());
            picasso.load(claim.getImageURL()).placeholder(R.drawable.history_row_item_image_corner_radius).into(imageViewAnswerTakeVideo);


            //Picasso.get().load("file:///storage/emulated/0/Pictures/DamagedPart.png").placeholder(R.drawable.image_damaged_zone).into(imageViewAnswerTakeVideo);

            //Picasso.with(context).load(new File(path)).into(imageView);

//            Bitmap bitmap = null;
//            try {
//                bitmap = retriveVideoFrameFromVideo(claim.getVideoURL());
//            } catch (Throwable throwable) {
//                throwabl//Utils.appendLog(System.err.toString());
//                e.printStackTrace();
//            }
//            //Bitmap bitmap = retriveVideoThumbnailFromURL(claim.getVideoURL());
//            imageViewAnswerTakeVideo.setImageBitmap(Bitmap.createScaledBitmap(bitmap, imageViewAnswerTakeVideo.getWidth(), imageViewAnswerTakeVideo.getHeight(), false));


        } else if (claimCurrentStep.getIntValue() == ClaimCurrentStep.WHAT_HAPPENED_EDITING.getIntValue()) {


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

            textViewAnswerTakeVideo.setClickable(false);
            buttonEditTakeVideo.setClickable(false);

        }

    }

    private void updateElseLayout() {

        buttonDoneElse.setClickable(true);
        editTextAnswerElse.setEnabled(true);

        if (claimCurrentStep == ClaimCurrentStep.ANSWERED_TAKE_VIDEO) {


            layoutQuestionElse.setBackgroundResource(R.drawable.file_a_claim_question_enabled);
            layoutAnwerElse.setBackgroundResource(R.drawable.claim_answer_saved);
            editTextAnswerElse.setText("");
            buttonDoneElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewQuestionElse.setText(getResources().getString(R.string.claim_question_else));
            textViewQuestionElse.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            editTextAnswerElse.setEnabled(true);

        } else if (claimCurrentStep == ClaimCurrentStep.ELSE_ANSWER_EDITING) {

            layoutQuestionElse.setBackgroundResource(R.drawable.file_a_claim_question_enabled);
            layoutAnwerElse.setBackgroundResource(R.drawable.claim_answer_saved);
            editTextAnswerElse.setText("");
            buttonDoneElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewQuestionElse.setText(getResources().getString(R.string.claim_question_else));
            textViewQuestionElse.setTextColor(getResources().getColor(R.color.colorNormalText, null));

            editTextAnswerElse.setEnabled(true);
        } else if (claimCurrentStep == ClaimCurrentStep.ANSWERED_ELSE) {

            layoutQuestionElse.setBackgroundResource(R.drawable.file_a_claim_question_enabled);
            layoutAnwerElse.setBackgroundResource(R.drawable.claim_answer_saved);
            buttonDoneElse.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            textViewQuestionElse.setText(getResources().getString(R.string.claim_question_else));
            textViewQuestionElse.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            editTextAnswerElse.setTextColor(getResources().getColor(R.color.colorNormalText, null));
            editTextAnswerElse.setText(claim.getExtraDescription());
            if (claim.getExtraDescription().isEmpty()) {

                buttonDoneElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            } else {
                buttonDoneElse.setTextColor(getResources().getColor(R.color.colorNormalBlue, null));
            }
            editTextAnswerElse.setEnabled(true);
        } else if (claimCurrentStep.getIntValue() == ClaimCurrentStep.WHAT_HAPPENED_EDITING.getIntValue()) {


        } else {

            layoutQuestionElse.setBackgroundResource(R.drawable.file_a_claim_question_disabled);
            layoutAnwerElse.setBackgroundResource(R.drawable.claim_answer_disabled);
            buttonDoneElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            textViewQuestionElse.setText(getResources().getString(R.string.claim_question_else));
            textViewQuestionElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            editTextAnswerElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));
            editTextAnswerElse.setEnabled(false);
            buttonDoneElse.setTextColor(getResources().getColor(R.color.colorInvalid, null));

            buttonDoneElse.setClickable(false);
            editTextAnswerElse.setEnabled(false);
        }
    }

    private boolean validateFields(ClaimState claimState) {

        if (claim.getWhatHappened() == null || claim.getWhatHappened().isEmpty()) {
            Toast.makeText(this, getString(R.string.message_input_what_happened), Toast.LENGTH_SHORT).show();
            return false;
        }

        if (claimState != ClaimState.INCOMPLETE) {

            if (claim.getWhenHappened() == null) {

                Toast.makeText(this, getString(R.string.message_input_when_happened), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (claim.getWhereHappened() == null || claim.getAddressHappened() == null || claim.getAddressHappened().isEmpty()) {
                Toast.makeText(this, getString(R.string.message_input_where_happened), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (claim.getDamagedParts() == null || claim.getDamagedParts().isEmpty()) {

                Toast.makeText(this, getString(R.string.message_select_damaged_zone), Toast.LENGTH_SHORT).show();
                return false;
            }

            if (claim.getVideoURL() == null || claim.getVideoURL().isEmpty()) {

                Toast.makeText(this, getString(R.string.claim_question_take_a_video), Toast.LENGTH_SHORT).show();
                return false;
            } else {

                String urlPrefix = "file://";

                if (claim.getVideoURL().contains(urlPrefix)) {


                    File file = new File(claim.getVideoURL().substring(urlPrefix.length()));
                    if (!file.exists()) {
                        Toast.makeText(this, getString(R.string.claim_question_take_a_video), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    File file1 = new File(claim.getImageURL().substring(urlPrefix.length()));
                    if (!file1.exists()) {
                        Toast.makeText(this, getString(R.string.claim_question_take_a_video), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else {
                    return true;
                }
            }
        }

        return true;

    }

    private void saveClaimToDb(ClaimState claimState) {

        if (!validateFields(claimState)) {

            return;
        }

        claim.setClaimState(claimState);

        claim.setExtraDescription(editTextAnswerElse.getText().toString());

        showWaitingScreen();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(
                Request.Method.POST, Constants.SERVER_HTTP_URL + "/api/add-claim",
                new com.android.volley.Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {

                        hideWaitingScreen();

                        String responseString = new String(response.data);

                        JSONObject object = null;
                        if (responseString != null) {
                            try {
                                object = new JSONObject(responseString);
                            } catch (Exception e) {
                                //Utils.appendLog(System.err.toString());
                                e.printStackTrace();
                            }
                        } else {

                            Toast.makeText(AddClaimActivity.this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (object == null) {

                            Toast.makeText(AddClaimActivity.this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        try {
                            if (object.getString("success").equals("true")) {

                                Constants.needHistoryRefresh = true;

                                JSONObject data = object.getJSONObject("data");
                                Toast.makeText(AddClaimActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();
                                claim.setId(data.getInt("claim_id"));
                                if (!isSaveOrSubmit) {
                                    finish();
                                }

                                if (data.getString("token_state").equals("valid")) {

                                    Iterator<String> keys = object.getJSONObject("data").keys();

                                    for (Iterator i = keys; i.hasNext(); ) {

                                        if (i.next().equals("refresh_token")) {
                                            String newPayload = data.get("refresh_token").toString();
                                            String newToken = data.getString("access_token");

                                            SharedHelper.putKey(AddClaimActivity.this, "access_token", newToken);
                                            SharedHelper.putKey(AddClaimActivity.this, "payload", newPayload);

                                            Utils.setAuthHabitSDK(AddClaimActivity.this);
                                        }
                                    }
                                }

                            } else if (object.getString("success").equals("false")) {

                                JSONObject data = object.getJSONObject("data");
                                Toast.makeText(AddClaimActivity.this, data.getString("message"), Toast.LENGTH_SHORT).show();

                                if (data.getString("token_state").equals("invalid")) {

                                    Utils.logout(AddClaimActivity.this, AddClaimActivity.this);
                                }

                            } else {

                                Toast.makeText(AddClaimActivity.this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
                            }


                        } catch (Exception e) {

                            Toast.makeText(AddClaimActivity.this, R.string.message_no_response, Toast.LENGTH_SHORT).show();
                            //Utils.appendLog(System.err.toString());
                            e.printStackTrace();
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitingScreen();
                Toast.makeText(AddClaimActivity.this, getResources().getString(R.string.message_no_response), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> paramObject = new HashMap<>();

                //paramObject.put("name", Globals.coverage.getCompany().getName());
                if (claim.getId() > 0) {
                    paramObject.put("claim_id", Long.valueOf(claim.getId()).toString());
                }
                if (claim.getWhereHappened() != null) {
                    paramObject.put("latitude", Double.valueOf(claim.getWhereHappened().getLatitude()).toString());
                    paramObject.put("longitude", Double.valueOf(claim.getWhereHappened().getLongitude()).toString());
                }
                if (claim.getAddressHappened() != null) {

                    paramObject.put("address", Utils.encodeAsUTF8(claim.getAddressHappened()));
                }

                paramObject.put("access_token", SharedHelper.getKey(AddClaimActivity.this, "access_token"));
                if (Globals.coverage.getId() != null) {
                    paramObject.put("coverage_id", Long.valueOf(Globals.coverage.getId()).toString());
                }
                if (claim.getWhatHappened() != null) {
                    paramObject.put("what_happened", Utils.encodeAsUTF8(claim.getWhatHappened()));


                }
                if (claim.getWhenHappened() != null) {
                    paramObject.put("time_happened", Long.valueOf(claim.getWhenHappened().getTimeInMillis() / 1000).toString());
                }

                if (claim.getDamagedParts() != null) {
                    paramObject.put("damaged_part", claim.getDamagedPartsString());
                }
                if (claim.getExtraDescription() != null) {


                    paramObject.put("note", Utils.encodeAsUTF8(claim.getExtraDescription()));


                }

                paramObject.put("state", Integer.valueOf(claimState.getIntValue()).toString());

                return paramObject;
            }

            @Override
            protected Map<String, VolleyMultipartRequest.DataPart> getByteData() throws AuthFailureError {
                Map<String, VolleyMultipartRequest.DataPart> params = new HashMap<>();

                if (Constants.isRecordingVehicleOrMileOrDamagedPart == 3) {

                    String urlPrefix = "file://";

                    if (claim.getVideoURL().contains(urlPrefix)) {

                        File file = new File(claim.getVideoURL().substring(urlPrefix.length()));
                        if (file.exists()) {
                            params.put("video", new VolleyMultipartRequest.DataPart("video-claim" + ".mp4", AppHelper.getFileDataFromUri(getVideoFilePath(AddClaimActivity.this)), "video/mp4"));

                        }

                        File file1 = new File(claim.getImageURL().substring(urlPrefix.length()));
                        if (file1.exists()) {
                            params.put("image", new VolleyMultipartRequest.DataPart("image-claim" + ".png", AppHelper.getFileDataFromUri(getImageFilePath(AddClaimActivity.this)), "image/png"));
                        }

                    }

                }

                return params;
            }
        };

        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.CONNECTION_TIMEOUT * 1000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(volleyMultipartRequest);
    }


    private void showWaitingScreen() {

        try {
            progressDialog.show();
        } catch (Exception e) {
            //Utils.appendLog(System.err.toString());
            e.printStackTrace();
        }

    }

    private void hideWaitingScreen() {

        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            //Utils.appendLog(System.err.toString());
            e.printStackTrace();
        }

    }


    /**
     * OnClick Handlers
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.button_back_to_onboarding) {
            finish();
        }

        if (!isEditable) {
            return;
        }

        switch (view.getId()) {

            case R.id.edittext_answer_else:
                //scrollView.scrollTo(0, scrollView.getBottom());
                break;
            case R.id.button_save:
                isSaveOrSubmit = true;
                saveClaimToDb(ClaimState.INCOMPLETE);
                break;


            case R.id.button_done_answer_else:
                Utils.hideKeyboard(this);
                claim.setExtraDescription(editTextAnswerElse.getText().toString());
                break;

            case R.id.button_submit:
                isSaveOrSubmit = false;

                saveClaimToDb(ClaimState.PENDING_REVIEW);
                break;

            case R.id.imagebutton_edit_answer_what_happend:
            case R.id.textview_answer_what_happend_title:
                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.dialog_claim_reason_selector);
                dialog.setTitle("");

                Button buttonCarAccident = dialog.findViewById(R.id.button_car_accident);
                Button buttonCarStolen = dialog.findViewById(R.id.button_car_stolen);
                Button buttonRockHitGlass = dialog.findViewById(R.id.button_rock_hit_glass);
                Button buttonNaturalHazard = dialog.findViewById(R.id.button_natural_hazard);
                Button buttonOtherReason = dialog.findViewById(R.id.button_other_reason);

                buttonCarAccident.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (claimCurrentStep.getIntValue() < ClaimCurrentStep.ANSWERED_WHATHAPPENED.getIntValue() && claimCurrentStep != ClaimCurrentStep.WHAT_HAPPENED_EDITING) {
                            claimCurrentStep = ClaimCurrentStep.ANSWERED_WHATHAPPENED;
                        }

                        claim.setWhatHappened(getResources().getString(R.string.claim_reason_car_accident));
                        dialog.dismiss();
                        updateViewContent();
                    }
                });

                buttonCarStolen.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (claimCurrentStep.getIntValue() < ClaimCurrentStep.ANSWERED_WHATHAPPENED.getIntValue() && claimCurrentStep != ClaimCurrentStep.WHAT_HAPPENED_EDITING) {
                            claimCurrentStep = ClaimCurrentStep.ANSWERED_WHATHAPPENED;
                        }
                        claim.setWhatHappened(getResources().getString(R.string.claim_reason_car_stolen));
                        dialog.dismiss();
                        updateViewContent();
                    }
                });

                buttonRockHitGlass.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (claimCurrentStep.getIntValue() < ClaimCurrentStep.ANSWERED_WHATHAPPENED.getIntValue() && claimCurrentStep != ClaimCurrentStep.WHAT_HAPPENED_EDITING) {
                            claimCurrentStep = ClaimCurrentStep.ANSWERED_WHATHAPPENED;
                        }
                        claim.setWhatHappened(getResources().getString(R.string.claim_reason_rock_hit_glass));
                        dialog.dismiss();
                        updateViewContent();
                    }
                });

                buttonNaturalHazard.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (claimCurrentStep.getIntValue() < ClaimCurrentStep.ANSWERED_WHATHAPPENED.getIntValue() && claimCurrentStep != ClaimCurrentStep.WHAT_HAPPENED_EDITING) {
                            claimCurrentStep = ClaimCurrentStep.ANSWERED_WHATHAPPENED;
                        }
                        claim.setWhatHappened(getResources().getString(R.string.claim_reason_natural_hazard));
                        dialog.dismiss();
                        updateViewContent();
                    }
                });

                buttonOtherReason.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //if (claimCurrentStep.getIntValue() < ClaimCurrentStep.WHAT_HAPPENED_EDITING.getIntValue()) {
                        if (claimCurrentStep.getIntValue() > ClaimCurrentStep.WHAT_HAPPENED_EDITING.getIntValue()) {
                            claimCurrentStepTemp = claimCurrentStep;
                        } else {
                            claimCurrentStepTemp = ClaimCurrentStep.ANSWERED_WHATHAPPENED;
                        }

                        claimCurrentStep = ClaimCurrentStep.WHAT_HAPPENED_EDITING;
                        //}
                        //claim.setWhatHappened(getResources().getString(R.string.claim_reason_other_reason));
                        dialog.dismiss();
                        updateViewContent();
                    }
                });

                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int densityDpi = (int) (metrics.density * 160f);

                wlp.width = width;

                window.setAttributes(wlp);
                dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_radius, null));

                dialog.show();
                break;


            case R.id.imagebutton_clear_answer_what_happened_description:
                editTextAnswerWhatHappenedDescription.setText("");
                break;


            case R.id.button_done_answer_what_happend_description:


                claimCurrentStep = claimCurrentStepTemp;

                claim.setWhatHappened(editTextAnswerWhatHappenedDescription.getText().toString());
                updateViewContent();

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                //Find the currently focused view, so we can grab the correct window token from it.
                View view1 = getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view1 == null) {
                    view1 = new View(this);
                }
                imm.hideSoftInputFromWindow(view1.getWindowToken(), 0);

                break;

            case R.id.imagebutton_edit_when_happend:
            case R.id.textview_answer_when_happend:
                showDatePicker();
                break;

            case R.id.imagebutton_edit_answer_where_happened:
            case R.id.textview_answer_where_happened:
                navigateToAddLocationActivity();
                break;

            case R.id.imagebutton_edit_answer_damaged_part:
            case R.id.textview_answer_damaged_part:
                displaySelectDamagedPartDialog();

                break;

            case R.id.imagebutton_edit_answer_take_video:
            case R.id.textview_answer_take_video:

                checkPermission();
                Constants.isRecordingVehicleOrMileOrDamagedPart = 3;

                Intent intent = new Intent(AddClaimActivity.this, MyCameraActivity.class);
                startActivityForResult(intent, MY_CAMERA_ACTIVITY_REQUEST_CODE);


        }
    }


    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        // request camera permission if it has not been grunted.
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
            return false;
        }

        return true;
    }

    private void displaySelectDamagedPartDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_select_damaged_parts);
        dialog.setTitle("");

        ImageButton imageButtonLeftFenderPanel = dialog.findViewById(R.id.imagebutton_left_fender_panel);
        ImageButton imageButtonLeftFrontDoor = dialog.findViewById(R.id.imagebutton_left_front_door);
        ImageButton imageButton_leftQuarterPanel = dialog.findViewById(R.id.imagebutton_left_quarter_panel);
        ImageButton imageButtonLeftHood = dialog.findViewById(R.id.imagebutton_left_hood);
        ImageButton imageButtonRightHood = dialog.findViewById(R.id.imagebutton_right_hood);
        ImageButton imageButtonRightRoof = dialog.findViewById(R.id.imagebutton_right_roof);
        ImageButton imageButtonLeftRoof = dialog.findViewById(R.id.imagebutton_left_roof);
        ImageButton imageButtonLeftBack = dialog.findViewById(R.id.imagebutton_left_bottom);
        ImageButton imageButtonRightBack = dialog.findViewById(R.id.imagebutton_right_back);
        ImageButton imageButtonRightFenderPanel = dialog.findViewById(R.id.imagebutton_right_fender_panel);
        ImageButton imageButtonRightFrontDoor = dialog.findViewById(R.id.imagebutton_right_front_door);
        ImageButton imageButtonRightQuarterPanel = dialog.findViewById(R.id.imagebutton_right_quarter_panel);
        Button doneButton = dialog.findViewById(R.id.button_done);

        ArrayList<DamagedPart> selectedParts = new ArrayList<>();
        if (claim.getDamagedParts() != null && !claim.getDamagedParts().isEmpty()) {

            for (int i = 0; i < claim.getDamagedParts().size(); i++) {

                selectedParts.add(claim.getDamagedParts().get(i));

                switch (claim.getDamagedParts().get(i)) {
                    case LEFT_FENDER_PANEL:
                        imageButtonLeftFenderPanel.setImageResource(R.drawable.damaged_zone_left_fender_panel_selected);
                        break;
                    case LEFT_FRONT_DOOR:
                        imageButtonLeftFrontDoor.setImageResource(R.drawable.damaged_zone_left_front_door_selected);
                        break;
                    case LEFT_QUARTER_PANEL:
                        imageButton_leftQuarterPanel.setImageResource(R.drawable.damaged_zone_left_quarter_panel_selected);
                        break;
                    case LEFT_HOOD:
                        imageButtonLeftHood.setImageResource(R.drawable.damaged_zone_left_hood_selected);
                        break;
                    case RIGHT_HOOD:
                        imageButtonRightHood.setImageResource(R.drawable.damaged_zone_right_hood_selected);
                        break;
                    case RIGHT_ROOF:
                        imageButtonRightRoof.setImageResource(R.drawable.damaged_zone_right_roof_selected);

                        break;
                    case LEFT_ROOF:
                        imageButtonLeftRoof.setImageResource(R.drawable.damaged_zone_left_roof_selected);
                        break;
                    case LEFT_BACK:
                        imageButtonLeftBack.setImageResource(R.drawable.damaged_zone_left_back_selected);
                        break;
                    case RIGHT_BACK:
                        imageButtonRightBack.setImageResource(R.drawable.damaged_zone_right_back_selected);
                        break;
                    case RIGHT_FENDER_PANEL:
                        imageButtonRightFenderPanel.setImageResource(R.drawable.damaged_zone_right_fender_panel_selected);
                        break;
                    case RIGHT_FRONT_DOOR:
                        imageButtonRightFrontDoor.setImageResource(R.drawable.damaged_zone_right_front_door_selected);
                        break;
                    case RIGHT_QUARTER_PANEL:
                        imageButtonRightQuarterPanel.setImageResource(R.drawable.damaged_zone_right_quarter_panel_selected);
                        break;
                }
            }
        }

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!selectedParts.isEmpty()) {

                    if (claimCurrentStep.getIntValue() < ClaimCurrentStep.ANSWERED_WHATPARTDAMAGED.getIntValue() && claimCurrentStep != ClaimCurrentStep.WHAT_HAPPENED_EDITING) {
                        claimCurrentStep = ClaimCurrentStep.ANSWERED_WHATPARTDAMAGED;
                    }

                    claim.setDamagedParts(selectedParts);
                    updateViewContent();
                    dialog.dismiss();
                } else {
                    Toast.makeText(AddClaimActivity.this, R.string.message_select_damaged_zone, Toast.LENGTH_SHORT).show();
                }

            }
        });

        imageButtonLeftFenderPanel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.LEFT_FENDER_PANEL)) {
                    selectedParts.remove(DamagedPart.LEFT_FENDER_PANEL);
                    imageButtonLeftFenderPanel.setImageResource(R.drawable.damaged_zone_left_fender_panel);
                } else {
                    selectedParts.add(DamagedPart.LEFT_FENDER_PANEL);
                    imageButtonLeftFenderPanel.setImageResource(R.drawable.damaged_zone_left_fender_panel_selected);
                }
            }
        });

        imageButtonLeftFrontDoor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.LEFT_FRONT_DOOR)) {
                    selectedParts.remove(DamagedPart.LEFT_FRONT_DOOR);
                    imageButtonLeftFrontDoor.setImageResource(R.drawable.damaged_zone_left_front_door);
                } else {
                    selectedParts.add(DamagedPart.LEFT_FRONT_DOOR);
                    imageButtonLeftFrontDoor.setImageResource(R.drawable.damaged_zone_left_front_door_selected);
                }
            }
        });

        imageButton_leftQuarterPanel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.LEFT_QUARTER_PANEL)) {
                    selectedParts.remove(DamagedPart.LEFT_QUARTER_PANEL);
                    imageButton_leftQuarterPanel.setImageResource(R.drawable.damaged_zone_left_quarter_panel);
                } else {
                    selectedParts.add(DamagedPart.LEFT_QUARTER_PANEL);
                    imageButton_leftQuarterPanel.setImageResource(R.drawable.damaged_zone_left_quarter_panel_selected);
                }
            }
        });

        imageButtonLeftHood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.LEFT_HOOD)) {
                    selectedParts.remove(DamagedPart.LEFT_HOOD);
                    imageButtonLeftHood.setImageResource(R.drawable.damaged_zone_left_hood);
                } else {
                    selectedParts.add(DamagedPart.LEFT_HOOD);
                    imageButtonLeftHood.setImageResource(R.drawable.damaged_zone_left_hood_selected);
                }
            }
        });

        imageButtonLeftRoof.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.LEFT_ROOF)) {
                    selectedParts.remove(DamagedPart.LEFT_ROOF);
                    imageButtonLeftRoof.setImageResource(R.drawable.damaged_zone_left_roof);
                } else {
                    selectedParts.add(DamagedPart.LEFT_ROOF);
                    imageButtonLeftRoof.setImageResource(R.drawable.damaged_zone_left_roof_selected);
                }
            }
        });

        imageButtonLeftBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.LEFT_BACK)) {
                    selectedParts.remove(DamagedPart.LEFT_BACK);
                    imageButtonLeftBack.setImageResource(R.drawable.damaged_zone_left_back);
                } else {
                    selectedParts.add(DamagedPart.LEFT_BACK);
                    imageButtonLeftBack.setImageResource(R.drawable.damaged_zone_left_back_selected);
                }
            }
        });

        imageButtonRightHood.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.RIGHT_HOOD)) {
                    selectedParts.remove(DamagedPart.RIGHT_HOOD);
                    imageButtonRightHood.setImageResource(R.drawable.damaged_zone_right_hood);
                } else {
                    selectedParts.add(DamagedPart.RIGHT_HOOD);
                    imageButtonRightHood.setImageResource(R.drawable.damaged_zone_right_hood_selected);
                }
            }
        });


        imageButtonRightRoof.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.RIGHT_ROOF)) {
                    selectedParts.remove(DamagedPart.RIGHT_ROOF);
                    imageButtonRightRoof.setImageResource(R.drawable.damaged_zone_right_roof);
                } else {
                    selectedParts.add(DamagedPart.RIGHT_ROOF);
                    imageButtonRightRoof.setImageResource(R.drawable.damaged_zone_right_roof_selected);
                }
            }
        });


        imageButtonRightBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.RIGHT_BACK)) {
                    selectedParts.remove(DamagedPart.RIGHT_BACK);
                    imageButtonRightBack.setImageResource(R.drawable.damaged_zone_right_back);
                } else {
                    selectedParts.add(DamagedPart.RIGHT_BACK);
                    imageButtonRightBack.setImageResource(R.drawable.damaged_zone_right_back_selected);
                }
            }
        });

        imageButtonRightFenderPanel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.RIGHT_FENDER_PANEL)) {
                    selectedParts.remove(DamagedPart.RIGHT_FENDER_PANEL);
                    imageButtonRightFenderPanel.setImageResource(R.drawable.damaged_zone_right_fender_panel);
                } else {
                    selectedParts.add(DamagedPart.RIGHT_FENDER_PANEL);
                    imageButtonRightFenderPanel.setImageResource(R.drawable.damaged_zone_right_fender_panel_selected);
                }
            }
        });

        imageButtonRightFrontDoor.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.RIGHT_FRONT_DOOR)) {
                    selectedParts.remove(DamagedPart.RIGHT_FRONT_DOOR);
                    imageButtonRightFrontDoor.setImageResource(R.drawable.damaged_zone_right_front_door);
                } else {
                    selectedParts.add(DamagedPart.RIGHT_FRONT_DOOR);
                    imageButtonRightFrontDoor.setImageResource(R.drawable.damaged_zone_right_front_door_selected);
                }
            }
        });

        imageButtonRightQuarterPanel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (selectedParts.contains(DamagedPart.RIGHT_QUARTER_PANEL)) {
                    selectedParts.remove(DamagedPart.RIGHT_QUARTER_PANEL);
                    imageButtonRightQuarterPanel.setImageResource(R.drawable.damaged_zone_right_quarter_panel);
                } else {
                    selectedParts.add(DamagedPart.RIGHT_QUARTER_PANEL);
                    imageButtonRightQuarterPanel.setImageResource(R.drawable.damaged_zone_right_quarter_panel_selected);
                }
            }
        });


        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int) (metrics.density * 160f);

        wlp.height = height - (int) (200 * metrics.density);
        wlp.width = width - (int) (80 * metrics.density);

        window.setAttributes(wlp);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_radius, null));

        dialog.show();
    }

    private void navigateToAddLocationActivity() {

        Intent intent = new Intent(AddClaimActivity.this, AddLocationActivity.class);
        startActivityForResult(intent, ADD_LOCATION_REQUEST_CODE);
    }

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog pickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                GregorianCalendar currentDate = new GregorianCalendar();
                currentDate.set(Calendar.HOUR, 0);
                currentDate.set(Calendar.MINUTE, 0);
                currentDate.set(Calendar.SECOND, 0);
                currentDate.set(Calendar.MILLISECOND, 0);
                currentDate.set(Calendar.AM_PM, Calendar.AM);

                GregorianCalendar selectedDate = new GregorianCalendar(year, month, dayOfMonth);

                if (selectedDate.after(currentDate)) {

                    Toast.makeText(getBaseContext(), R.string.claim_date_is_future, Toast.LENGTH_SHORT).show();

                    if (claim.getWhenHappened() != null) {

                    } else {

                        Globals.coverage.setDateFrom(null);
                        textViewAnswerWhenHappened.setText(getString(R.string.file_a_claim_answer_when_happened_text_title));

                        if (claimCurrentStep.getIntValue() < ClaimCurrentStep.ANSWERED_WHATHAPPENED.getIntValue() && claimCurrentStep != ClaimCurrentStep.WHAT_HAPPENED_EDITING) {
                            claimCurrentStep = ClaimCurrentStep.ANSWERED_WHATHAPPENED;
                        }

                        updateViewContent();
                    }


                } else {

                    try {

                        if (claimCurrentStep.getIntValue() < ClaimCurrentStep.ANSWERED_WHENHAPPENED.getIntValue() && claimCurrentStep != ClaimCurrentStep.WHAT_HAPPENED_EDITING) {
                            claimCurrentStep = ClaimCurrentStep.ANSWERED_WHENHAPPENED;
                        }

                        claim.setWhenHappened(new GregorianCalendar(year, month, dayOfMonth));
                        textViewAnswerWhenHappened.setText(claim.getDateString());
                        updateViewContent();
                    } catch (Exception e) {
                        //Utils.appendLog(System.err.toString());
                        e.printStackTrace();
                    }

                }


            }
        }, year, month, day);

        pickerDialog.show();
    }

    private Marker marker = null;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMapWhereHappened = googleMap;

        if (claim.getWhatHappened() != null) {
            if (marker == null) {
                marker = googleMapWhereHappened.addMarker(new MarkerOptions().position(new LatLng(claim.getWhereHappened().getLatitude(), claim.getWhereHappened().getLongitude())).title("Marker"));
            } else {
                marker.setPosition(new LatLng(claim.getWhereHappened().getLatitude(), claim.getWhereHappened().getLongitude()));
            }
            googleMapWhereHappened.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(claim.getWhereHappened().getLatitude(), claim.getWhereHappened().getLongitude()), Constants.DEFAULT_MAP_ZOOM_LEVEL));

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ADD_LOCATION_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {


                if (claimCurrentStep.getIntValue() < ClaimCurrentStep.ANSWERED_WHEREHAPPENED.getIntValue() && claimCurrentStep != ClaimCurrentStep.WHAT_HAPPENED_EDITING) {
                    claimCurrentStep = ClaimCurrentStep.ANSWERED_WHEREHAPPENED;
                }

                claim.setWhereHappened(Constants.selectedLocation);

                if (googleMapWhereHappened != null && claim.getWhereHappened() != null) {

                    String address = Constants.selectedLocation.getProvider();
                    if (address.equals("Marker")) {
                        address = Utils.getAddressFromLocation(this, Constants.selectedLocation);
                    }

                    claim.setAddressHappened(address);

                    googleMapWhereHappened.addMarker(new MarkerOptions().position(new LatLng(claim.getWhereHappened().getLatitude(), claim.getWhereHappened().getLongitude())).title("Marker"));
                    googleMapWhereHappened.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(claim.getWhereHappened().getLatitude(), claim.getWhereHappened().getLongitude()), Constants.DEFAULT_MAP_ZOOM_LEVEL));

                }

                updateViewContent();
            }
        }

        if (requestCode == MY_CAMERA_ACTIVITY_REQUEST_CODE) {

            if (resultCode == RESULT_OK) {
                if (claimCurrentStep.getIntValue() < ClaimCurrentStep.ANSWERED_TAKE_VIDEO.getIntValue() && claimCurrentStep != ClaimCurrentStep.WHAT_HAPPENED_EDITING) {
                    claimCurrentStep = ClaimCurrentStep.ANSWERED_TAKE_VIDEO;
                }

                claim.setImageURL("file://" + BaseCameraActivity.getImageFilePath(AddClaimActivity.this));
                claim.setVideoURL("file://" + BaseCameraActivity.getVideoFilePath(AddClaimActivity.this));
                updateViewContent();
            }
        }
        super.onActivityResult(requestCode, requestCode, data);
    }

}
