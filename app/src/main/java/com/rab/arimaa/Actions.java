package com.rab.arimaa;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.text.SimpleDateFormat;
import java.util.*;

public class Actions {
    private static String tableName; // Setting table name to default.
    private static SQLiteDatabase myDB; // This is globally declared to be used every methods.

    // Declaring required constructors.
    public Actions() {}
    public Actions(SQLiteDatabase myDB, String tableName)
    {
        Actions.myDB = myDB;
        Actions.tableName = tableName;
    }
    // Declaration ends here.

    // Below function is used to check if there are any records in DB or not.
    protected boolean recordPresent()
    {
        Cursor dbValues = myDB.rawQuery("Select * From " + tableName, null); // Query to fetch all data in db table.
        boolean decision = false; // Declaring and initializing decision variable to false.
        if(dbValues.getCount() > 0) // Checking if there are records in db. If yes, do as below.
        {
            decision = true; // Assign true to decision.
        }
        dbValues.close(); // Else close the connection.
        return decision; // Return decision.
    }
    // recordPresent method ends here.

    // Below function is used to truncateDBTable.
    protected void truncateDBTable()
    {
        String TruncateQuery = "DELETE FROM gamestate;"; // This is string to truncate db table.
        myDB.execSQL(TruncateQuery); // Execute truncate statement.
    }
    // truncateDBTable method ends here.

    // Below function will be used to convert last record in db to 2D string array.
    protected String[][] getStringArray()
    {
        String state;
        String[][] gameState = new String[8][8];
        Cursor dbValue = myDB.rawQuery("Select * From " + tableName + " DESC", null); // Query to fetch last record in db table.
        dbValue.moveToLast(); // Move to last record from fetched records.
        state = dbValue.getString(dbValue.getColumnIndex("state")); // Return state of game from db in String format.
        String[] temp = state.split(","); // This will contain 64 elements in 1D array.
        int k=0; // This will be used to handle index of temp.
        for(int i=0; i<8; i++) // This will be used to handle rows.
        {
            for(int j=0; j<8; j++) // This will be used to handle columns.
            {
                if(k < temp.length && temp[k] != null && temp[k].compareTo("") != 0)
                    gameState[i][j] = temp[k]; // This will be used to put 8 elements each time in gameState.
                k++; // Incrementing k.
            }
        }
        dbValue.close();
        return gameState; // Returning gameState.
    }
    // getStringArray method ends here.

    // Below method will be used to identify player turn when game resumes.
    protected String getPlayerTurn()
    {
        String playerTurn;
        Cursor dbValue = myDB.rawQuery("Select * From " + tableName + " DESC", null); // Query to fetch last record in db table.
        dbValue.moveToLast(); // Move to last record from fetched records.
        playerTurn = dbValue.getString(dbValue.getColumnIndex("playerTurn")); // Return player turn from db in String format.
        dbValue.close(); // Closing cursor.
        return playerTurn; // Returning playerTurn.
    }
    // getPlayerTurn method ends here.

    // Below method will be used to identify steps left when game resumes.
    protected int getStepsLeft()
    {
        int stepsLeft;
        Cursor dbValue = myDB.rawQuery("Select * From " + tableName + " DESC", null); // Query to fetch last record in db table.
        dbValue.moveToLast(); // Move to last record from fetched records.
        stepsLeft = dbValue.getInt(dbValue.getColumnIndex("stepsLeft")); // Return steps left from db in String format.
        dbValue.close(); // Closing cursor.
        return stepsLeft; // Returning stepsLeft.
    }
    // getStepsLeft method ends here.

    // Below function will be used to convert 2D String array to String and store it in db.
    protected String convertToString(String[][] gameState)
    {
        StringBuilder sb = new StringBuilder(); // Initializing string builder.
        for(int i=0; i<8; i++) // This loop handles rows.
        {
            for(int j=0; j<8; j++) // This loop handles columns.
            {
                if(gameState[i][j] != null)
                    sb.append(gameState[i][j]); // Appending values of each row and each column to string builder.
                else
                    sb.append("");
                if(j!=7) // Below will be appended for all records other than last record in our 2D array.
                {
                    sb.append(","); // Appending ',' after appending each value to string builder.
                }
            }
            if(i!=7) // Below will be appended for all records other than last record in our 2D array.
            {
                sb.append(","); // Appending ',' after appending each value to string builder.
            }
        }
        return sb.toString(); // Returning gameState in string format.
    }
    // convertToString method ends here.

    // Below method will be called to store data into db table.
    protected boolean storeData(String playerTurn, int stepsLeft, String[][] gameState)
    {
        String state; // Initializing variable.
        state = convertToString(gameState);
        if(state.compareTo("") != 0) // In case state is not blank, do as below.
        {
            String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SS", Locale.getDefault()).format(new Date()); // Getting current date.
            ContentValues cv = new ContentValues(); // Passing values to be written on database.
            cv.put("timestamp", timestamp); // Adding timestamp.
            cv.put("playerTurn", playerTurn); // Adding playerTurn.
            cv.put("stepsLeft", stepsLeft); // Adding stepsLeft.
            cv.put("state", state); // Adding above generated StringBuilder.
            long Status = myDB.insert(tableName, null, cv); // Storing status of insert in Status.
            return Status != -1; // This will return true if above status is not equal to -1 else will return false.
        }
        else // In case passed string is blank, return false.
        {
            return false; // Return false.
        }
    }

    // Below method will be used to delete 1 row from DB table.
    protected boolean deleteLastRecord()
    {
        String dateTime;
        int status;
        Cursor dbValue = myDB.rawQuery("Select * From " + tableName + " DESC", null); // Query to fetch last record in db table.
        dbValue.moveToLast(); // Move to last record from fetched records.
        dateTime = dbValue.getString(dbValue.getColumnIndex("timestamp")); // Return timestamp from db in String format.
        status = myDB.delete(tableName,"timestamp='"+dateTime+"'",null); // Delete last record from db table.
        dbValue.close(); // Closing cursor.
        return status>0; // Return if deletion was performed or not.
    }
    // deleteLastRecord method ends here.
}
