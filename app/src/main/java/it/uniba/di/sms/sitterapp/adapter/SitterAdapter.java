package it.uniba.di.sms.sitterapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.oggetti.UtenteSitter;

/**
 * Classe Adapter per gestire la lista delle babySitter nel menù della famiglia
 */

public class SitterAdapter extends RecyclerView.Adapter<SitterAdapter.MyViewHolder> {

    private Context context;
    private List<UtenteSitter> sitterList;
    private ContactsSitterAdapterListener listener;

    //costruttore
    public SitterAdapter(Context context, List<UtenteSitter> sitterList, ContactsSitterAdapterListener listener) {
        this.context = context;
        this.sitterList = sitterList;
        this.listener = listener;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, engages;
        private ImageView photo;
        private RatingBar ratingBar;

        private MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            photo = (ImageView) view.findViewById(R.id.thumbnail);
            ratingBar = (RatingBar) view.findViewById(R.id.ratingSitterItem);
            engages = (TextView) view.findViewById(R.id.ingaggiSitterItem);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onSitterSelected(sitterList.get(getAdapterPosition()));
                }
            });
        }

    }

    public void updateSitterList(List<UtenteSitter> sitterList) {
        this.sitterList = sitterList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sitter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final UtenteSitter contact = sitterList.get(position);
        //attribuzione dei dettagli dell'annuncio alle varie View
        holder.name.setText(contact.getUsername());
        holder.ratingBar.setRating(contact.getRating());
        holder.engages.setText(String.valueOf(contact.getNumLavori()));

        Glide.with(context).load(contact.getFoto()).apply(RequestOptions.centerCropTransform()).into(holder.photo);
    }

    //restituisce il numero degli elementi in sitter list
    @Override
    public int getItemCount() {
        return sitterList.size();
    }


    //interfaccia di comunicazione tra adapter e listView
    public interface ContactsSitterAdapterListener {
        void onSitterSelected(UtenteSitter sitter);
    }
}
