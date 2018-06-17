package it.uniba.di.sms.sitterapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.uniba.di.sms.sitterapp.Oggetti.Notice;
import it.uniba.di.sms.sitterapp.R;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder> {
    private Context context;
    private List<Notice> noticeList;
    private List<Notice> noticeListFiltered;
    private NoticeAdapterListener listener;
    private final static int TEXT_TO_SHOW = 100;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView family_name, date, start_time, end_time, description, scad;

        MyViewHolder(View view) {
            super(view);
            family_name = (TextView) view.findViewById(R.id.appuntamento_item_username);
            description = (TextView) view.findViewById(R.id.appuntamento_item_detail);
            start_time = (TextView) view.findViewById(R.id.appuntamento_item_start);
            end_time = (TextView) view.findViewById(R.id.appuntamento_item_end);
            date = (TextView) view.findViewById(R.id.appuntamento_item_data);
            scad = (TextView) view.findViewById(R.id.scadutoCard);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onNoticeSelected(noticeListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    //castruttore
    public NoticeAdapter(Context context, List<Notice> noticeList, NoticeAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.noticeList = noticeList;
        this.noticeListFiltered = noticeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        //attribuzione dei dettagli dell'annuncio alle varie View
        final Notice notice = noticeListFiltered.get(position);
        holder.family_name.setText(notice.getFamily());
        holder.date.setText(notice.getDate());
        holder.description.setText((notice.getDescription().length() > TEXT_TO_SHOW ? notice.getDescription().substring(0, TEXT_TO_SHOW) + "..." : notice.getDescription()));
        holder.start_time.setText(notice.getStart_time());
        holder.end_time.setText(notice.getEnd_time());


        //Controllo se l'annuncio è scaduto
        Calendar c = Calendar.getInstance();
        int anno = c.get(Calendar.YEAR);
        int mese = c.get(Calendar.MONTH) + 1; //+1 perchè Calendar legge i mesi da 0 a 11
        int giorno = c.get(Calendar.DATE);

        String data = notice.getDate();
        Integer annoNotice, meseNotice, giornoNotice;
        annoNotice = Integer.valueOf(data.substring(6, 10));
        meseNotice = Integer.valueOf(data.substring(3, 5));
        giornoNotice = Integer.valueOf(data.substring(0, 2));

        boolean scaduto;


        if (annoNotice < anno) {
            scaduto = true;
        } else if (annoNotice == anno && meseNotice < mese) {
            scaduto = true;
        } else if (annoNotice == anno && meseNotice == mese && giornoNotice < giorno) {
            scaduto = true;
        } else {
            scaduto = false;
        }

        if (scaduto) {
            //se l'annuncio è scaduto setto la visibilità a VISIBLE della textView
            if (holder.scad.getVisibility() == View.GONE) {
                holder.scad.setVisibility(View.VISIBLE);
            }
        }


    }

    //restutuisce il numero degli elementi della lista
    @Override
    public int getItemCount() {
        return noticeListFiltered.size();
    }

    //interfaccia di comunicazione tra adapter e listView
    public interface NoticeAdapterListener {
        void onNoticeSelected(Notice notice);
    }


}
