package com.drizzle.carrental.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.drizzle.carrental.R;
import com.drizzle.carrental.adapters.CustomAdapterForHistoryListView;
import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.enumerators.PaymentState;
import com.drizzle.carrental.models.Claim;
import com.drizzle.carrental.models.History;
import com.drizzle.carrental.models.Payment;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class HistoryFragmentFull extends Fragment {

    ArrayList<History> dataModels;
    ListView listView;
    private static CustomAdapterForHistoryListView adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history_full, container, false);


        listView=(ListView) view.findViewById(R.id.list_history);

        dataModels = new ArrayList<>();

        prepareTestData();

        adapter= new CustomAdapterForHistoryListView(dataModels, getActivity().getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                History historyModel = dataModels.get(position);

            }
        });

        return view;
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

        for (int i = 0; i < 10; i ++) {

            History historyModel = new History();
            if (i % 2 == 0) {
                Claim claim = new Claim();

                if (i % 3 == 0) {
                    claim.setActiveState(true);
                }
                else {
                    claim.setActiveState(false);
                }


                claim.setTitle("Budge Rental Car");
                claim.setState(ClaimState.COVERED);
                claim.setDateFrom(new GregorianCalendar(2019, 2,2));
                claim.setDateTo(new GregorianCalendar(2020, 2,1));
                claim.setLocation("New York, United States");
                claim.getCarURLs().add("http://i.imgur.com/DvpvklR.png");
                claim.getCarURLs().add("https://png.pngtree.com/element_our/20190523/ourlarge/pngtree-car-driving-box-type-long-motor-vehicle-line-image_1088711.jpg");

                historyModel.setClaim(claim);
                historyModel.setPaymentOrClaim(false);

            }
            else {
                Payment payment = new Payment();

                payment.setTitle("Payment Success");
                payment.setState(PaymentState.SUCCESS);
                payment.setPaymentDate(new GregorianCalendar(2019,2,2));
                payment.setInformation("49.99€ / per year");

                historyModel.setPayment(payment);
                historyModel.setPaymentOrClaim(true);

            }

            dataModels.add(historyModel);
        }
        //!!!!!!!!!!!!!!!!!!!!!!!! Above should be removed !!!!!!!!!!!!!!!!!!!!!!!!


    }
}
