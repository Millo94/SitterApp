package it.uniba.di.sms.sitterapp.Recensioni;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import it.uniba.di.sms.sitterapp.Adapter.NoticeAdapter;
import it.uniba.di.sms.sitterapp.Oggetti.Notice;
import it.uniba.di.sms.sitterapp.Php;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;


public class RecensioniScritteFragment extends Fragment {


    View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recensioni_scritte, container, false);



        return view;
    }
}


        /*
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerHome);

        noticeList = new ArrayList<>();
        noticeAdapter = new NoticeAdapter(getContext(), noticeList, scritteListener);
        remainingNoticeList = new LinkedList<>();

        recyclerView.setAdapter(noticeAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());



        //caricamento di annunci
        loadNotices();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                if (dy > 0) {
                    // Recycle view scrolling downwards...
                    // this if statement detects when user reaches the end of recyclerView, this is only time we should load more
                    if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN)) {
                        // remember "!" is the same as "== false"
                        // here we are now allowed to load more, but we need to be careful
                        // we must check if itShouldLoadMore variable is true [unlocked]
                        if (itShouldLoadMore) {

                            loadMore();
                        }
                    }
                }
            }
        });


        return view;
    }


     * Caricamento degli annunci (per la home delle babysitter)

    private void loadNotices() {

        itShouldLoadMore = false; // lock this guy,(itShouldLoadMore) to make sure,
        // user will not load more when volley is processing another request
        // only load more when  volley is free

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //TODO -> inserire php giusto
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Php.INGAGGI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressDialog.dismiss();
                        itShouldLoadMore = true;
                        try {
                            JSONArray notice = new JSONArray(response);
                            for (int i = 0; i < notice.length(); i++) {

                                JSONObject noticeObject = notice.getJSONObject(i);
                                String famiglia = noticeObject.getString("usernameFamiglia");
                                String data = noticeObject.getString("data");
                                String oraInizio = noticeObject.getString("oraInizio");
                                String oraFine = noticeObject.getString("oraFine");
                                String descrizione = noticeObject.getString("descrizione");
                                Notice n = new Notice(famiglia, data, oraInizio, oraFine, descrizione);

                                if (i < LOAD_LIMIT) {
                                    noticeList.add(n);
                                } else {
                                    remainingNoticeList.add(n);
                                }
                            }

                            noticeAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", sessionManager.getSessionUsername());
                params.put("tipoutente", String.valueOf(sessionManager.getSessionType()));
                return params;
            }
        };
        Volley.newRequestQueue(getContext()).add(stringRequest);
    }


     * Caricamento incrementale degli annunci

    private void loadMore() {

        itShouldLoadMore = false; // lock this until volley completes processing

        // progressWheel is just a loading spinner, please see the content_main.xml
        final ProgressWheel progressWheel = (ProgressWheel) view.findViewById(R.id.progress_wheel_home);
        progressWheel.setVisibility(View.VISIBLE);

        itShouldLoadMore = true;

        if (!remainingNoticeList.isEmpty()) {
            //todo aggiungere un ritardo (asynctask) per visualizzare la ruota figa di caricamento
            final int remainingNoticeListSize = remainingNoticeList.size();
            for (int i = 0; i < remainingNoticeListSize; ++i) {
                if (i < LOAD_LIMIT) {
                    noticeList.add(remainingNoticeList.remove());
                }
            }
            noticeAdapter.notifyDataSetChanged();
        }
        progressWheel.setVisibility(View.GONE);
    }

    @Override
    public void onNoticeSelected(Notice notice) {
        Toast.makeText(getContext(),"Ancora da implementare", Toast.LENGTH_SHORT).show();
    }



     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Notice notice);
   }
   */

