package com.example.jgwhit14.cs450finalproject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddFriend extends AppCompatActivity {
    private Button Save;
    private Button Cancel;
    private FirebaseDatabase database;
    private String loggedInUser;
    private SharedPreferences pref;
    private String friend;
    EditText FriendUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        Save = findViewById(R.id.button3);
        Cancel = findViewById(R.id.button);


        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              save();
            }
        });

        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                setResult(0);
            }
        });
        FriendUsername = findViewById(R.id.editTextNickname);

        //when you click done
        FriendUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_NULL
                        || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    save();

                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    private void save() {

        FriendUsername = findViewById(R.id.editTextNickname);
        friend = FriendUsername.getText().toString().trim();


        database  = FirebaseDatabase.getInstance();
        pref = getApplicationContext().getSharedPreferences("Profile",0);
        loggedInUser = pref.getString("Username","none");


        //retrieve locations from Firebase and create MyLocationsObject objects
        final DatabaseReference loginRef = database.getReference("users");
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                for (DataSnapshot user:users){
                    String usernameP = user.getKey();
                    //loggedInUser
                    if(usernameP.equals(friend)){ //check if this user exists

                        addFriend(usernameP); //friend exists so add
                        found = true;
                        break;
                    }

                }
                if (found){
                    Toast.makeText(getApplicationContext(),"Friend Added!",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(),"User does not exist",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void addFriend(String usernameP) {
        final DatabaseReference AddFriend = FirebaseDatabase.getInstance().getReference();

        database  = FirebaseDatabase.getInstance();
        pref = getApplicationContext().getSharedPreferences("Profile",0);
        loggedInUser = pref.getString("Username","none");


        //retrieve locations from Firebase and create MyLocationsObject objects
        final DatabaseReference loginRef = database.getReference("users");
        loginRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                Iterable<DataSnapshot> users = dataSnapshot.getChildren();
                for (DataSnapshot user:users){
                    String usernameP = user.getKey();
                    //loggedInUser
                    if(usernameP.equals(loggedInUser)){ // Check if this user exists

                        if (friend.equals(loggedInUser)){
                            Toast.makeText(AddFriend.this, "Can't friend yourself", Toast.LENGTH_SHORT).show();

                            setResult(Activity.RESULT_OK);
                            finish();
                            return;
                        }

                        Iterable<DataSnapshot> friendRequests = user.child("friends").getChildren();
                        for (DataSnapshot request:friendRequests){
                            if (request.getValue().toString().split("mySPLIT")[0].equals(friend)){
                                // Already added this person
                                Toast.makeText(AddFriend.this, "Already added this user", Toast.LENGTH_SHORT).show();

                                setResult(Activity.RESULT_OK);
                                finish();
                                return;
                            }
                        }

                        // User exists and not already added, add to logged in user's friend list
                        User loginUser = user.getValue(User.class);
                        ArrayList<String> userFriends = loginUser.friends;
                        ArrayList<String> userFriendRequests = loginUser.friendRequests;
                        System.out.println("Friends: " + userFriends);

                        if (userFriends != null) {
                            AddFriend.child("users").child(loggedInUser).child("friends").child(String.valueOf(userFriends.size())).setValue(friend + "mySPLITfalse");
                            if (userFriendRequests != null){
                                AddFriend.child("users").child(friend).child("friendRequests").child(String.valueOf(userFriendRequests.size())).setValue(loggedInUser + "mySPLITtrue");
                            } else {
                                AddFriend.child("users").child(friend).child("friendRequests").child(String.valueOf(0)).setValue(loggedInUser + "mySPLITtrue");
                            }
                            setResult(Activity.RESULT_OK);
                            finish();

                        }else{
                            AddFriend.child("users").child(loggedInUser).child("friends").child(String.valueOf(0)).setValue(friend + "mySPLITfalse");
                            if (userFriendRequests != null){
                                AddFriend.child("users").child(friend).child("friendRequests").child(String.valueOf(userFriendRequests.size())).setValue(loggedInUser + "mySPLITtrue");
                            } else {
                                AddFriend.child("users").child(friend).child("friendRequests").child(String.valueOf(0)).setValue(loggedInUser + "mySPLITtrue");
                            }
                            setResult(Activity.RESULT_OK);
                            finish();

                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
