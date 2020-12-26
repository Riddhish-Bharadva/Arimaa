package com.rab.arimaa;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.database.sqlite.*;
import android.content.*;
import android.graphics.*;
import android.widget.*;

public class MainActivity extends AppCompatActivity
{
    private Actions a; // This is an object of Class Actions.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int dbVersion = 1; // Setting db version to pass in CreateDB constructor.
        String dbName = "Arimaa"; // Setting db name to pass in CreateDB constructor. This will be always same.
        String tableName = "gamestate"; // Setting table name to default.
        Button resume; // This button will be visible only in case there is any record in our db for last session.
        ImageView gl = findViewById(R.id.GameLogo);

        // Calling a constructor of an activity (Java class) to establish connection with my Database and create a table if it does not exist.
        CreateDB CDB = new CreateDB(getApplicationContext(), dbName, null, dbVersion);
        CDB.getWritableDatabase(); // Calling function to execute db creation if not exists.
        resume = findViewById(R.id.Resume);

        // Checking if there are any entries in db table.
        SQLiteDatabase DB = this.openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        a = new Actions(DB, tableName); // Creating object and passing required variables to constructor.
        if(!a.recordPresent()) // If my record count is 0, do as below.
        {
            // Disabling resume button in case previous game data is not present on db.
            resume.setVisibility(View.INVISIBLE);
        }
        Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.arimaa);
        image = Bitmap.createScaledBitmap(image,400,400,false);
        gl.setImageBitmap(image);
    }

    // Below method is used to redirect user to Game page with mode resume game.
    public void resumeGame(View view)
    {
        this.finish(); // Finish existing activity.
        Intent RG = new Intent(MainActivity.this, GameBoard.class); // Redirect.
        startActivity(RG); // Start redirecting.
    }

    // Below method is used to redirect user to Game page with mode new game.
    public void newGame(View view)
    {
        if(a.recordPresent()) // In case there are records and if user wants to start new game, do below.
        {
            a.truncateDBTable(); // Call function to truncate DB table.
        }
        this.finish(); // Finish existing activity.
        Intent NG = new Intent(MainActivity.this, GameBoard.class); // Redirect.
        NG.putExtra("Message","StartNewGame");
        startActivity(NG); // Start redirecting.
    }

    // Below method is used to redirect user to GameInfo page.
    public void gameInfo(View view)
    {
        this.finish(); // Finish existing activity.
        Intent GI = new Intent(MainActivity.this, GameInfo.class); // Redirect.
        startActivity(GI); // Start redirecting.
    }

    // Below method is used to terminate the game.
    public void exitGame(View view)
    {
        this.finish();
    }

    // Below method will be called when user press back button on device.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish(); // Finish existing activity.
        Intent start = new Intent(MainActivity.this, MainActivity.class); // Create new activity.
        startActivity(start); // Start new activity.
    }
}
