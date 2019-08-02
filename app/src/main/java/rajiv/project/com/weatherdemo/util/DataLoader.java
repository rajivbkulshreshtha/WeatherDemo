package rajiv.project.com.weatherdemo.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import br.com.mauker.materialsearchview.MaterialSearchView;

/**
 * Created by SUJAN on 06-Nov-17.
 */

public class DataLoader extends android.support.v4.content.AsyncTaskLoader<String> {

    private static int count;
    private LocalDbHelper localDbHelper;
    private MaterialSearchView materialSearchView;
    private static final String TAG = "DataLoader";

    public DataLoader(Context context, LocalDbHelper localDbHelper, MaterialSearchView materialSearchView) {
        super(context);
        this.localDbHelper = localDbHelper;
        this.materialSearchView = materialSearchView;

    }


    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {

       try{

           if (localDbHelper != null) {

               if (localDbHelper.getMyDataBase() != null) {

                   Cursor cursor = localDbHelper.getMyDataBase().query("LOCATION_DB", new String[]{"city", "iso2"}, null, null, null, null, null);
                   getListProduct(cursor);
               }
           }
           return "";

       }catch (Exception e){
           Log.d(TAG, "loadInBackground: ");
       }
        return "";

    }

    @Override
    public void deliverResult(String data) {
        super.deliverResult(data);
    }

    private void getListProduct(Cursor cursor) {

        int index_city = cursor.getColumnIndex("city");
        int index_county = cursor.getColumnIndex("iso2");

        cursor.moveToFirst();
        while (!cursor.isAfterLast() && cursor.getCount() > 0) {

            String str;
            String city = cursor.getString(index_city);
            String country = cursor.getString(index_county);

            str = String.format(city + " , " + country);
            materialSearchView.addSuggestion(str);
            cursor.moveToNext();
        }
        cursor.close();
    }


}