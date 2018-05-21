package it.uniba.di.sms.sitterapp.Home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.sms.sitterapp.R;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Notice> noticeList;
    private List<Notice> noticeListFiltered;
    private NoticeAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView family_name, date,start_time,end_time,description;

        public MyViewHolder(View view) {
            super(view);
            family_name = (TextView) view.findViewById(R.id.family_name);
            description = (TextView) view.findViewById(R.id.notice_detail);
            start_time = (TextView) view.findViewById(R.id.start_time);
            end_time = (TextView) view.findViewById(R.id.end_time);
            date = (TextView) view.findViewById(R.id.date);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onNoticeSelected(noticeListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }
    public NoticeAdapter(Context context, List<Notice> noticeList, NoticeAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.noticeList = noticeList;
        this.noticeListFiltered = noticeList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.notice_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Notice notice = noticeListFiltered.get(position);
        holder.family_name.setText(notice.getFamily());
        holder.date.setText(notice.getDate());
        holder.description.setText(notice.getDescription());
        holder.start_time.setText(notice.getStart_time());
        holder.end_time.setText(notice.getEnd_time());
    }

    @Override
    public int getItemCount() {
        return noticeListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    noticeListFiltered = noticeList;
                } else {
                    List<Notice> filteredList = new ArrayList<>();
                    for (Notice row : noticeList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getDate().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    noticeListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = noticeListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                noticeListFiltered = (ArrayList<Notice>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface NoticeAdapterListener {
        void onNoticeSelected(Notice notice);
    }
}
