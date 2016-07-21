package sca.biwiwatchface.activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import sca.biwiwatchface.R;
import sca.biwiwatchface.common.model.ForecastSlice;
import sca.biwiwatchface.data.Weather;

public class WeatherAdapter extends BaseAdapter {
    private static final String TAG = WeatherAdapter.class.getSimpleName();

    JSONArray mForecastArray;
    JSONObject mLocationJson;
    private static LayoutInflater mInflater = null;

    private DateFormat mDateFormat = new SimpleDateFormat("EEE H", Locale.getDefault());
    private Context mContext;

    WeatherAdapter( Context context, String jsonString ) {
        mContext = context;

        mInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);

        try {
            JSONObject json = new JSONObject( jsonString );
            mForecastArray = json.getJSONArray( "weather" );
            mLocationJson = json.getJSONObject( "location" );

        } catch ( JSONException e ) {
            Log.e( TAG, "WeatherAdapter: ", e );
        }
    }

    @Override
    public int getCount() {
        return mForecastArray.length() +1;
    }

    @Override
    public Object getItem( int position ) {
        try {
            if (position == 0) {
                return mLocationJson;
            }
            return mForecastArray.get( position-1 );
        } catch ( JSONException e ) {
            Log.e( TAG, "WeatherAdapter: ", e );
        }
        return null;
    }

    @Override
    public long getItemId( int position ) {
        return position;
    }

    @Override
    public View getView( int position, View convertView, ViewGroup parent ) {
        View vi = convertView;
        if (convertView == null) vi = mInflater.inflate( android.R.layout.simple_list_item_2, null);
        TextView text1 = (TextView) vi.findViewById(android.R.id.text1);
        TextView text2 = (TextView) vi.findViewById(android.R.id.text2);

        long now = System.currentTimeMillis();
        try {

            if (position == 0) {
                text1.setText( mLocationJson.getString( "cityName" ) );
                text2.setText( String.format( "Lat: %s Lon: %s",
                        mLocationJson.getDouble( "askedLat" ), mLocationJson.getDouble( "askedLon" ) ) );

            } else {
                JSONObject forecast = mForecastArray.getJSONObject( position-1 );
                ForecastSlice slice = ForecastSlice.ofJSONObject( forecast );
                String string1 = String.format( "%s %s° | %s°",
                        Weather.conditionIdToUnicode( slice.getConditionId() ),
                        Math.round(slice.getMaxTemp()),
                        Math.round(slice.getMinTemp()) );
                text1.setText( string1 );

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis( slice.getUTCMillisStart() );
                String szStartDate = mDateFormat.format( calendar.getTime() );

                String ageWarning = "";
                final int MILLIS_IN_HOUR = 60*60*1000;
                long fetchTs = slice.getFetchTimestampUTCMillis();
                long ageInHour = (now-fetchTs)/MILLIS_IN_HOUR;
                if (ageInHour > 3) {
                    ageWarning = " (" + String.format( mContext.getString( R.string.format_weather_age_warning ), ageInHour ) +")";
                }

                String string2 = String.format( "%sh - %sh%s", szStartDate, slice.getLocalHourEnd(), ageWarning );
                text2.setText( string2 );
            }
        } catch ( JSONException e ) {
            Log.e( TAG, "WeatherAdapter: ", e );
        }

        return vi;
    }
}