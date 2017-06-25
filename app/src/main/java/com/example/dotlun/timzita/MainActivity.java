package com.example.dotlun.timzita;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationMenu;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import me.relex.circleindicator.CircleIndicator;
import models.TimzitaModel;

public class MainActivity extends ActionBarActivity {
    ViewPager viewPager;
    boolean isLoading = false;
    private final String URL_TO_HIT = "http://timzita.com/api/programs/list";
    private TextView tvData;
    private ListView lvTimzitas;
    private ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //actionbar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.ic_search_black_24dp);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        //Slide
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        ViewPageAdapter viewPagerAdapter = new ViewPageAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimeTask(),2000,4000);
        //Json
        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait..."); // showing a dialog for loading the data
        // Create default options which will be used for every
        //  displayImage(...) call if no options will be passed to this method
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application startlv
        lvTimzitas = (ListView)findViewById(R.id.lvTimzitas);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        //bottom Navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.action_add:
                        Toast.makeText(MainActivity.this, "Action Add Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_add2:
                        Toast.makeText(MainActivity.this, "Action Add1 Clicked", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_add3:
                        Toast.makeText(MainActivity.this, "Action Add2 Clicked", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

        // To start fetching the data when app start, uncomment below line to start the async task.
        new JSONTask().execute(URL_TO_HIT);
    }

    public class JSONTask extends AsyncTask<String,String, List<TimzitaModel> > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<TimzitaModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("programs");

                List<TimzitaModel> timzitaModelList = new ArrayList<>();

                Gson gson = new Gson();
                for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);
                        /**
                         * below single line of code from Gson saves you from writing the json parsing yourself
                         */
                    TimzitaModel timzitaModel = gson.fromJson(finalObject.toString(), TimzitaModel.class); // a single line json parsing using Gson
//
                    timzitaModelList.add(timzitaModel);
                }
                return timzitaModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(final List<TimzitaModel> result) {
            dialog.dismiss();
            if (result != null) {
                TimzitaAdapter adapter = new TimzitaAdapter(getApplicationContext(), R.layout.row, result);
                lvTimzitas.setAdapter(adapter);
                lvTimzitas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TimzitaModel movieModel = result.get(position); // getting the model
                        Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                        intent.putExtra("timzitaModel", new Gson().toJson(movieModel)); // converting model json into string type and sending it via intent
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }

        public class TimzitaAdapter extends ArrayAdapter {

            private List<TimzitaModel> timzitaModelList;
            private int resource;
            private LayoutInflater inflater;

            public TimzitaAdapter(Context context, int resource, List<TimzitaModel> objects) {
                super(context, resource, objects);
                timzitaModelList = objects;
                this.resource = resource;
                inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                Picasso.with(context);
        }
            @Override
            public View getView(int position,  View convertView,  ViewGroup parent) {
                ViewHolder holder = null;

                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = inflater.inflate(resource, null);
                    holder.ivMovieIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
                    //  holder.tvId = (TextView) convertView.findViewById(R.id.tvId);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
                    //  holder.tvSlug = (TextView) convertView.findViewById(R.id.tvSlug);

                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                //  final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

                // Then later, when you want to display image
                //  final ViewHolder finalHolder = holder;
                Picasso.with(getContext()).load("http://timzita.com/"+ timzitaModelList.get(position).getImage()).into(holder.ivMovieIcon);

              /* ImageLoader.getInstance().displayImage(timzitaModelList.get(position).getImage(), holder.ivMovieIcon, new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                        finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                        finalHolder.ivMovieIcon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        progressBar.setVisibility(View.GONE);
                        finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                    }
                });*/

                // holder.tvId.setText(timzitaModelList.get(position).getId());
                holder.tvTitle.setText(timzitaModelList.get(position).getTitle());
                //  holder.tvSlug.setText(timzitaModelList.get(position).getSlug());

                StringBuffer stringBuffer = new StringBuffer();
                return convertView;
            }

            class ViewHolder {
                private ImageView ivMovieIcon;
                //  private TextView tvId;
                private TextView tvTitle;
                //  private TextView tvSlug;

            }
        }
    }
    public class MyTimeTask extends TimerTask{

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (viewPager.getCurrentItem() == 0){
                        viewPager.setCurrentItem(1);
                    }else if (viewPager.getCurrentItem() == 1){
                        viewPager.setCurrentItem(2);
                    }else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.activity_main_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_id:
                Toast.makeText(getApplicationContext(), "Setting options slected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.out_id:
                Toast.makeText(getApplicationContext(), "Log out options slected", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

