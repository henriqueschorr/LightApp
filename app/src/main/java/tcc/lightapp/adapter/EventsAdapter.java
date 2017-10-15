package tcc.lightapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.models.Event;

/**
 * Created by Henrique on 05/10/2017.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {
    private List<Event> events;
    private Context context;
    //TODO: create event details
//    private GroupOnClickListener groupOnClickListener;

    public EventsAdapter(List<Event> events, Context context) {
        this.events = events;
        this.context = context;
//        this.groupOnClickListener = groupOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.events != null ? this.events.size() : 0;
    }

    @Override
    public EventsViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_events, viewGroup, false);
        EventsViewHolder holder = new EventsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final EventsViewHolder holder, final int position) {
        Event event = events.get(position);

        holder.eventName.setText(event.eventName);

//        if (groupOnClickListener != null) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    groupOnClickListener.onClickGroup(holder.itemView, position);
//                }
//            });
//        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public void removeEvent(int position) {
        events.remove(position);
    }

    public List<Event> getEvents() {
        return events;
    }

    public interface EventOnClickListener {
        void onClickEvent(View view, int idx);
    }

    public static class EventsViewHolder extends RecyclerView.ViewHolder {
        public TextView eventName;

        public EventsViewHolder(View view) {
            super(view);
            eventName = (TextView) view.findViewById(R.id.event_name);
        }
    }
}
