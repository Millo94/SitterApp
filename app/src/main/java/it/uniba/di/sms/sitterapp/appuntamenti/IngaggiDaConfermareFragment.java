package it.uniba.di.sms.sitterapp.appuntamenti;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.SessionManager;
import it.uniba.di.sms.sitterapp.adapter.NoticeAdapter;
import it.uniba.di.sms.sitterapp.oggetti.Notice;
import tr.xip.errorview.ErrorView;

public class IngaggiDaConfermareFragment extends Fragment {

    //Items ingaggi
    private List<Notice> noticeList;
    private NoticeAdapter noticeAdapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private RecyclerView recycler;
    private RecyclerView.LayoutManager layoutManager;

    private SessionManager sessionManager;
    ErrorView errorView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_home, container, false);

        sessionManager = new SessionManager(getActivity().getApplicationContext());


        layoutManager = new LinearLayoutManager(getContext());

        recycler.setLayoutManager(layoutManager);
        //TODO SISTEMARE VISUALIZZAZIONE INGAGGI
        noticeList = new ArrayList<>();
        //noticeAdapter = new NoticeAdapter(getContext(),noticeList,);
        recycler.setAdapter(noticeAdapter);

        errorView = (ErrorView) view.findViewById(R.id.errorView);
        loadNotice();

        return view;
    }

}
