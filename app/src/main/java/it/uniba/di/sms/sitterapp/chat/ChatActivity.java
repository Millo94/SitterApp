package it.uniba.di.sms.sitterapp.chat;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import it.uniba.di.sms.sitterapp.R;
import it.uniba.di.sms.sitterapp.chat.dialogs.CustomDialogViewHolder;
import it.uniba.di.sms.sitterapp.chat.fixtures.DialogsFixtures;
import it.uniba.di.sms.sitterapp.oggetti.Dialog;
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
        ChatConversationActivity.open(this);
    }

    private void initAdapter() {
        dialogsAdapter = new DialogsListAdapter<>(
                R.layout.item_custom_dialog_view_holder,
                CustomDialogViewHolder.class,
                imageLoader);

        dialogsAdapter.setItems(DialogsFixtures.getDialogs());

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

    private void getdialogs(User user) {

        Query querySender = db.collection("chat").whereEqualTo("sender",sessionManager.getSessionUsername());
        Query queryReceiver = db.collection("chat").whereEqualTo("receiver",sessionManager.getSessionUsername());

        Task<QuerySnapshot> taskSender = querySender.get();
        Task<QuerySnapshot> taskReceiver = queryReceiver.get();

        final Task<?> combinedTask = Tasks.whenAllComplete(taskSender,taskReceiver)
                .addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
            @Override
            public void onComplete(@NonNull Task<List<Task<?>>> task) {
                if(task.isSuccessful()){
                    Set userSet = new HashSet();
                    List<Task<?>> taskList = task.getResult();
                    Iterator<Task<?>> taskIterator = taskList.iterator();
                    while(taskIterator.hasNext()){
                        Task<?> result = taskIterator.next();
                        QuerySnapshot querySnapshot = (QuerySnapshot) result.getResult();
                    }
                }else{
                    Toast.makeText(ChatActivity.this,R.string.genericError,Toast.LENGTH_LONG).show();
                }
            }

        });
    }
}

