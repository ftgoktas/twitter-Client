package com.codepath.apps.restclienttemplate;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    public static final String TAG = "ComposeActivity";
    public static final int MAX_LENGTH = 140;
    EditText etCompose;
    Button btnTweet;
    TextView textCount;

    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet= findViewById(R.id.btnTweet);
        btnTweet.setEnabled(false);

        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Fires right after the text has changed
                if(s.length() > MAX_LENGTH || s.length() < 1){
                    Toast.makeText(ComposeActivity.this, "We can't really chirp that!", Toast.LENGTH_SHORT).show();
                    btnTweet.setEnabled(false);
                }
                else btnTweet.setEnabled(true);
            }
        });

        setTitle("New Tweet");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher);

        //button listen to publish text
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Your tweet cannot be empty!", Toast.LENGTH_LONG).show();
                    return;
                }
                if(tweetContent.length() > MAX_LENGTH){
                    Toast.makeText(ComposeActivity.this, "Sorry, your tweet is too long!", Toast.LENGTH_LONG).show();
                    return;
                }
                Toast.makeText(ComposeActivity.this, tweetContent, Toast.LENGTH_LONG).show();
                //make an API call to send a tweet
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess tweet published");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "tweet sent" + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent); //set results
                            finish(); //closes the activity
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "onFailure to publish tweet", throwable);
                    }
                },  tweetContent);
            }

        });

    }
}