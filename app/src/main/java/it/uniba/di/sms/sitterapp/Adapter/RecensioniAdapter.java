package it.uniba.di.sms.sitterapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.sitterapp.Oggetti.Recensione;
import it.uniba.di.sms.sitterapp.R;

/**
 * Adapter per le recensioni
 */

public class RecensioniAdapter extends RecyclerView.Adapter<RecensioniAdapter.MyViewHolder>
        implements Filterable {



    private List<Recensione> recensioneList;
    private List<Recensione> recensioneListFiltered;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, description;
        public RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.usernameRecensione);
            description = (TextView) view.findViewById(R.id.descrizioneRecensione);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingBarRecensione);

        }

    }

    public RecensioniAdapter(List<Recensione> recensioneList) {
        this.recensioneList = recensioneList;
        this.recensioneListFiltered = recensioneList;
    }

    @Override
    public RecensioniAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recensione, parent, false);


        return new RecensioniAdapter.MyViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Recensione recensione = recensioneListFiltered.get(position);
        holder.username.setText(recensione.getUsername());
        holder.description.setText(recensione.getDescrizione());
        holder.ratingBar.setRating(recensione.getRating());
    }

    @Override
    public int getItemCount() {
        return recensioneListFiltered.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    recensioneListFiltered = recensioneList;
                } else {
                    List<Recensione> filteredList = new ArrayList<>();
                    for (Recensione r : recensioneList) {
                        filteredList.add(r);
                    }
                    recensioneListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = recensioneListFiltered;
                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                recensioneListFiltered = (ArrayList<Recensione>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface RecensioniAdapterListener {
        void onRecensioniSelected(Recensione recensione);
    }
}
