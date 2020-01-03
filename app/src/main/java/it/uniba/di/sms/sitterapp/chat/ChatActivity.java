package it.uniba.di.sms.sitterapp.chat;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.chat.dialogs.CustomDialogViewHolder;
import it.uniba.di.sms.sitterapp.chat.fixtures.DialogsFixtures;
import it.uniba.di.sms.sitterapp.oggetti.Dialog;
import it.uniba.di.sms.sitterapp.oggetti.Message;
import it.uniba.di.sms.sitterapp.oggetti.User;
import it.uniba.di.sms.sitterapp.principale.DrawerActivity;
import it.uniba.di.sms.sitterapp.utils.AppUtils;

public class ChatActivity extends DrawerActivity implements DialogsListAdapter.OnDialogClickListener<Dialog>,
        DialogsListAdapter.OnDialogLongClickListener<Dialog>{

    private static final String SELECTED = "selected";
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
        imageLoader = new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, String url, Object payload) {
                Picasso.with(ChatActivity.this).load(url).into(imageView);
            }
        };
        dialogsList = (DialogsList) findViewById(R.id.dialogsList);
        initAdapter();
    }

    public void onDialogClick(Dialog dialog) {
        //ChatConversationActivity.open(this);
        Intent chatConversationIntent = new Intent(this, ChatConversationActivity.class);
        chatConversationIntent.putExtra("conversationName",dialog.getDialogName());
        chatConversationIntent.putExtra("senderId",sessionManager.getSessionUsername());
        chatConversationIntent.putExtra("conversationUID",dialog.getId());
        startActivity(chatConversationIntent);
    }

    private void initAdapter() {
        dialogsAdapter = new DialogsListAdapter<>(
                R.layout.item_custom_dialog_view_holder,
                CustomDialogViewHolder.class,
                imageLoader);

        dialogsAdapter.setItems(new ArrayList<Dialog>());
        getDialogs(sessionManager.getSessionUsername());

        dialogsAdapter.setOnDialogClickListener(this);
        dialogsAdapter.setOnDialogLongClickListener(this);

        dialogsList.setAdapter(dialogsAdapter);
    }

    public void onDialogLongClick(Dialog dialog) {
        AppUtils.showToast(
                this,
                dialog.getDialogName(),
                false);
    }

    private void getDialogs(final String user) {
        db.collection("chat")
                .whereArrayContains("UsersList",sessionManager.getSessionUsername())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        //ripulisco la lista dei dialogs
                        dialogsAdapter.clear();
                        List<Dialog> dialogList = new ArrayList<>();
                        //aggiungo ogni conversazione alla lista
                        for(DocumentSnapshot documentSnapshot:querySnapshot.getDocuments()){
                            List<Map<String,Object>> mapUserList = (ArrayList<Map<String,Object>>) documentSnapshot.get("Users");
                            //rimuovo dell'utente che non sono necessarie
                            for(int i=0;i<mapUserList.size();++i){
                                Map<String,Object> user = mapUserList.get(i);
                                if(user.get("id").equals(sessionManager.getSessionUsername())){
                                    mapUserList.remove(i);
                                }
                            }

                            //avvaloro la lista degli utenti (nel nostro caso c'è solo un utente
                            ArrayList<User> userList = new ArrayList<>();
                            userList.add(new User((String) mapUserList.get(0).get("id"),(String) mapUserList.get(0).get("name"),(String) mapUserList.get(0).get("avatar"),(Boolean) mapUserList.get(0).get("online")));

                            //avvaloro il campo ultimo messaggio
                            Map<String,?> mapMessage = (HashMap<String,?>) documentSnapshot.get("lastMessage");
                            String textMessage = (String) mapMessage.get("text");
                            User userMessage = new User(documentSnapshot.getString("lastMessage.user.id"),
                                    documentSnapshot.getString("lastMessage.user.name"),
                                    documentSnapshot.getString("lastMessage.user.avatar"),
                                    documentSnapshot.getBoolean("lastMessage.user.online"));
                            String idMessage = (String) mapMessage.get("id").toString();
                            Date dateMessage = ((Timestamp) mapMessage.get("timestamp")).toDate();
                            Message lastMessage = new Message(idMessage,userMessage,textMessage,dateMessage);

                            //creo il dialog per la conversazione
                            dialogList.add(new Dialog(documentSnapshot.getId(),userList.get(0).getName(),userList.get(0).getAvatar(),userList,lastMessage,0));
                        }
                        dialogsAdapter.addItems(dialogList);

                    }
                });
    }

}

