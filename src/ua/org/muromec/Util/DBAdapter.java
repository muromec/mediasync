package ua.org.muromec.MediaSync;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
   public static final String KEY_ROWID = "_uniq";
   public static final String KEY_SERVER_NAME = "server_name";
   public static final String KEY_SERVER_LOCAL_ADDRESS = "local";
   public static final String KEY_SERVER_WHITE_ADDRESS = "white";

   private static final String TAG = "DBAdapter";
   private static final String DATABASE_NAME = "servs";
   private static final String DATABASE_TABLE = "servers";
   private static final int DATABASE_VERSION = 1;
   private static final String DATABASE_CREATE =
     "create table servers (_uniq varchar(36) not null primary key, " +
         "server_name text not null, local text not null, " +
         "white text not null);";

   private final Context context;
   private DatabaseHelper DBHelper;
   private SQLiteDatabase db;

   public DBAdapter(Context ctx) {
      this.context = ctx;
      DBHelper = new DatabaseHelper(context);
   }

   private static class DatabaseHelper extends SQLiteOpenHelper {
      DatabaseHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL(DATABASE_CREATE);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("DROP TABLE IF EXISTS servers");
         onCreate(db);
      }
   }

   public void reCreate() {
      db.execSQL("DROP TABLE IF EXISTS servers");
      db.execSQL(DATABASE_CREATE);
   }

   // ---opens the database---
   public DBAdapter open() throws SQLException {
      db = DBHelper.getWritableDatabase();
      return this;
   }

   // ---closes the database---
   public void close() {
      DBHelper.close();
   }

   // ---insert a title into the database---
   public long insertServer(String uniq, String name, String local, String white) {
      
      ContentValues initialValues = new ContentValues();
      initialValues.put(KEY_ROWID, uniq);
      initialValues.put(KEY_SERVER_NAME, name);
      initialValues.put(KEY_SERVER_LOCAL_ADDRESS, local);
      initialValues.put(KEY_SERVER_WHITE_ADDRESS, white);

      return db.insert(DATABASE_TABLE, null, initialValues);
   }

   // ---deletes a particular title---
   public boolean deleteServer(String uniq) {
      return db.delete(DATABASE_TABLE, KEY_ROWID + "=\"" + uniq + "\"", null) > 0;
   }

   public void deleteAllServer() {
      db.execSQL("DROP TABLE IF EXISTS servers");
      db.execSQL(DATABASE_CREATE);
   }

   public Cursor getAllServers() {
      return db.query(DATABASE_TABLE, new String[] 
          { 
            KEY_ROWID, KEY_SERVER_NAME, 
            KEY_SERVER_LOCAL_ADDRESS, KEY_SERVER_WHITE_ADDRESS
          },
          null, null, null, null, null
      );
   }

   // ---retrieves a particular title---
   public Cursor getServer(String uniq) throws SQLException {
      Cursor mCursor = db.query(true, DATABASE_TABLE, new String[] 
          { 
            KEY_ROWID, KEY_SERVER_NAME, 
            KEY_SERVER_LOCAL_ADDRESS, KEY_SERVER_WHITE_ADDRESS
          },
          KEY_ROWID + "=" + uniq, 
          null, null, null, null, null
      );

      if (mCursor != null) {
         mCursor.moveToFirst();
      }
      return mCursor;
   }

   // ---updates a title---
   public boolean updateServer(String uniq, String name, String local, String white) {

      ContentValues args = new ContentValues();
      args.put(KEY_SERVER_NAME, name);
      args.put(KEY_SERVER_LOCAL_ADDRESS, local);
      args.put(KEY_SERVER_WHITE_ADDRESS, white);

      return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + uniq, null) > 0;
   }

}
