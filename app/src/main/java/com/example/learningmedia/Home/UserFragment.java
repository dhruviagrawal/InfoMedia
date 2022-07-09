package com.example.learningmedia.Home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.learningmedia.Adapter.UserAdapter;
import com.example.learningmedia.R;
import com.example.learningmedia.Util.ModelUser;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserFragment extends Fragment {
    public static final String TAG="UserFragmenr";
    RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<ModelUser> modelUsers;
    EditText search_bar;
    String prevuser="";
    private static final int ActivityNum =2;

    public UserFragment(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
       // recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        search_bar = view.findViewById(R.id.search_bar);
        modelUsers = new ArrayList<>();
        userAdapter = new UserAdapter(getContext(),modelUsers,true);
        recyclerView.setAdapter(userAdapter);
        firstUsers();
        //readUsers();
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater){

    }

    private void searchUsers(String s){
      /*  Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("fullName")
                .startAt(s).endAt(s+"\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                modelUsers.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    ModelUser user=  snapshot.getValue(ModelUser.class);
                    modelUsers.add(user);

                }
                userAdapter = new UserAdapter(getContext(),modelUsers);
                userAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //first all the users present in recycler view initially is cleared
                modelUsers.clear();
                //if the keyword is present in any of the email or username i.e full name it will be added to modeluser
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    HashMap<Object,String>hashMap = (HashMap<Object, String>)dataSnapshot.getValue();
                    ModelUser user = new ModelUser(hashMap.get("Name"),hashMap.get("Id"),hashMap.get("email"),
                            hashMap.get("phone"),hashMap.get("imageurl"),hashMap.get("bio"));
                    if(hashMap.get("Id").equals(firebaseUser.getUid())){
                        if(user.getName().toLowerCase().contains(s)||user.getEmail().toLowerCase().contains(s)){
                            modelUsers.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getActivity(),modelUsers,true);
                userAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private  void firstUsers(){
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.limitToFirst(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot db:snapshot.getChildren()){
                 prevuser = db.getKey();
                    HashMap<Object,String>hashMap = (HashMap<Object, String>)db.getValue();
                    ModelUser user = new ModelUser(hashMap.get("Name"),hashMap.get("Id"),hashMap.get("email"),
                            hashMap.get("phone"),hashMap.get("imageurl"),hashMap.get("bio"));
                    if(!hashMap.get("Id").equals(firebaseUser.getUid())){
                        modelUsers.add(user);
                    }
                }
                userAdapter = new UserAdapter(getActivity(),modelUsers,true);
                recyclerView.setAdapter(userAdapter);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    /*
    private void readUsers(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(search_bar.getText().toString().equals(" ")){
                    modelUsers.clear();
                    for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                        ModelUser user= snapshot.getValue(ModelUser.class);
                        modelUsers.add(user);

                    }

                    userAdapter = new UserAdapter(getContext(),modelUsers);
                    userAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(userAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

     */
}

