package com.example.learningmedia.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learningmedia.Home.ProfileFragment;
import com.example.learningmedia.Home.StartActivity;
import com.example.learningmedia.R;
import com.example.learningmedia.Util.ModelUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<ModelUser> mUser;
    public static final String SHARED_PREFS = "PREFS";
    private FirebaseUser firebaseUser;
    private boolean isFragment;

    public UserAdapter(Context mContext, List<ModelUser> mUser, boolean isFragment) {
        this.mContext = mContext;
        this.mUser = mUser;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // Log.e("Test", "UserAdapter");

        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);

        // return new UserAdapter.ViewHolder(view)
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
      final ModelUser user= mUser.get(position);
      holder.button_follow.setVisibility(View.VISIBLE);
      holder.fullname.setText(user.getName());
      holder.emailid.setText(user.getName());
        Glide.with(mContext).load(user.getImageurl()).into(holder.image_profile);

        isFollowing(user.getId(),holder.button_follow);

        if(user.getId().equals(firebaseUser.getUid())){
            holder.button_follow.setVisibility(View.GONE);
        }

      /*  holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE).edit();
                editor.putString("profilefield",user.getId());
                editor.apply();

                // as soon as the application opens the UserActivity
                // is should be shown to the user
               // Intent intent = new Intent(mContext, User.class);
                //mContext.startActivity(intent);
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });
        */
        holder.button_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.button_follow.getText().toString().equals("follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).setValue(true);
                }
                else{
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFragment){
                  mContext.getSharedPreferences("PROFILE",Context.MODE_PRIVATE).edit().putString("profileId",user.getId()).apply();
                    ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().
                            replace(R.id.fragment_container,new ProfileFragment()).commit();

                }
                else{
                    Intent intent = new Intent(mContext, StartActivity.class);
                    intent.putExtra("publisherId",user.getId());
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.e("Test", "" + this.mUser.size());

        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView fullname;
        public TextView emailid;
        public CircleImageView image_profile;
        public Button button_follow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullname = itemView.findViewById(R.id.fullname);
            emailid = itemView.findViewById(R.id.emailid);
            image_profile = itemView.findViewById(R.id.image_profile);
            button_follow = itemView.findViewById(R.id.button_follow);
        }
    }
    public void isFollowing(final String userid,final Button button){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.child(userid).exists()){
                    button.setText("following");
                }
                else{
                    button.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
