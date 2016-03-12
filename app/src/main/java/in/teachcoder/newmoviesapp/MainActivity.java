package in.teachcoder.newmoviesapp;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView moviesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moviesListView = (ListView) findViewById(R.id.discover_movie_list);
        populateMovies();
    }

    private void populateMovies() {
        new FetchSearchResults(this, moviesListView).execute("discover");
    }

    public class FetchSearchResults extends AsyncTask<String, Void, ArrayList<Movie>> {
        Context context;
        public ArrayList<Movie> movieResults = new ArrayList<>();
        public MyListAdapter searchAdapter;
        ListView listView;

        Uri buildUri;

        public FetchSearchResults(Context c, ListView lv) {
            super();
            context = c;
            listView = lv;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!movieResults.isEmpty()) {
                movieResults.clear();
            }


        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            HttpURLConnection client = null;
            BufferedReader bufferedReader = null;
            String searchJSONstr = null;
            final String SEARCH_BASE_URL =
                    "http://api.themoviedb.org/3/";
            final String API_KEY = "9b153f4e40437e115298166e6c1b997c";
            final String API_KEY_PARAM = "api_key";
            final String MOVIE_SEGMENT = "movie";
            final String DISCOVER_QUERY_PARAM = "discover";

            buildUri = Uri.parse(SEARCH_BASE_URL).buildUpon()
                    .appendPath(DISCOVER_QUERY_PARAM)
                    .appendPath(MOVIE_SEGMENT)
                    .appendQueryParameter(API_KEY_PARAM, API_KEY)
                    .build();

            URL url = null;

            try {
                url = new URL(buildUri.toString());
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            Log.d("URL", url.toString());
            try {
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");
                client.connect();
                InputStream inputStream = client.getInputStream();
                if (inputStream == null)
                    return null;
                StringBuilder buffer = new StringBuilder();


                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line + '\n');
                }

                if (buffer.length() == 0)
                    return null;
                searchJSONstr = buffer.toString();
                Log.d("JSON Str", searchJSONstr);
            } catch (ProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                if (client != null) {
                    client.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e("Main", "Error closing stream", e);
                    }
                }
            }

            try

            {
                return getSearchDataFromJson(searchJSONstr);
            } catch (
                    JSONException e
                    )

            {
                e.printStackTrace();
            }

            return null;
        }

        private ArrayList<Movie> getSearchDataFromJson(String searchJSONstr)
                throws JSONException {
            final String LIST_NAME = "results";
            final String MOVIE_TITLE = "original_title";
            final String MOVIE_YEAR = "release_date";
            final String MOVIE_POSTER_URL = "poster_path";
            final String MOVIE_PLOT = "overview";

            JSONObject searchResult = new JSONObject(searchJSONstr);
            JSONArray movieArray = searchResult.getJSONArray(LIST_NAME);
            Log.d("movieArray", movieArray.toString());

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movieObject = movieArray.getJSONObject(i);
                String title = movieObject.getString(MOVIE_TITLE);
                String posterUrl = movieObject.getString(MOVIE_POSTER_URL);
                String year = movieObject.getString(MOVIE_YEAR);
                String plot = movieObject.getString(MOVIE_PLOT);
                movieResults.add(new Movie(title, year, "https://image.tmdb.org/t/p/original" + posterUrl, plot));
            }

            return movieResults;
        }

        @Override
        protected void onPostExecute(ArrayList<Movie> movies) {
            super.onPostExecute(movies);

            if (movies != null) {
                searchAdapter = new MyListAdapter(context, movies);
                listView.setAdapter(searchAdapter);
            }

        }

    }
}
