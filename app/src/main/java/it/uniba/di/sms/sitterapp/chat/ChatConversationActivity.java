package it.uniba.di.sms.sitterapp.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.chat.fixtures.MessagesFixtures;
import it.uniba.di.sms.sitterapp.chat.messages.CustomIncomingImageMessageViewHolder;
import it.uniba.di.sms.sitterapp.chat.messages.CustomIncomingTextMessageViewHolder;
import it.uniba.di.sms.sitterapp.chat.messages.CustomOutcomingImageMessageViewHolder;
import it.uniba.di.sms.sitterapp.chat.messages.CustomOutcomingTextMessageViewHolder;
import it.uniba.di.sms.sitterapp.oggetti.Message;
import it.uniba.di.sms.sitterapp.oggetti.User;
import it.uniba.di.sms.sitterapp.utils.AppUtils;

public class ChatConversationActivity extends Activity
        implements MessagesListAdapter.OnMessageLongClickListener<Message>,
        MessageInput.InputListener,
        MessageInput.AttachmentsListener, MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    private int TOTAL_MESSAGES_COUNT = 0;

    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;

    private Menu menu;
    private int selectionCount;
    private Date lastLoadedDate;
    private Message lastMessage;

    private Bundle BUNDLE;
    private String CONVERSATION_UID;
    private String CONVERSATION_NAME;
    private String senderId;

    private MessagesList messagesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_holder_messages);

        BUNDLE = getIntent().getExtras();
        CONVERSATION_UID =BUNDLE.getString("conversationUID");
        CONVERSATION_NAME = BUNDLE.getString("conversationName");
        senderId = BUNDLE.getString("senderId");

        messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.with(ChatConversationActivity.this).load(url).into(imageView);
            }
        };
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(CONVERSATION_NAME);
        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chat").document(CONVERSATION_UID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                TOTAL_MESSAGES_COUNT = ((ArrayList<Object>)documentSnapshot.get("Messages")).size();
                Map<String,?> mapMessage = (HashMap<String,?>) documentSnapshot.get("lastMessage");
                String textMessage = (String) mapMessage.get("text");
                User userMessage = new User(documentSnapshot.getString("lastMessage.user.id"),
                        documentSnapshot.getString("lastMessage.user.name"),
                        documentSnapshot.getString("lastMessage.user.avatar"),
                        documentSnapshot.getBoolean("lastMessage.user.online"));
                String idMessage = (String) mapMessage.get("id");
                Date dateMessage = ((Timestamp) mapMessage.get("timestamp")).toDate();
                lastMessage = new Message(idMessage,userMessage,textMessage,dateMessage);
                messagesAdapter.addToStart(lastMessage, true);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                messagesAdapter.deleteSelectedMessages();
                break;
            case R.id.action_copy:
                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                AppUtils.showToast(this, R.string.copied_message, true);

                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, final int totalItemsCount) {
        Log.i("TAG", "onLoadMore: " + page + " " + totalItemsCount);
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            //loadMessages();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("chat").document(CONVERSATION_UID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists() && totalItemsCount < ((ArrayList<Object>) documentSnapshot.get("Messages")).size()){

                        List<Map<String,Object>> mapMessageList = (ArrayList<Map<String,Object>>)(documentSnapshot.get("Messages"));
                        Integer i = 0;
                        ArrayList<Message> messageList = new ArrayList<>();
                        for(Map<String,Object> mapMessage : mapMessageList){
                            String textMessage = (String) mapMessage.get("text");
                            User userMessage = new User((String) mapMessage.get("user"),(String) mapMessage.get("user"),"",true);
                            String idMessage = i.toString();
                            i++;
                            Date dateMessage = ((Timestamp) mapMessage.get("timestamp")).toDate();
                            Message message = new Message(idMessage,userMessage,textMessage,dateMessage);
                            //TODO DA RIVERE LA CONDIZIONE DI UGUAGLIANZA
                            if(!(message.getText().equals(lastMessage.getText()) ||
                                    message.getUser().equals(lastMessage.getUser()) ||
                                    message.getCreatedAt().equals(lastMessage.getCreatedAt()))) messageList.add(message);
                        }
                        messagesAdapter.addToEnd(messageList,true);
                    }
                }
            });
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_delete).setVisible(count > 0);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    protected void loadMessages() {
        new Handler().postDelayed(new Runnable() { //imitation of internet connection
            @Override
            public void run() {
                ArrayList<Message> messages = MessagesFixtures.getMessages(lastLoadedDate);
                lastLoadedDate = messages.get(messages.size() - 1).getCreatedAt();
                messagesAdapter.addToEnd(messages, false);
            }
        }, 1000);
    }

    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<Message>() {
            @Override
            public String format(Message message) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message.getCreatedAt());

                String text = message.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, createdAt);
            }
        };
    }
    @Override
    public boolean onSubmit(CharSequence input) {
        Map<String,Object> message = new HashMap<>();
        message.put("text",input);
        Timestamp timestamp = new Timestamp(new Date());
        message.put("timestamp",timestamp);
        message.put("user",senderId);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chat").document(CONVERSATION_UID).collection("Messages").add(message);
        return true;
    }

    @Override
    public void onAddAttachments() {
        messagesAdapter.addToStart(MessagesFixtures.getImageMessage(), true);
    }

    @Override
    public void onMessageLongClick(Message message) {
        AppUtils.showToast(this, R.string.on_log_click_message, false);
    }

    private void initAdapter() {

        //We can pass any data to ViewHolder with payload
        CustomIncomingTextMessageViewHolder.Payload payload = new CustomIncomingTextMessageViewHolder.Payload();
        //For example click listener
        payload.avatarClickListener = new CustomIncomingTextMessageViewHolder.OnAvatarClickListener() {
            @Override
            public void onAvatarClick() {
                Toast.makeText(ChatConversationActivity.this,
                        "Text message avatar clicked", Toast.LENGTH_SHORT).show();
            }
        };

        MessageHolders holdersConfig = new MessageHolders()
                .setIncomingTextConfig(
                        CustomIncomingTextMessageViewHolder.class,
                        R.layout.item_custom_incoming_text_message,
                        payload)
                .setOutcomingTextConfig(
                        CustomOutcomingTextMessageViewHolder.class,
                        R.layout.item_custom_outcoming_text_message)
                .setIncomingImageConfig(
                        CustomIncomingImageMessageViewHolder.class,
                        R.layout.item_custom_incoming_image_message)
                .setOutcomingImageConfig(
                        CustomOutcomingImageMessageViewHolder.class,
                        R.layout.item_custom_outcoming_image_message);

        messagesAdapter = new MessagesListAdapter<>(senderId, holdersConfig,imageLoader);
        messagesAdapter.setOnMessageLongClickListener(this);
        messagesAdapter.setLoadMoreListener(this);
        messagesList.setAdapter(messagesAdapter);
    }
}
