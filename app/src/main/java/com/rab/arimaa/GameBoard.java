package com.rab.arimaa;

import android.os.Bundle;
import androidx.appcompat.app.*;
import android.content.*;
import android.view.*;
import android.widget.*;

public class GameBoard extends AppCompatActivity {
    private static TextView messageTV, stepsLeftTV, playerTurnTV;
    private static Context context;
    private static GameBoard_CustomView gbcv;
    private static Actions a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_board);

        messageTV = findViewById(R.id.Message);
        stepsLeftTV = findViewById(R.id.StepsLeft);
        playerTurnTV = findViewById(R.id.PlayerTurn);
        context = this;
        gbcv = findViewById(R.id.GameBoardCustomView);
        a = new Actions();
        if(getIntent().getStringExtra("Message") != null && getIntent().getStringExtra("Message").compareTo("StartNewGame") == 0) // Checking if new game is requested.
        {
            gbcv.setWinnerDecided(); // Resetting all winner data.
        }
    }

    // Below method will be called when user clicks on Quit button.
    public void confirmQuit(View view)
    {
        if(a.recordPresent()) // In case there are few moves played by player, do as below.
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this); // Creating AlertDialog builder.
            builder.setTitle("Are you sure you want to quit this game?"); // Setting dialog message.
            // Below is to handle positive response from user.
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    goBack(); // Go back to main activity.
                }
            });
            // Below is to handle negative response from user.
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel(); // Cancel this dialog.
                }
            });
            AlertDialog dialog = builder.create(); // Creating dialog.
            dialog.show(); // Show dialog.
        }
        else // In case new game started is not yet player by either player, do as below.
        {
            goBack(); // Go back to main activity.
        }
    }

    // Below method will be called when user clicks on reset game button.
    public void resetGameButton(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Creating AlertDialog builder.
        builder.setTitle("Are you sure you want to reset this game?"); // Setting dialog message.
        // Below is to handle positive response from user.
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                a.truncateDBTable(); // Deleting all records of existing game.
                gbcv.setWinnerDecided(); // Resetting winner data.
                gbcv.resetGame("NewGame",""); // Reset game by passing NewGame in function to create a new game.
                gbcv.postInvalidate(); // Refreshing the canvas.
            }
        });
        // Below is to handle negative response from user.
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Cancel this dialog.
            }
        });
        AlertDialog dialog = builder.create(); // Creating dialog.
        dialog.show(); // Show dialog.
    }

    // Below method will be called when user press back button on device.
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // Creating AlertDialog builder.
        builder.setTitle("Are you sure you want to go back to main menu?"); // Setting dialog message.
        // Below is to handle positive response from user.
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GameBoard.super.onBackPressed();
                goBack(); // Go back to main activity.
            }
        });
        // Below is to handle negative response from user.
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Cancel this dialog.
            }
        });
        AlertDialog dialog = builder.create(); // Creating dialog.
        dialog.show(); // Show dialog.
    }

    // This method will be called when user presses back button and confirms to go back.
    public void goBack()
    {
        this.finish(); // Finish existing activity.
        Intent start = new Intent(GameBoard.this, MainActivity.class); // Create new activity.
        startActivity(start); // Start new activity.
    }

    // Below method will be called to update message on UI.
    public void updateMessage(String Message)
    {
        messageTV.setText(Message);
    }

    // Below method will be called to update Steps Left on UI.
    public void updateStepsLeft(int sl)
    {
        String update = "Steps Left: "+sl;
        stepsLeftTV.setText(update);
    }

    // Below method will be called to update player turn on UI.
    public void updatePlayerTurn(String pt)
    {
        String update = "Turn of Player: "+pt;
        playerTurnTV.setText(update);
    }

    // Below method will be called when player clicks on FinishTurn button from UI.
    public void finishTurn(View view)
    {
        if(gbcv.getWinnerDecided())
        {
            gbcv.confirmFinishTurn(); // Finishing move to switch player turn.
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context); // Creating AlertDialog builder.
            builder.setTitle("Game has already ended."); // Setting dialog message.
            // Below is to handle response from user.
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel(); // Simply cancelling dialog box.
                }
            });
            AlertDialog dialog = builder.create(); // Creating dialog.
            dialog.show(); // Show dialog.
        }
    }

    // Finish game and declare result.
    public void finishGame(String winner)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context); // Creating AlertDialog builder.
        builder.setTitle("Player: "+winner+" have won this game."); // Setting dialog message.
        // Below is to handle response from user.
        builder.setPositiveButton("Back to Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Simply cancelling dialog box.
            }
        });
        AlertDialog dialog = builder.create(); // Creating dialog.
        dialog.show(); // Show dialog.
    }

    // Below method will be called when player wishes to push enemy piece.
    public void pushPosition()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context); // Creating AlertDialog builder.
        builder.setTitle("Please select position to push the enemy piece."); // Setting dialog message.
        // Below is to handle response from user.
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Simply cancelling dialog box.
            }
        });
        AlertDialog dialog = builder.create(); // Creating dialog.
        dialog.show(); // Show dialog.
    }
    // pushPosition method ends here.

    // Below method will be called when player wish to pull enemy piece.
    public void isPull()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context); // Creating AlertDialog builder.
        builder.setTitle("Do you wish to pull your enemy piece?"); // Setting dialog message.
        // Below is to handle positive response from user.
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Cancel this dialog.
                pullPosition();
            }
        });
        // Below is to handle negative response from user.
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Cancel this dialog.
            }
        });
        AlertDialog dialog = builder.create(); // Creating dialog.
        dialog.show(); // Show dialog.
    }
    // isPull method ends here.

    // Below method will be called when player wishes to pull enemy piece.
    public void pullPosition()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context); // Creating AlertDialog builder.
        builder.setTitle("Please select enemy piece you want to pull."); // Setting dialog message.
        // Below is to handle response from user.
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Simply cancelling dialog box.
                gbcv.setPull(); // Calling method to set pull decision to true.
            }
        });
        AlertDialog dialog = builder.create(); // Creating dialog.
        dialog.show(); // Show dialog.
    }
    // pullPosition method ends here.

    // Below method will be called when player wish to undo move.
    public void onUndoMoveButtonClicked(View view)
    {
        boolean loop = true;
        if(gbcv.getWinnerDecided())
        {
            while(loop) // I will loop until we achieve our target of undoing all moves done in any turn.
            {
                undoMove("UndoMove"); // Calling undo move to function for undo by 1 step.
                if(gbcv.getStepsLeft() == 4) // In case all steps are left i.e., if turn is reverted, break the loop.
                {
                    loop = false; // Breaking the while loop.
                }
            }
        }
        else
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context); // Creating AlertDialog builder.
            builder.setTitle("Game has already ended."); // Setting dialog message.
            // Below is to handle response from user.
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel(); // Simply cancelling dialog box.
                }
            });
            AlertDialog dialog = builder.create(); // Creating dialog.
            dialog.show(); // Show dialog.
        }
    }
    // onRevertMoveButtonClicked method ends here.

    // Below method will be called to function for revert move.
    public void undoMove(String reason)
    {
        if(a.recordPresent() && a.deleteLastRecord()) // In case there are records in db table and if we are able to delete last record, do as below.
        {
            gbcv.resetGame("ResumeGame",reason); // Reset game to last saved game position and if no positions are found, create a new game.
            gbcv.postInvalidate(); // Refreshing the canvas.
        }
        else if(a.recordPresent() && !a.deleteLastRecord()) // In case there are records in db table and if we are unable to delete last record, do as below.
        {
            updateMessage("Error occurred while performing undo."); // Update message on UI.
        }
        else // In case there are no records in db table to undo move, do as below.
        {
            updateMessage("No moves to undo."); // Update message on UI.
        }
    }
    // undoMove method ends here.
}
