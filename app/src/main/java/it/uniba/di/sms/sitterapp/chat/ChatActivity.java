package it.uniba.di.sms.sitterapp.chat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.chat.dialogs.CustomDialogViewHolder;
import it.uniba.di.sms.sitterapp.oggetti.Dialog;
import it.uniba.di.sms.sitterapp.oggetti.Message;
import it.uniba.di.sms.sitterapp.oggetti.User;
import it.uniba.di.sms.sitterapp.principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.Constants.FirebaseDb;
import tr.xip.errorview.ErrorView;

public class ChatActivity extends DrawerActivity implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog>{

    private static final String SELECTED = "selected";
    private static final String TAG = "ChatActivity";
    BottomNavigationView bottomNavigationView;

    private DialogsList dialogsList;
    protected ImageLoader imageLoader;
    protected DialogsListAdapter<Dialog> dialogsAdapter;

    User user_;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //BottomNavigationView
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.getMenu().findItem(getIntent().getIntExtra(SELECTED,R.id.nav_chat)).setChecked(true);
        dialogsList = (DialogsList) findViewById(R.id.dialogsList);

        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(final ImageView imageView, String url, Object payload) {

                /*Glide.with(ChatActivity.this)
                        .load("https://www.studiofrancesconi.com/wp-content/uploads/2019/03/placeholder-profile-sq.jpg")
                        .into(imageView);*/
                //modifico il link per la foto
                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
                storageReference.getDownloadUrl()
                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(ChatActivity.this)
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
    }

    public void onDialogClick(Dialog dialog) {
        //ChatConversationActivity.open(this);
        Intent chatConversationIntent = new Intent(this, ChatConversationActivity.class);
        chatConversationIntent.putExtra("conversationName",dialog.getDialogName());
        chatConversationIntent.putExtra("senderId",sessionManager.getSessionUid());
        chatConversationIntent.putExtra("receiverId",dialog.getUsers().get(0).getId());
        chatConversationIntent.putExtra("conversationUID",dialog.getId());
        startActivity(chatConversationIntent);
    }

    private void initAdapter() {
        dialogsAdapter = new DialogsListAdapter<>(
                R.layout.item_custom_dialog_view_holder,
                CustomDialogViewHolder.class,
                imageLoader);

        dialogsAdapter.setItems(new ArrayList<Dialog>());
        getDialogs(sessionManager.getSessionUid());

        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(dialogsAdapter);
    }

    public void onDialogLongClick(Dialog dialog) {
        Toast.makeText(this,dialog.getDialogName(),Toast.LENGTH_SHORT);
    }

    private void getDialogs(final String user) {
        db.collection(FirebaseDb.CHAT)
                .whereArrayContains(FirebaseDb.CHAT_USERSLIST,sessionManager.getSessionUid())
                .orderBy(FirebaseDb.CHAT_LASTMESSAGE+"."+FirebaseDb.LASTMESSAGE_TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        ErrorView errorView = (ErrorView) findViewById(R.id.errorView);
                        if(e!=null){
                            Log.d(TAG,"Error:"+e.getMessage());
                        }else {
                            errorView.setVisibility(View.INVISIBLE);
                            //ripulisco la lista dei dialogs
                            dialogsAdapter.clear();
                            List<Dialog> dialogList = new ArrayList<>();
                            if (!querySnapshot.isEmpty()) {

                                //aggiungo ogni conversazione alla lista
                                for (DocumentSnapshot documentSnapshot : querySnapshot.getDocuments()) {
                                    Map<String,Map<String, Object>> mapUserList = (HashMap<String,Map<String, Object>>) documentSnapshot.get("Users");
                                    //rimuovo dell'utente che non sono necessarie
                                    mapUserList.remove(sessionManager.getSessionUid());
                                    Set<String> keySet = mapUserList.keySet();
                                    //avvaloro la lista degli utenti (nel nostro caso c'è solo un utente
                                    ArrayList<User> userList = new ArrayList<>();
                                    for(String key: keySet){
                                        userList.add(new User(key,
                                                (String) mapUserList.get(key).get(FirebaseDb.CHAT_USER_NAME),
                                                (String) mapUserList.get(key).get(FirebaseDb.CHAT_USER_AVATAR),
                                                (Boolean) mapUserList.get(key).get(FirebaseDb.CHAT_USER_ONLINE)));
                                    }

                                    //avvaloro il campo ultimo messaggio
                                    Map<String, Object> mapMessage = (HashMap<String, Object>) documentSnapshot.get(FirebaseDb.CHAT_LASTMESSAGE);
                                    String textMessage = (String) mapMessage.get(FirebaseDb.LASTMESSAGE_TEXT);
                                    User userMessage = new User(documentSnapshot.getString(FirebaseDb.CHAT_LASTMESSAGE+"."+FirebaseDb.LASTMESSAGE_USER+"."+FirebaseDb.LASTMESSAGE_USER_ID),
                                            documentSnapshot.getString(FirebaseDb.CHAT_LASTMESSAGE+"."+FirebaseDb.LASTMESSAGE_USER+"."+FirebaseDb.LASTMESSAGE_USER_NAME),
                                            documentSnapshot.getString(FirebaseDb.CHAT_LASTMESSAGE+"."+FirebaseDb.LASTMESSAGE_USER+"."+FirebaseDb.LASTMESSAGE_USER_AVATAR),
                                            documentSnapshot.getBoolean(FirebaseDb.CHAT_LASTMESSAGE+"."+FirebaseDb.LASTMESSAGE_USER+"."+FirebaseDb.LASTMESSAGE_USER_ONLINE));
                                    String idMessage = (String) mapMessage.get(FirebaseDb.LASTMESSAGE_ID).toString();
                                    Date dateMessage = ((Timestamp) mapMessage.get(FirebaseDb.LASTMESSAGE_TIMESTAMP)).toDate();
                                    Message lastMessage = new Message(idMessage, userMessage, textMessage, dateMessage);


                                    //creo il dialog per la conversazione
                                    dialogList.add(new Dialog(documentSnapshot.getId(), userList.get(0).getName(), userList.get(0).getAvatar(), userList, lastMessage, 0));
                                }
                                dialogsAdapter.addItems(dialogList);
                            }
                            else{
                                errorView.setTitle(R.string.niente_messaggi);
                                errorView.setVisibility(View.VISIBLE);

                            }
                        }

                    }
                });
    }

}

