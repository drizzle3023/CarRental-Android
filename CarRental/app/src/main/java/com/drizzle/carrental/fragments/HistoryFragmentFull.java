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
import com.drizzle.carrental.components.CustomAdapterForHistoryListView;
import com.drizzle.carrental.enumerators.ClaimState;
import com.drizzle.carrental.enumerators.PaymentState;
import com.drizzle.carrental.models.Claim;
import com.drizzle.carrental.models.HistoryModel;
import com.drizzle.carrental.models.Payment;

import java.util.ArrayList;
import java.util.GregorianCalendar;

public class HistoryFragmentFull extends Fragment {

    ArrayList<HistoryModel> dataModels;
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

                HistoryModel historyModel = dataModels.get(position);

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

            HistoryModel historyModel = new HistoryModel();
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
                claim.getCarURLs().add("https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwiByJGVsqbmAhUUIIgKHV7LDxgQjRx6BAgBEAQ&url=https%3A%2F%2Fcars.usnews.com%2Fcars-trucks%2Fbest-midsize-cars-for-the-money&psig=AOvVaw0GG1NL47H4V_K0iuIxZJe0&ust=1575906172816711");
                claim.getCarURLs().add("https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwiT8861sqbmAhVOFYgKHddOCO8QjRx6BAgBEAQ&url=https%3A%2F%2Fen.wikipedia.org%2Fwiki%2FSport_utility_vehicle&psig=AOvVaw0GG1NL47H4V_K0iuIxZJe0&ust=1575906172816711");

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
