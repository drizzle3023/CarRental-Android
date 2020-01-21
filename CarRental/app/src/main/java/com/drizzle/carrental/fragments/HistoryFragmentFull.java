package com.drizzle.carrental.fragments;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.adapters.CustomAdapterForHistoryListView;
import com.drizzle.carrental.api.ApiClient;
import com.drizzle.carrental.api.ApiInterface;
import com.drizzle.carrental.enumerators.CoverageState;
import com.drizzle.carrental.enumerators.PaymentState;
import com.drizzle.carrental.globals.Constants;
import com.drizzle.carrental.globals.SharedHelper;
import com.drizzle.carrental.models.Claim;
import com.drizzle.carrental.models.Company;
import com.drizzle.carrental.models.Coverage;
import com.drizzle.carrental.models.History;
import com.drizzle.carrental.models.Payment;
import com.drizzle.carrental.serializers.ParseHistory;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragmentFull extends Fragment {

    private ArrayList<History> dataModels;
    private ListView listView;

    private static CustomAdapterForHistoryListView adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_full, container, false);


        listView = (ListView) view.findViewById(R.id.list_history);

        dataModels = new ArrayList<>();

        fetchHistoryFromServer();


        return view;
    }

    private void fetchHistoryFromServer() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        JsonObject gsonObject = new JsonObject();
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("access_token", SharedHelper.getKey(getActivity(), "access_token"));

            JsonParser jsonParser = new JsonParser();
            gsonObject = (JsonObject) jsonParser.parse(paramObject.toString());

            apiInterface.getHistoryList(gsonObject).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response.body().string());

                        if (object.getString("success").equals("true")) {

                            JSONObject data = object.getJSONObject("data");
                            JSONArray jsonHistory = data.getJSONArray("historyList");

                            for (int i = 0; i < jsonHistory.length(); i++) {

                                JSONObject item = jsonHistory.getJSONObject(i);

                                Log.d("tiny-debug", "onResponse: " + item.getInt("id"));
                                ParseHistory history = new ParseHistory();

                                history.setType(item.getString("type"));
                                history.setContent(item.getJSONObject("content"));

                                History historyModel = new History();

                                if (history.getType().equals(Constants.HISTORY_TYPE_COVERAGE)) {

                                    Coverage coverage = new Coverage();

                                    JSONObject content = history.getContent();

                                    int state = 1;
                                    try {
                                        state = content.getInt("state");
                                    } catch (Exception e) {

                                    }

                                    CoverageState coverageState = CoverageState.values()[state - 1];


                                    coverage.setActiveState(true);
                                    coverage.setState(coverageState);


                                    try {
                                        coverage.setTitle(content.getString("name"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        coverage.setLocationAddress(content.getString("address"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    Location loc = new Location("location");


                                    try {
                                        loc.setLatitude(content.getDouble("latitude"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        loc.setLongitude(content.getDouble("longitude"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    coverage.setLocation(loc);

                                    //set company
                                    Company company = new Company();
                                    try {
                                        company.setId(content.getLong("company_id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    GregorianCalendar calFrom = new GregorianCalendar();
                                    try {
                                        calFrom.setTimeInMillis(content.getLong("start_at") * 1000);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    coverage.setDateFrom(calFrom);

                                    GregorianCalendar calTo = new GregorianCalendar();
                                    try {
                                        calTo.setTimeInMillis(content.getLong("end_at") * 1000);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    coverage.setDateTo(calTo);


                                    try {
                                        coverage.setUrlVideoVehicle(Constants.MEDIA_PATH_URL + content.getString("video_vehicle"));
                                    }
                                    catch (Exception e) {

                                    }

                                    try {
                                        coverage.setUrlImageVehicle(Constants.MEDIA_PATH_URL + content.getString("image_vehicle"));
                                    }
                                    catch (Exception e) {

                                    }

                                    try {
                                        coverage.setUrlVideoMile(Constants.MEDIA_PATH_URL + content.getString("video_mile"));
                                    }
                                    catch (Exception e) {

                                    }

                                    try {
                                        coverage.setUrlImageMile(Constants.MEDIA_PATH_URL + content.getString("image_mile"));
                                    }
                                    catch (Exception e) {

                                    }

                                    historyModel.setCoverage(coverage);
                                    historyModel.setPaymentOrCoverage(false);

                                } else if (history.getType().equals(Constants.HISTORY_TYPE_PAYMENT)) {

                                    Payment payment = new Payment();

                                    JSONObject content = history.getContent();

                                    int state = 1;
                                    try {
                                        state = content.getInt("state");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    PaymentState paymentState = PaymentState.values()[state - 1];

                                    payment.setState(paymentState);
                                    payment.setTitle(paymentState.name());

                                    double amount = 0;
                                    try {
                                        amount = content.getDouble("amount");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    String currency = null;
                                    try {
                                        currency = content.getString("currency");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    String strAmount = String.format(Locale.getDefault(), "%.2f", amount);
                                    if (currency.equals("EUR")) {
                                        currency = getResources().getString(R.string.euro_character);
                                    } else if (currency.equals("USD")) {
                                        currency = getResources().getString(R.string.usd_character);
                                    }
                                    String paymentInformation = strAmount + " " + currency + "/ per year";
                                    payment.setInformation(paymentInformation);

                                    GregorianCalendar calendar = new GregorianCalendar();
                                    try {
                                        calendar.setTimeInMillis(content.getLong("date") * 1000);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    payment.setPaymentDate(calendar);

                                    historyModel.setPayment(payment);
                                    historyModel.setPaymentOrCoverage(true);

                                } else {

                                    Claim claim = new Claim();

                                    JSONObject content = history.getContent();

                                    continue;
                                }

                                dataModels.add(historyModel);

                                adapter = new CustomAdapterForHistoryListView(dataModels, getActivity().getApplicationContext());

                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        History historyModel = dataModels.get(position);
                                    }
                                });
                            }

                        } else {
                            JSONObject data = object.getJSONObject("data");
                            Toast.makeText(getContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressDialog.dismiss();
                    t.printStackTrace();
                    Toast.makeText(getContext(), "Server connect error", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Prepare test data
     */
    private void prepareTestData() {

        /**!!!!!!!!!!!!!!!!!!!!!!!!
         * !!!!!!!!!!!!!!!!!!!!!!!!
         * Below part should be removed
         * !!!!!!!!!!!!!!!!!!!!!!!!
         * !!!!!!!!!!!!!!!!!!!!!!!!
         */

        for (int i = 0; i < 10; i++) {

            History historyModel = new History();
            if (i % 2 == 0) {
                Coverage coverage = new Coverage();

                if (i % 3 == 0) {
                    coverage.setActiveState(true);
                } else {
                    coverage.setActiveState(false);
                }


                coverage.setTitle("Budge Rental Car");
                coverage.setState(CoverageState.COVERED);
                coverage.setDateFrom(new GregorianCalendar(2019, 2, 2));
                coverage.setDateTo(new GregorianCalendar(2020, 2, 1));
                coverage.setLocationAddress("New York, United States");

                historyModel.setCoverage(coverage);
                historyModel.setPaymentOrCoverage(false);

            } else {
                Payment payment = new Payment();

                payment.setTitle("Payment Success");
                payment.setState(PaymentState.AUTHORISED);
                payment.setPaymentDate(new GregorianCalendar(2019, 2, 2));
                payment.setInformation("49.99€ / per year");

                historyModel.setPayment(payment);
                historyModel.setPaymentOrCoverage(true);

            }

            dataModels.add(historyModel);
        }
        //!!!!!!!!!!!!!!!!!!!!!!!! Above should be removed !!!!!!!!!!!!!!!!!!!!!!!!


    }
}
