package b.b.a.firebasedem.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import b.b.a.firebasedem.R;
import b.b.a.firebasedem.pojo.UserPojo;

public class LoginActivity extends AppCompatActivity {

    private EditText emailEditText, passEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("myFile", MODE_PRIVATE);
        boolean isLogin = preferences.getBoolean("isLogin", false);
        if (isLogin)
        {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }

        init();
    }

    private void init() {
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passEditText = (EditText) findViewById(R.id.passEditText);
    }

    public void login(View view)
    {
        final String enteredEmail = emailEditText.getText().toString();
        final String enteredPass = passEditText.getText().toString();

        if (enteredEmail.equals("") || enteredPass.equals(""))
        {
            Toast.makeText(this, "all fields are required", Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("user");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren())
                    {
                        UserPojo pojo = childDataSnapshot.getValue(UserPojo.class);
                        Log.d("1234", "onDataChange: "+pojo);

                        if (enteredEmail.equals(pojo.getEmail()) && enteredPass.equals(pojo.getPass()))
                        {
                            SharedPreferences sharedPreferences =
                                    getSharedPreferences("myFile", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("isLogin", true);
                            editor.putString("user_id", pojo.getUserId());
                            editor.commit();

                            Log.d("1234", "onDataChange: login succes");
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                            finish();
                        }
                    }
                    Toast.makeText(LoginActivity.this, "Email/Pass incorrect", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    public void register(View view)
    {
        startActivity(new Intent(this, RegisterActivity.class));
    }
}
