package com.example.learningmedia.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.learningmedia.CommentActivity;
import com.example.learningmedia.Home.StartActivity;
import com.example.learningmedia.R;
import com.example.learningmedia.Util.Comment;
import com.example.learningmedia.Util.ModelUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
   private Context mContext;
   private List<Comment> mComments;
   String postId;
   private FirebaseUser firebaseUser;

   public CommentAdapter(Context mContext,List<Comment>mComments,String postId){
       this.mContext = mContext;
       this.mComments = mComments;
       this.postId = postId;
   }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
       final  Comment comment = mComments.get(position);
       holder.comment.setText(comment.getComment());
       FirebaseDatabase.getInstance().getReference().child("Users").
               child(comment.getPublisher()).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               ModelUser user = snapshot.getValue(ModelUser.class);
               holder.username.setText(user.getName());
               if (user.getImageurl().equals("gs://learning-media-4cd52.appspot.com/user-png-icon-download-icons-logos-emojis-users-2240.png")) {
                   holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
               } else {
                   Glide.with(mContext).load(user.getImageurl()).into(holder.imageProfile);
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
       holder.comment.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(mContext, StartActivity.class);
               intent.putExtra("publisherId",comment.getPublisher());
               mContext.startActivity(intent);
           }
       });
       holder.imageProfile.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(mContext,StartActivity.class);
               intent.putExtra("publisherId",comment.getPublisher());
               mContext.startActivity(intent);
           }
       });
       holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               if(comment.getPublisher().endsWith(firebaseUser.getUid())){
                   AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                   alertDialog.setTitle("Do you want to delete?");
                   alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           dialog.dismiss();
                       }
                   });
                   alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           FirebaseDatabase.getInstance().getReference().child("Comments").
                                   child(postId).child(comment.getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()){
                                  Toast.makeText(mContext,"Comment deleted successfully!",Toast.LENGTH_SHORT).show();
                                  dialog.dismiss();
                              }
                               }
                           });
                       }
                   });
                   alertDialog.show();
               }
               return true;
           };
       });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView imageProfile;
        public TextView username;
        public  TextView comment;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.username);
            comment = itemView.findViewById(R.id.comment);
        }
    }
}
