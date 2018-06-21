package scoj.pioneer_camp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

    String newprogress = "";

    private String pass = "123455";
    // UI references.
    private AutoCompleteTextView room_numberm;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        room_numberm = findViewById(R.id.room_number);
        mPasswordView = findViewById(R.id.password);


        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPasswordView.getText().toString().equalsIgnoreCase("123455")) {
                    Intent intent = new Intent(LoginActivity.this, Main.class);
                    intent.putExtra("number", room_numberm.getText().toString());
                    intent.putExtra("pass", mPasswordView.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Please Write correct Password", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Main.class);
        setResult(RESULT_CANCELED, intent);
        finish();
    }
}
