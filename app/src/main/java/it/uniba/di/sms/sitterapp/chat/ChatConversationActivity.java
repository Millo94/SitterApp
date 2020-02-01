package it.uniba.di.sms.sitterapp.chat;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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
import androidx.appcompat.app.AppCompatActivity;
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
import it.uniba.di.sms.sitterapp.Constants.FirebaseDb;


public class ChatConversationActivity extends AppCompatActivity
        implements MessagesListAdapter.OnMessageLongClickListener<Message>,
        MessageInput.InputListener,
        MessageInput.AttachmentsListener, MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    private final String TAG = "ChatConversActivity";

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

    SharedPreferences mPrefs;

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

        mPrefs = getSharedPreferences("lastMessage",MODE_PRIVATE);

        if(CONVERSATION_UID.equals("NEWUID")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            ArrayList<String> userList = new ArrayList<>();
            userList.add(senderId);
            userList.add(receiverId);
            Map<String,Object> mapUserList = new HashMap<>();
            mapUserList.put(FirebaseDb.CHAT_USERSLIST,userList);
            db.collection(FirebaseDb.CHAT).add(mapUserList)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            CONVERSATION_UID = documentReference.getId();
                            getSenderUser(senderId);
                            getReceiverUser(receiverId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }

        messagesList = (MessagesList) findViewById(R.id.messagesList);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(final ImageView imageView, String url, Object payload) {
                //modifico il link per la foto
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                storageReference.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(ChatConversationActivity.this)
                                        .load(uri== null?R.drawable.ic_account_circle_black_56dp:uri)
                                        .into(imageView);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG,e.getMessage());
                            }
                        });
            }
        };

        initAdapter();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(CONVERSATION_NAME);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        db.collection(FirebaseDb.CHAT).document(CONVERSATION_UID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i(TAG,e.getMessage());
                } else {
                    Map<String, ?> mapMessage = (HashMap<String, ?>) documentSnapshot.get(FirebaseDb.CHAT_LASTMESSAGE);
                    if (mapMessage != null) {
                        String textMessage = (String) mapMessage.get(FirebaseDb.LASTMESSAGE_TEXT);
                        User userMessage = new User(documentSnapshot.getString(FirebaseDb.CHAT_LASTMESSAGE+"."+FirebaseDb.LASTMESSAGE_USER+"."+FirebaseDb.LASTMESSAGE_USER_ID),
                                documentSnapshot.getString(FirebaseDb.CHAT_LASTMESSAGE+"."+FirebaseDb.LASTMESSAGE_USER+"."+FirebaseDb.LASTMESSAGE_USER_NAME),
                                documentSnapshot.getString(FirebaseDb.CHAT_LASTMESSAGE+"."+FirebaseDb.LASTMESSAGE_USER+"."+FirebaseDb.LASTMESSAGE_USER_AVATAR),
                                documentSnapshot.getBoolean(FirebaseDb.CHAT_LASTMESSAGE+"."+FirebaseDb.LASTMESSAGE_USER+"."+FirebaseDb.LASTMESSAGE_USER_ONLINE));
                        String idMessage = (String) mapMessage.get(FirebaseDb.LASTMESSAGE_ID);
                        Date dateMessage = ((Timestamp) mapMessage.get(FirebaseDb.LASTMESSAGE_TIMESTAMP)).toDate();
                        Message message = new Message(idMessage, userMessage, textMessage, dateMessage);
                        if(lastMessage == null)lastMessage=message;
                        //verifica che il messaggio successivo sia diverso da quello precedente
                        // e che non faccia il comando all'apertura dell'app (perché viene caricata tutta la lista dei messaggi)
                        if (!message.equals(lastMessage) && TOTAL_MESSAGES_COUNT>0) {
                            lastMessage = message;
                            messagesAdapter.addToStart(lastMessage, true);
                        }
                    }
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
    protected void onPause() {
        super.onPause();
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

            messagesAdapter.addToEnd(messageArrayList.subList(totalItemsCount,totalItemsCount+MESSAGE_LIMIT),false);
        }else if(totalItemsCount < TOTAL_MESSAGES_COUNT){
            messagesAdapter.addToEnd(messageArrayList.subList(totalItemsCount,TOTAL_MESSAGES_COUNT),false);
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
        db.collection(FirebaseDb.CHAT)
                .document(CONVERSATION_UID)
                .collection(FirebaseDb.CHAT_MESSAGES)
                .orderBy(FirebaseDb.CHAT_MESSAGES_TIMESTAMP, Query.Direction.DESCENDING)
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
                                    String textMessage = (String) SnapshotMessageList.get(FirebaseDb.CHAT_MESSAGES_TEXT);
                                    User userMessage;
                                    if(SnapshotMessageList.getString(FirebaseDb.CHAT_MESSAGES_USER).equals(senderId)){
                                        userMessage = senderUser;
                                    }else{
                                        userMessage = receiverUser;
                                    }
                                    String idMessage = String.valueOf(i);
                                    Date dateMessage = ((Timestamp) SnapshotMessageList.get(FirebaseDb.CHAT_MESSAGES_TIMESTAMP)).toDate();
                                    Message message = new Message(idMessage,userMessage,textMessage,dateMessage);

                                    messageArrayList.add(message);
                                }
                                if(TOTAL_MESSAGES_COUNT<MESSAGE_LIMIT){
                                    messagesAdapter.addToEnd(messageArrayList.subList(0,TOTAL_MESSAGES_COUNT),false);

                                }else{
                                    messagesAdapter.addToEnd(messageArrayList.subList(0,MESSAGE_LIMIT),false);

                                }
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

        TOTAL_MESSAGES_COUNT++;

        Map<String,Object> message = new HashMap<>();
        message.put(FirebaseDb.CHAT_MESSAGES_TEXT,input.toString());
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date);
        message.put(FirebaseDb.CHAT_MESSAGES_TIMESTAMP,timestamp);
        message.put(FirebaseDb.CHAT_MESSAGES_USER,senderId);

        Map<String,Object> lastMsg = new HashMap<>();
        lastMsg.put(FirebaseDb.LASTMESSAGE_TEXT,input.toString());
        lastMsg.put(FirebaseDb.LASTMESSAGE_ID,String.valueOf(TOTAL_MESSAGES_COUNT));
        lastMsg.put(FirebaseDb.LASTMESSAGE_TIMESTAMP,timestamp);
        Map<String,Object> user = new HashMap<>();
        user.put(FirebaseDb.LASTMESSAGE_USER_ID,senderUser.getId());
        user.put(FirebaseDb.LASTMESSAGE_USER_NAME,senderUser.getName());
        user.put(FirebaseDb.LASTMESSAGE_USER_AVATAR,senderUser.getAvatar());
        user.put(FirebaseDb.LASTMESSAGE_USER_ONLINE,senderUser.isOnline());
        lastMsg.put(FirebaseDb.LASTMESSAGE_USER,user);

        Map<String,Object> mapLastMessage = new HashMap<>();
        mapLastMessage.put(FirebaseDb.CHAT_LASTMESSAGE,lastMsg);

        String textMessage = (String) input.toString();
        User userMessage = new User(senderUser.getId(),
                senderUser.getName(),
                senderUser.getAvatar(),
                senderUser.isOnline());
        String idMessage = String.valueOf(TOTAL_MESSAGES_COUNT);
        Date dateMessage = timestamp.toDate();
        Message chatMessage = new Message(idMessage, userMessage, textMessage, dateMessage);
        lastMessage = chatMessage;
        messagesAdapter.addToStart(lastMessage, true);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(FirebaseDb.CHAT).document(CONVERSATION_UID).collection(FirebaseDb.CHAT_MESSAGES).add(message);
        db.collection(FirebaseDb.CHAT).document(CONVERSATION_UID).update(mapLastMessage);
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

    private void getReceiverUser(final String userID){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FirebaseDb.USERS).document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){

                }else{
                    User user = new User(documentSnapshot.getId(),
                            documentSnapshot.getString(FirebaseDb.USER_NOME_COMPLETO),
                            documentSnapshot.getString(FirebaseDb.USER_AVATAR),
                            documentSnapshot.getBoolean(FirebaseDb.USER_ONLINE));
                    if(!user.equals(receiverUser)){
                        receiverUser = user;
                        for(int i=0;i<messagesAdapter.getItemCount();++i){
                            messagesAdapter.notifyItemChanged(i,receiverUser);
                        }
                        messagesAdapter.notifyDataSetChanged();
                    }
                    DocumentReference docRef = db.collection(FirebaseDb.CHAT).document(CONVERSATION_UID);
                    docRef.update(FirebaseDb.CHAT_USERS+"."+receiverId,receiverUser);
                }
            }
        });
    }
    private void getSenderUser(String userID){
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(FirebaseDb.USERS).document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){

                }else{
                    User user = new User(documentSnapshot.getId(),
                            documentSnapshot.getString(FirebaseDb.USER_NOME_COMPLETO),
                                    documentSnapshot.getString(FirebaseDb.USER_AVATAR),
                                    documentSnapshot.getBoolean(FirebaseDb.USER_ONLINE));
                    if(!user.equals(senderUser)){
                        senderUser = user;
                        for(int i=0;i<messagesAdapter.getItemCount();++i){
                            messagesAdapter.notifyItemChanged(i,senderUser);
                        }
                        messagesAdapter.notifyDataSetChanged();
                    }
                    DocumentReference docRef = db.collection(FirebaseDb.CHAT).document(CONVERSATION_UID);
                    docRef.update(FirebaseDb.CHAT_USERS+"."+senderId,senderUser);
                }
            }
        });
    }
}
