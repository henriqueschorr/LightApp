package tcc.lightapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

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
    private View mViewUser;
    private View mViewFriend;
    private static final int USER_LIST = 1;
    private static final int FRIEND_LIST = 2;
    private int teste;

    public UserAdapter(List<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

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
        UsersViewHolder holder;
        if (viewType == USER_LIST) {
            mViewUser = LayoutInflater.from(context).inflate(R.layout.adapter_users, viewGroup, false);
            holder = new UsersViewHolder(mViewUser);
        } else {
            mViewFriend = LayoutInflater.from(context).inflate(R.layout.adapter_friends, viewGroup, false);
            holder = new UsersViewHolder(mViewFriend);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(final UsersViewHolder holder, final int position) {
        User user = users.get(position);

        holder.userName.setText(user.userName);

        //                TODO: set item selected in screen
        if(position == teste) {
//            int red = Color.parseColor("#C62828");
//            holder.cardView.setBackgroundColor(red);
            holder.cardView.setSelected(true);
        }

        if (userOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    userOnClickListener.onClickUser(holder.itemView, position);
                    teste = position;
                    notifyItemChanged(teste);
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

    @Override
    public int getItemViewType(int position) {
        if (users.get(position).firebaseToken == null) {
            return FRIEND_LIST;
        } else {
            return USER_LIST;
        }
    }

    public void setItemSelected(){
        View item = mViewFriend.findViewById(R.id.card_view);
        item.setSelected(true);
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public CardView cardView;

        public UsersViewHolder(View view) {
            super(view);
            userName = (TextView) view.findViewById(R.id.userName);
            cardView = (CardView) view.findViewById(R.id.card_view);
        }
    }

}
