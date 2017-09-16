package com.arx_era.project;

/**
 * Created by Tronix99 on 10-07-2017.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Fragment {

    private Button btnLogin;
    private EditText inputUserName;
    private EditText inputCategory;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_login, container, false);

        inputUserName = (EditText) rootView.findViewById(R.id.username);
        inputCategory = (EditText) rootView.findViewById(R.id.category);
        inputPassword = (EditText) rootView.findViewById(R.id.password);
        btnLogin = (Button) rootView.findViewById(R.id.btnLogin);

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getActivity().getApplicationContext());

        // Session manager
        session = new SessionManager(getActivity().getApplicationContext());

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String username = inputUserName.getText().toString().trim();
                String category = inputCategory.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                TelephonyManager manager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                String imeiid = manager.getDeviceId();
                // Check for empty data in the form
                if (!username.isEmpty() && !password.isEmpty() && !category.isEmpty()) {
                    // login user
                    checkLogin(username, imeiid, password, category);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        return rootView;
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String username, final String imeiid, final String password ,final String category) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String username = user.getString("username");
                        String category = user.getString("category");
                        String imeiid = user.getString("imeiid");
                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(username, category, imeiid, uid, created_at);

                        // Launch main activity
                        Intent intent = new Intent(getActivity(),
                                HomeActivity.class);
                        startActivity(intent);
                        getActivity().finish();

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("imeiid", imeiid);
                params.put("password", password);
                params.put("category", category);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}