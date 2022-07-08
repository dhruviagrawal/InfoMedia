package com.example.learningmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.learningmedia.Adapter.CommentAdapter;
import com.example.learningmedia.Util.Comment;
import com.example.learningmedia.Util.ModelUser;
import com.example.learningmedia.Util.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;
    private EditText addComment;
    private ImageView imageProfile;
    private TextView post;
    Context mContext;
    private String postId;
    private String authordId;

    FirebaseUser firebaseUser;
    //@SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        authordId = intent.getStringExtra("authordId");
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this,commentList,postId);

        recyclerView.setAdapter(commentAdapter);
        addComment = findViewById(R.id.add_comment);
        post = findViewById(R.id.post);
        imageProfile = findViewById(R.id.image_profile);


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        
        UserImage();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(addComment.getText().toString())){
                    Toast.makeText(CommentActivity.this,"No Comments Added!",Toast.LENGTH_SHORT).show();
                }
                else{
                    CommentAdded();
                }
            }
        });
        getComment();
    }

    private void getComment() {

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Comment comment = dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }

                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void CommentAdded() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("comment",addComment.getText().toString());
        map.put("publisher",firebaseUser.getUid());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
        String id = databaseReference.push().getKey();
        map.put("id",id);
        addComment.setText("");
      databaseReference.child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CommentActivity.this,"Comment Added!",Toast.LENGTH_SHORT).show();
                }
                else{
                   Toast.makeText(CommentActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UserImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").
                child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
              //  if (user.getImageurl().equals("gs://learning-media-4cd52.appspot.com/user-png-icon-download-icons-logos-emojis-users-2240.png")) {
                    imageProfile.setImageResource(R.mipmap.ic_launcher);
              //  } else {
                //    Glide.with(mContext).load(user.getImageurl()).into(imageProfile);
                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}