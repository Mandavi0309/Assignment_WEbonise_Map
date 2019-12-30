package map.develop.com.mapdemo.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;
import map.develop.com.mapdemo.model.Restaurant;

public class DatabaseHelper extends SQLiteOpenHelper {

  // Database Version
  private static final int DATABASE_VERSION = 1;

  // Database Name
  private static final String DATABASE_NAME = "notes_db";
  public static final String TABLE_NAME = "restaurant";

  public static final String COLUMN_ID = "id";
  public static final String NAME = "name";
  public static final String ADDRESS = "address";
  public static final String ICON = "icon";
  public static final String RATING = "rating";


  public DatabaseHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }


  // Create table SQL query
  public static final String CREATE_TABLE =
      "CREATE TABLE " + TABLE_NAME + "("
          + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
          + NAME + " TEXT,"
          + ADDRESS + " TEXT,"
          + ICON + " TEXT,"
          + RATING + " TEXT"
          + ")";

  // Creating Tables
  @Override
  public void onCreate(SQLiteDatabase db) {

    // create notes table
    db.execSQL(CREATE_TABLE);
  }

  // Upgrading database
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    // Drop older table if existed
    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

    // Create tables again
    onCreate(db);
  }

  public void insertData(Restaurant objRestaurant) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues values = new ContentValues();

    values.put(NAME, objRestaurant.getName());
    values.put(ADDRESS, objRestaurant.getAddress());
    values.put(ICON, objRestaurant.getImage());
    values.put(RATING, objRestaurant.getRatings());

    // insert row
    db.insert(TABLE_NAME, null, values);

    // close db connection
    db.close();

  }

  public ArrayList<Restaurant> getData() {
    // get readable database as we are not inserting anything
    ArrayList<Restaurant> restaurantList = new ArrayList<>();
    SQLiteDatabase db = this.getReadableDatabase();

    Cursor cursor = db.query(TABLE_NAME,
        null, null,
        null, null, null, null);

    if (cursor.moveToFirst()) {
      do {

        // prepare note object
        Restaurant restaurant = new Restaurant(
            cursor.getString(cursor.getColumnIndex(NAME)),
            cursor.getString(cursor.getColumnIndex(ADDRESS)),
            cursor.getString(cursor.getColumnIndex(ICON)),
            cursor.getString(cursor.getColumnIndex(RATING)));
        restaurantList.add(restaurant);
      } while (cursor.moveToNext());
    }
    // close the db connection
    cursor.close();

    return restaurantList;
  }

  public void deleteData() {
    SQLiteDatabase db = this.getWritableDatabase();
    db.delete(TABLE_NAME, null, null);
    db.close();
  }
}
