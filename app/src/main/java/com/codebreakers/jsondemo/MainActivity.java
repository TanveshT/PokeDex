package com.codebreakers.jsondemo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    String bufferString;
    ArrayList<Pokedex> pokedexArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        lv = findViewById(R.id.main_listview);
        pokedexArrayList = new ArrayList<>();

        new AsyncGetData().execute();

    }

    public class PokeAdapter extends BaseAdapter {

        LayoutInflater layoutInflater;

        @Override
        public int getCount() {
            return pokedexArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return pokedexArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

            if (layoutInflater != null) {
                convertView = layoutInflater.inflate(R.layout.list_item, null);
            }

            ImageView pokeimageView = convertView.findViewById(R.id.imgView);
            TextView pokeName = convertView.findViewById(R.id.txt_pokeName);
            TextView pokeId = convertView.findViewById(R.id.txt_pokeID);

            Pokedex pokedex = (Pokedex) getItem(position);

            Picasso.with(MainActivity.this)
                    .load(pokedex.getImage())
                    .into(pokeimageView);

            pokeName.setText(pokedex.getName());
            pokeId.setText(pokedex.getNum());

            return convertView;
        }
    }

    public class AsyncGetData extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        String urlParameters;
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Fetching");
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            try {
                urlParameters = "fg8hg4f2d66fg8h41grer2gh845g12fed3f6g=" + URLEncoder.encode("TheBestSecretKey", "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL(properties.server_url + "fetchjson.php");

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e.toString();
            }
            try {

                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(properties.READ_TIMEOUT);
                conn.setConnectTimeout(properties.CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);

                DataOutputStream wr1 = new DataOutputStream(
                        conn.getOutputStream());
                wr1.writeBytes(urlParameters);
                wr1.flush();
                wr1.close();

            } catch (IOException e1) {
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {

                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    return (result.toString());

                } else {

                    return ("unsuccessful");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "error";
            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (result.contains("unsuccessful") || result.contains("error")) {
                Toast.makeText(MainActivity.this, "Internet Connection error", Toast.LENGTH_SHORT).show();
            } else if (result.contains("Successful")) {
                try {

                    JSONArray pokedexdata = new JSONArray(result);

                    for (int i = 1; i < pokedexdata.length(); i++) {
                        Pokedex pokedex = new Pokedex();
                        JSONObject pokemon = pokedexdata.getJSONObject(i);
                        pokedex.setNum(pokemon.getString("num"));
                        pokedex.setName(pokemon.getString("name"));
                        pokedex.setImage(pokemon.getString("img"));

                        pokedexArrayList.add(pokedex);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                lv.setAdapter(new PokeAdapter());
            }
        }
    }
}
