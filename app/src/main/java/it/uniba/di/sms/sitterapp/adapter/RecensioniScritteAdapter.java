package it.uniba.di.sms.sitterapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.Recensione;


    public class RecensioniScritteAdapter extends RecyclerView.Adapter<RecensioniScritteAdapter.MyViewHolder> {

        //Lista per le recensioni
        private List<Recensione> recensioneList;


        class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView labelDest, username, description, idAnnuncio;
            private RatingBar ratingBar;

            private MyViewHolder(View view) {
                super(view);
                labelDest = (TextView) view.findViewById(R.id.destinatario);
                username = (TextView) view.findViewById(R.id.usernameRecensione);
                description = (TextView) view.findViewById(R.id.descrizioneRecensione);
                ratingBar = (RatingBar) view.findViewById(R.id.ratingBarRecensione);

            }

        }

        //costruttore
        public RecensioniScritteAdapter(List<Recensione> recensioneList) {
            this.recensioneList = recensioneList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_recensione, parent, false);


            return new RecensioniScritteAdapter.MyViewHolder(viewItem);
        }

        @Override
        public void onBindViewHolder(RecensioniScritteAdapter.MyViewHolder holder, int position) {
            final Recensione recensione = recensioneList.get(position);
            //attribuzione dei dettagli dell'annuncio alle varie View
            holder.labelDest.setText("To:");
            holder.username.setText(recensione.getReceiver());
            holder.description.setText(recensione.getDescrizione());
            holder.ratingBar.setRating(recensione.getRating());
        }

        //restituisce il numero degli elementi presenti in recensioneList
        @Override
        public int getItemCount() {
            return recensioneList.size();
        }


    }


