package com.example.learningmedia.Home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.learningmedia.Adapter.PhotoAdapter;
import com.example.learningmedia.Adapter.PostAdapter;
import com.example.learningmedia.EditProfileActivity;
import com.example.learningmedia.FollowersActivity;
import com.example.learningmedia.LogoutActivity;
import com.example.learningmedia.R;
import com.example.learningmedia.Util.ModelUser;
import com.example.learningmedia.Util.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerViewBookmark;
    private PhotoAdapter postAdapterBookmark;
    private List<Post> BookmarkPosts;

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> PhotoList;
    private ImageView imageProfile;
    private ImageView options;
    private TextView posts;
    private  TextView followers;
    private  TextView following;
    private TextView bio;
    private TextView username;
    private TextView fullname;

    private ImageView pictures;
    private  ImageView bookmarkPost;

    private Button editProfile;

    String profileId;

    FirebaseUser firebaseUser;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String data = getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileId","none");

        if(data.equals("none")){
            profileId = firebaseUser.getUid();
        }
        else{
            profileId = data;
            getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit().clear().apply();
        }
      //  profileId = firebaseUser.getUid();
        imageProfile = view.findViewById(R.id.image_profile);
        options = view.findViewById(R.id.options);
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        pictures = view.findViewById(R.id.pictures);
        bookmarkPost = view.findViewById(R.id.bookmarkPost);
        fullname = view.findViewById(R.id.fullname);
        editProfile = view.findViewById(R.id.edit_profile);

        recyclerView = view.findViewById(R.id.recycler_view_posts);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        PhotoList = new ArrayList<>();
        photoAdapter = new PhotoAdapter(getContext(),PhotoList);
        recyclerView.setAdapter(photoAdapter);

        recyclerViewBookmark = view.findViewById(R.id.recycler_view_bookmark);
        recyclerViewBookmark.setHasFixedSize(true);
        recyclerViewBookmark.setLayoutManager(new GridLayoutManager(getContext(),3));
        BookmarkPosts = new ArrayList<>();
        postAdapterBookmark = new PhotoAdapter(getContext(),BookmarkPosts);
        recyclerViewBookmark.setAdapter(postAdapterBookmark);

        getUserInfo();
        getFollowersAndFollowingCount();
        postCount();
        myPosts();
        getBookmarkPosts();

        if(profileId.equals(firebaseUser.getUid())){
            editProfile.setText("Edit Profile");
        }
        else{
            checkFollowingStatus();
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = editProfile.getText().toString();

                if(buttonText.equals("Edit Profile")){
                startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
                else{
                    if(buttonText.equals("follow")){
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).
                                child("following").child(profileId).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).
                                child("followers").child(firebaseUser.getUid()).setValue(true);
                    }
                    else{
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).
                                child("following").child(profileId).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).
                                child("followers").child(firebaseUser.getUid()).removeValue();

                    }
                }
            }
        });
        recyclerView.setVisibility(View.VISIBLE);
        recyclerViewBookmark.setVisibility(View.GONE);
        pictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerViewBookmark.setVisibility(View.GONE);
            }
        });

        bookmarkPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerViewBookmark.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","followers");
                startActivity(intent);
            }
        });

        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","following");
                startActivity(intent);
            }
        });

        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), LogoutActivity.class));
            }
        });
        return view;
    }

    private void getBookmarkPosts() {
       final List<String> bookmarkIds = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("Bookmark").child(firebaseUser.getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                            bookmarkIds.add(dataSnapshot.getKey());
                        }
                        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot1) {
                                BookmarkPosts.clear();

                                for(DataSnapshot dataSnapshot1: snapshot1.getChildren()){
                                    Post post = dataSnapshot1.getValue(Post.class);

                                    for(String id:bookmarkIds){
                                        if(post.getPostid().equals(id)){
                                            BookmarkPosts.add(post);
                                        }
                                    }

                                }
                                postAdapterBookmark.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void myPosts() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PhotoList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId)){
                        PhotoList.add(post);
                    }
                }
                Collections.reverse(PhotoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).
                child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(profileId).exists()){
                    editProfile.setText("following");
                }
                else{
                    editProfile.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postCount() {

        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cnt=0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId))
                        cnt++;
                }
                posts.setText(String.valueOf(cnt));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowersAndFollowingCount() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        databaseReference.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText("" + snapshot.getChildrenCount());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText("" + snapshot.getChildrenCount());
               //System.out.print(snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserInfo() {

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUser user = snapshot.getValue(ModelUser.class);
                Glide.with(ProfileFragment.this).load(user.getImageurl()).into(imageProfile);
                username.setText(user.getName());
                fullname.setText(user.getName());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}