package com.rab.arimaa;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.*;
import android.widget.*;

public class GameInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_info);
        TextView agc = findViewById(R.id.AboutGameContent);
        TextView grc = findViewById(R.id.GameRulesContent);
        TextView wcc = findViewById(R.id.WinningConditionsContent);
        ImageView e1 = findViewById(R.id.elephant1);
        ImageView ai = findViewById(R.id.arimaaIcon);
        ImageView e2 = findViewById(R.id.elephant2);
        String agText =
                "Arimaa was invented in the year 2003."
                        + " Game developer wanted to design a new game considering that the Game can be played on a standard chess board, must be difficult to play well by computer and have simple rules that are easy to understand."
                        + " Arimaa is an 8x8 board game with four trap squares."
                        + " There are six different pieces on Arimaa Game Board, i.e. rabbit, cat, dog, horse, camel, elephant in ascending order of their weight."
                        + " Rabbit is the weakest piece and elephant is a strongest piece on the board."
                        + " This android game is designed to be played in 2 player mode.";
        String grText =
                        "1) All the pieces can move to front, back, left and right block except rabbit that cannot move backwards.\n\n"
                        + "2) None of the piece can move in diagonal direction in 1 step.\n\n"
                        + "3) In every turn, each player can make at most 4 moves.\n\n"
                        + "4) Player can finish their turn after 1 move by clicking on \"Finish Turn\".\n\n"
                        + "5) Player turn cannot be switched without having at least 1 move by player.\n\n"
                        + "6) Stronger piece can push or pull to a weaker piece.\n" +
                        "\t\tEg.6.1: Elephant can push or pull to any other enemy piece but cannot push or pull enemy's elephant.\n" +
                        "\t\tEg.6.2: Camel can push or pull to all the enemy piece except elephant and enemy's camel.\n\n"
                        + "7) In case of push or pull performed, count of moves will be 2 as pushing or pulling enemy's piece will also be considered as 1 move.\n\n"
                        + "8) Player can either push or pull enemy piece but cannot perform both together simultaneously.\n\n"
                        + "9) Piece in any of the 4 trap squares will be captured if there are no friendly piece near them.\n\n"
                        + "10) Player can capture enemy's piece by pushing or pulling them into trap square.\n\n"
                        + "11) In case weaker piece lands next to enemy's stronger piece, weaker piece will become immobile either until enemy's stronger piece moves away from the same or unless there is at least 1 friendly piece near weaker piece.\n\n"
                        + "12) If weaker piece have a friendly piece next to it, immobility is no longer valid and weaker piece can move without having stronger piece moving away from it.";
        String wcText =
                        "1) If your rabbit reaches the extreme other end of the board in forward direction.\n\n"
                        + "2) If you manage to remove all the enemy rabbits from the board by either pushing or pulling them in trap squares.\n\n"
                        + "3) If you manage to make all the enemy pieces immobile which leaves no moves for your enemy to play the game further.";
        agc.setText(agText);
        grc.setText(grText);
        wcc.setText(wcText);
        Bitmap elephant1 = BitmapFactory.decodeResource(getResources(),R.drawable.goldelephant);
        elephant1 = Bitmap.createScaledBitmap(elephant1,150,150,false);
        e1.setImageBitmap(elephant1);
        Bitmap image = BitmapFactory.decodeResource(getResources(),R.drawable.arimaa);
        image = Bitmap.createScaledBitmap(image,250,250,false);
        ai.setImageBitmap(image);
        Bitmap elephant2 = BitmapFactory.decodeResource(getResources(),R.drawable.silverelephant);
        elephant2 = Bitmap.createScaledBitmap(elephant2,150,150,false);
        e2.setImageBitmap(elephant2);
    }

    // Below method will be called when user press back button on device.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish(); // Finish existing activity.
        Intent goBack = new Intent(GameInfo.this, MainActivity.class); // Create new activity.
        startActivity(goBack); // Start new activity.
    }
}
