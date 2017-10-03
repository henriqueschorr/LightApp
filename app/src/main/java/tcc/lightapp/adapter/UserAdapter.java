package tcc.lightapp.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import tcc.lightapp.R;
import tcc.lightapp.models.User;

/**
 * Created by Henrique on 20/09/2017.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UsersViewHolder> {
    private List<User> users;
    private Context context;
    private UserOnClickListener userOnClickListener;

    public UserAdapter(List<User> users, Context context, UserOnClickListener userOnClickListener) {
        this.users = users;
        this.context = context;
        this.userOnClickListener = userOnClickListener;
    }

    @Override
    public int getItemCount() {
        return this.users != null ? this.users.size() : 0;
    }

    @Override
    public UsersViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_users, viewGroup, false);
        UsersViewHolder holder = new UsersViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final UsersViewHolder holder, final int position) {
        User user = users.get(position);

        holder.tNome.setText(user.userName);

        if (userOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userOnClickListener.onClickUser(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(int position) {
        users.remove(position);
    }

    public List<User> getUsers() {
        return users;
    }

    public interface UserOnClickListener {
        void onClickUser(View view, int idx);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        public TextView tNome;
        CardView cardView;

        public UsersViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            tNome = (TextView) view.findViewById(R.id.userName);
        }
    }
}
