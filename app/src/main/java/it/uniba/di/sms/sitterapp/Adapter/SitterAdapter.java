package it.uniba.di.sms.sitterapp.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.Oggetti.UtenteSitter;

/**
 * Classe Adapter per gestire la lista delle babySitter nel menù della famiglia
 */

public class SitterAdapter extends RecyclerView.Adapter<SitterAdapter.MyViewHolder>
        implements Filterable {

    private Context context;
    private List<UtenteSitter> sitterList;
    private List<UtenteSitter> sitterListFilter;
    private ContactsSitterAdapterListener listener;

    public SitterAdapter(Context context, List<UtenteSitter> sitterList, ContactsSitterAdapterListener listener) {
        this.context = context;
        this.sitterList = sitterList;
        this.sitterListFilter = sitterList;
        this.listener = listener;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone;
        public ImageView photo;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            phone = (TextView) view.findViewById(R.id.phone);
            photo = (ImageView) view.findViewById(R.id.thumbnail);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onSitterSelected(sitterListFilter.get(getAdapterPosition()));
                }
            });
        }

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sitter, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final UtenteSitter contact = sitterListFilter.get(position);

        holder.name.setText(contact.getUsername());
        holder.phone.setText(contact.getNumero());
        Glide.with(context).load(contact.getFoto()).apply(RequestOptions.centerCropTransform()).into(holder.photo);
    }

    @Override
    public int getItemCount() {
        return sitterListFilter.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    sitterListFilter = sitterList;
                } else {
                    List<UtenteSitter> filteredList = new ArrayList<>();
                    for (UtenteSitter row : sitterList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getUsername().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    sitterListFilter = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = sitterListFilter;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                sitterListFilter = (ArrayList<UtenteSitter>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface ContactsSitterAdapterListener {
        void onSitterSelected(UtenteSitter sitter);
    }
}
