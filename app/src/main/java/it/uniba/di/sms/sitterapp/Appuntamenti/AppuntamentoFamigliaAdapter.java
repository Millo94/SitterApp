package it.uniba.di.sms.sitterapp.Appuntamenti;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.uniba.di.sms.sitterapp.R;

/**
 * Created by Feder on 21/05/2018.
 */

public class AppuntamentoFamigliaAdapter extends RecyclerView.Adapter<AppuntamentoFamigliaAdapter.AppuntamentoViewHolder> {
    private Context mCtx;
    private List<Appuntamento> appuntamentiList;

    public AppuntamentoFamigliaAdapter(Context mCtx, List<Appuntamento> appuntamentiList){
        this.mCtx =mCtx;
        this.appuntamentiList = appuntamentiList;
    }

    @Override
    public AppuntamentoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.appuntamento_famiglia_item, null);
        return new AppuntamentoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AppuntamentoFamigliaAdapter.AppuntamentoViewHolder holder, int position) {
        Appuntamento appuntamento = appuntamentiList.get(position);
        holder.family_name.setText(appuntamento.getFamily());
        holder.date.setText(appuntamento.getDate());
        holder.start_time.setText(appuntamento.getStart_time());
        holder.end_time.setText(appuntamento.getEnd_time());
    }

    @Override
    public int getItemCount() {
        return appuntamentiList.size();
    }

    class AppuntamentoViewHolder extends RecyclerView.ViewHolder {

        public TextView family_name, date, start_time, end_time, description;

        public AppuntamentoViewHolder(View view) {
            super(view);
            family_name = (TextView) view.findViewById(R.id.family_name);
            start_time = (TextView) view.findViewById(R.id.start_time);
            end_time = (TextView) view.findViewById(R.id.end_time);
            date = (TextView) view.findViewById(R.id.date);
        }
    }
}
