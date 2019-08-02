package rajiv.project.com.weatherdemo.util;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by SUJAN on 06-Nov-17.
 */

public class LocalDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "worldcities.sqlite";
    private static final String DB_PATH = "/data/data/rajiv.project.com.weatherdemo/databases/";
    private SQLiteDatabase myDataBase;
    private Context mycontext;

    public LocalDbHelper(Context context) throws IOException {
        super(context, DB_NAME, null, 1);
        this.mycontext = context;

        boolean dbexist = checkDatabase();
        if (dbexist) {
            openDatabase();
        } else {
            createdatabase();
        }
    }

    SQLiteDatabase getMyDataBase() {

        boolean dbexist = checkDatabase();
        if (dbexist) {
            openDatabase();
            return myDataBase;

        } else {
            try {
                createdatabase();
                return myDataBase;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void setMyDataBase(SQLiteDatabase myDataBase) {
        this.myDataBase = myDataBase;
    }

    private void createdatabase() throws IOException {
        boolean dbexist = checkDatabase();
        if (dbexist) {
        } else {
            this.getReadableDatabase();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDatabase() {
        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
        }

        return checkdb;
    }

    private void copyDatabase() throws IOException {

        InputStream myinput = mycontext.getAssets().open(DB_NAME);
        String outfilename = DB_PATH + DB_NAME;
        OutputStream myoutput = new FileOutputStream("/data/data/rajiv.project.com.weatherdemo/databases/worldcities.sqlite");

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer)) > 0) {
            myoutput.write(buffer, 0, length);
        }
        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

    private void openDatabase() throws SQLException {

        String mypath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);

    }

    public synchronized void close() {
        if (myDataBase != null) {
            myDataBase.close();
        }
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}