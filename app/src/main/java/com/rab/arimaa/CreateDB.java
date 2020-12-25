package com.rab.arimaa;

import android.content.Context;
import android.database.sqlite.*;
import androidx.annotation.*;
import android.util.*;

public class CreateDB extends SQLiteOpenHelper
{
    // Declaration of constructors starts here.
    public CreateDB(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    // Declaration of constructors ends here.

    // Below method is used to create db table.
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DB Message","Creating table");
        // Below is the query to create database table gamestate.
        String CreateQuery = "CREATE TABLE IF NOT EXISTS gamestate(timestamp text primary key, playerTurn varchar, stepsLeft int, state varchar);";
        db.execSQL(CreateQuery);
    }

    // Below method is used to update db.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Here upgrade means upgrading database version. The only possible way in our project is
        // by deleting all entries of db table, dropping db table and start over again.
        Log.i("DB Message","Upgrading database from version "+oldVersion+" to new version "+newVersion);
        Log.i("DB Message","Truncating table");
        // Below is the query to Truncate database table.
        String TruncateQuery = "DELETE FROM gamestate;";
        db.execSQL(TruncateQuery);
        Log.i("DB Message","Deleting table");
        // Below is the query to Drop database table.
        String DropTableQuery = "DROP TABLE IF EXISTS gamestate;";
        db.execSQL(DropTableQuery);
        onCreate(db);
    }
}
