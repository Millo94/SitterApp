package it.uniba.di.sms.sitterapp.chat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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
    private final int MESSAGE_LIMIT = 10;
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;

    private Menu menu;
    private int selectionCount;
    private Date lastLoadedDate;
    private Message lastMessage;

    private Bundle BUNDLE;
    private String CONVERSATION_UID;
    private String CONVERSATION_NAME;
    private String senderId, receiverId;
    private User senderUser,receiverUser;

    //recycler view for messageList
    private MessagesList messagesList;
    private List<Message> messageArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_holder_messages);

        BUNDLE = getIntent().getExtras();
        CONVERSATION_UID =BUNDLE.getString("conversationUID");
        CONVERSATION_NAME = BUNDLE.getString("conversationName");
        senderId = BUNDLE.getString("senderId");
        receiverId = BUNDLE.getString("receiverId");
        messagesList = (MessagesList) findViewById(R.id.messagesList);
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.with(ChatConversationActivity.this).load(url).into(imageView);
            }
        };

        initAdapter();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(CONVERSATION_NAME);
        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        getReceiverUser(receiverId);
        getSenderUser(senderId);

        if(TOTAL_MESSAGES_COUNT == 0)loadMessages();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chat").document(CONVERSATION_UID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Map<String,?> mapMessage = (HashMap<String,?>) documentSnapshot.get("lastMessage");
                String textMessage = (String) mapMessage.get("text");
                User userMessage = new User(documentSnapshot.getString("lastMessage.user.id"),
                        documentSnapshot.getString("lastMessage.user.name"),
                        documentSnapshot.getString("lastMessage.user.avatar"),
                        documentSnapshot.getBoolean("lastMessage.user.online"));
                String idMessage = (String) mapMessage.get("id");
                Date dateMessage = ((Timestamp) mapMessage.get("timestamp")).toDate();
                Message message = new Message(idMessage,userMessage,textMessage,dateMessage);

                //verifica che il messaggio successivo sia diverso da quello precedente
                // e che non faccia il comando all'apertura dell'app (perché viene caricata tutta la lista dei messaggi)
                if(!message.equals(lastMessage)&& TOTAL_MESSAGES_COUNT>0) {
                    lastMessage = message;
                    messagesAdapter.addToStart(lastMessage, true);
                }
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
        if (totalItemsCount+MESSAGE_LIMIT < TOTAL_MESSAGES_COUNT) {

            messagesAdapter.addToEnd(messageArrayList.subList(totalItemsCount,totalItemsCount+MESSAGE_LIMIT-1),false);
        }else if(totalItemsCount < TOTAL_MESSAGES_COUNT){
            messagesAdapter.addToEnd(messageArrayList.subList(totalItemsCount,TOTAL_MESSAGES_COUNT-1),false);
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_delete).setVisible(count > 0);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }

    protected void loadMessages() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chat")
                .document(CONVERSATION_UID)
                .collection("Messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot  querySnapshot= task.getResult();
                            if(!querySnapshot.isEmpty()){
                                List<DocumentSnapshot> documentSnapshotList = querySnapshot.getDocuments();
                                TOTAL_MESSAGES_COUNT = documentSnapshotList.size();
                                for(int i=0;i<TOTAL_MESSAGES_COUNT;++i){
                                    DocumentSnapshot SnapshotMessageList = documentSnapshotList.get(i);
                                    String textMessage = (String) SnapshotMessageList.get("text");
                                    User userMessage;
                                    if(SnapshotMessageList.getString("user").equals(senderId)){
                                        userMessage = senderUser;
                                    }else{
                                        userMessage = receiverUser;
                                    }
                                    String idMessage = String.valueOf(i);
                                    Date dateMessage = ((Timestamp) SnapshotMessageList.get("timestamp")).toDate();
                                    Message message = new Message(idMessage,userMessage,textMessage,dateMessage);

                                    messageArrayList.add(message);
                                }
                                messagesAdapter.addToEnd(messageArrayList.subList(0,MESSAGE_LIMIT-1),false);
                            }
                        }else{
                            Log.i("TAG", "onLoadMore: " +task.getResult());
                        }
                    }
                });

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
        message.put("text",input.toString());
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date);
        message.put("timestamp",timestamp);
        message.put("user",senderId);

        Map<String,Object> lastMsg = new HashMap<>();
        lastMsg.put("text",input.toString());
        lastMsg.put("id",String.valueOf(TOTAL_MESSAGES_COUNT+1));
        lastMsg.put("timestamp",timestamp);
        Map<String,Object> user = new HashMap<>();
        user.put("id",senderUser.getId());
        user.put("name",senderUser.getName());
        user.put("avatar",senderUser.getAvatar());
        user.put("online",senderUser.isOnline());
        lastMsg.put("user",user);

        Map<String,Object> mapLastMessage = new HashMap<>();
        mapLastMessage.put("lastMessage",lastMsg);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("chat").document(CONVERSATION_UID).collection("Messages").add(message);
        db.collection("chat").document(CONVERSATION_UID).update(mapLastMessage);
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
        messageArrayList = new ArrayList<Message>();
    }

    private void getReceiverUser(String userID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("utente").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){

                }else{
                    User user = new User(documentSnapshot.getId(),
                            documentSnapshot.getString("NomeCompleto"),
                            documentSnapshot.getString("Avatar"),
                            documentSnapshot.getBoolean("online"));
                    if(!user.equals(receiverUser)){
                        receiverUser = user;
                        messagesAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
    private void getSenderUser(String userID){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("utente").document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){

                }else{
                    User user = new User(documentSnapshot.getId(),
                            documentSnapshot.getString("NomeCompleto"),
                                    documentSnapshot.getString("Avatar"),
                                    documentSnapshot.getBoolean("online"));
                    if(!user.equals(senderUser)){
                        senderUser = user;
                        messagesAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
