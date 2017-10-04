package tcc.lightapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import tcc.lightapp.R;
import tcc.lightapp.models.ChatMessage;

/**
 * Created by Henrique on 01/10/2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{
    private List<ChatMessage> mChatMessages;
    private Context context;
    private static final int VIEW_TYPE_ME = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, Context context) {
        this.mChatMessages = chatMessages;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return this.mChatMessages != null ? this.mChatMessages.size() : 0;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        ChatViewHolder viewHolder = null;
        switch (viewType) {
            case VIEW_TYPE_ME:
                View viewChatMine = layoutInflater.inflate(R.layout.item_chat_mine, parent, false);
                viewHolder = new ChatViewHolder(viewChatMine);
                break;
            case VIEW_TYPE_OTHER:
                View viewChatOther = layoutInflater.inflate(R.layout.item_chat_other, parent, false);
                viewHolder = new ChatViewHolder(viewChatOther);
                break;
        }
        return viewHolder;
    }
//
    @Override
    public void onBindViewHolder(ChatViewHolder viewHolder, int position) {
//        if (TextUtils.equals(mChatsMessages.get(position).senderUid,
//                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
//            configureMyChatViewHolder((MyChatViewHolder) holder, position);
//        } else {
//            configureOtherChatViewHolder((OtherChatViewHolder) holder, position);
//        }
        ChatMessage chatMessage = mChatMessages.get(position);

        String alphabet = chatMessage.sender.substring(0, 1);

        viewHolder.chatMessage.setText(chatMessage.message);
        viewHolder.userAlphabet.setText(alphabet);
    }

    //
    public void add(ChatMessage chatMessage) {
        mChatMessages.add(chatMessage);
        notifyItemInserted(mChatMessages.indexOf(chatMessage));
    }
//

//    private void configureMyChatViewHolder(MyChatViewHolder myChatViewHolder, int position) {
//        ChatMessage chatMessage = mChatsMessages.get(position);
//
//        String alphabet = chatMessage.sender.substring(0, 1);
//
//        myChatViewHolder.txtChatMessage.setText(chatMessage.message);
//        myChatViewHolder.txtUserAlphabet.setText(alphabet);
//    }
//
//    private void configureOtherChatViewHolder(OtherChatViewHolder otherChatViewHolder, int position) {
//        ChatMessage chatMessage = mChatsMessages.get(position);
//
//        String alphabet = chatMessage.sender.substring(0, 1);
//
//        otherChatViewHolder.txtChatMessage.setText(chatMessage.message);
//        otherChatViewHolder.txtUserAlphabet.setText(alphabet);
//    }
//

//
    @Override
    public int getItemViewType(int position) {
        if (TextUtils.equals(mChatMessages.get(position).senderUid,
                FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            return VIEW_TYPE_ME;
        } else {
            return VIEW_TYPE_OTHER;
        }
    }
//
    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        public TextView chatMessage;
        public TextView userAlphabet;

        public ChatViewHolder(View itemView) {
            super(itemView);
            chatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
            userAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
        }
    }
////
//    private static class OtherChatViewHolder extends RecyclerView.ViewHolder {
//        private TextView txtChatMessage, txtUserAlphabet;
//
//        public OtherChatViewHolder(View itemView) {
//            super(itemView);
//            txtChatMessage = (TextView) itemView.findViewById(R.id.text_view_chat_message);
//            txtUserAlphabet = (TextView) itemView.findViewById(R.id.text_view_user_alphabet);
//        }
//    }
}
