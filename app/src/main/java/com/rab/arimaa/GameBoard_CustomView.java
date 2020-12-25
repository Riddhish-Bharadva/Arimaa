package com.rab.arimaa;

import android.content.Context;
import android.os.Build;
import java.text.DecimalFormat;
import java.util.*;
import android.util.*;
import android.view.*;
import android.graphics.*;
import androidx.annotation.*;

public class GameBoard_CustomView extends View {
    // Declaring required variables. All the below variables are static as we do not want any other instance of any variables when an object is created.
    private static int screenSize = 0, boxSize = 0, imageSize = 0, pieceStartX = 0, pieceStartY = 0, goldRabbit = 8, silverRabbit = 8, pushStartX = 0, pushStartY = 0, pullEndX = 0, pullEndY = 0, enemyWeakPieceAdjacentCount = 0;
    private static Paint light, dark, border, selectedPiece, possibleMoves; // These are colors of boxes on board.
    private static Rect square; // This is to make blocks on screen square.
    private static boolean winnerDecided = false; // This will decide if game can be continued or not.
    private static Actions a; // Object of class Actions.
    private static GameBoard gb; // Object of class GameBoard.
    private static String[][] piece; // This will contain positions of all the pieces on board.
    private static String[][] selectPossible = new String[8][8]; // Declaring 2D-String array to identify selected piece on board and possible moves of same.
    private static String playerTurn; // This will contain which player turn is it.
    private static int stepsLeft; // This will contain count of steps left for each player before steps are finished.
    private static boolean touch = false; // This will handle touch events.
    private static boolean pushTouch = false; // This variable will be used to identify push touch decisions in the game.
    private static boolean pullTouch = false; // This variable will be used to handle pull request by user.
    private static double touchxS, touchyS, touchxE, touchyE; // These will contain start and end of touch x and y positions.
    private static double pushEndX, pushEndY; // These will contain position of push piece in the game.
    private static double pullStartX, pullStartY; // These will contain start position of pull piece in the game.
    private static String winner = ""; // This will store final winner of the game.
    private static final Map<String,Integer> pieceWeight = new HashMap<String,Integer>(); // This hashmap will contain weight of all animals.
    // Declaration ends here.

    // Declaration of required constructors starts here.
    public GameBoard_CustomView(Context context) {
        super(context);
    }

    public GameBoard_CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GameBoard_CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    // Declaration of constructors ends here.

    // Below onMeasure method is responsible to force layout to be Square in all devices.
    @Override
    public void onMeasure(int screenWidth, int screenHeight) {
        super.onMeasure(screenWidth, screenHeight);
        int width = MeasureSpec.getSize(screenWidth);
        int height = MeasureSpec.getSize(screenHeight);
        screenSize = Math.min(width, height); // Whichever from width and height is less, will be returned.
        setMeasuredDimension(screenSize, screenSize);
        init(); // Calling init method to initialize all required variables.
    }
    // onMeasure Method Ends.

    // Init method is responsible to initialize all declared variables.
    private void init() {
        a = new Actions(); // Creating object to be used later in other methods.
        gb = new GameBoard(); // Creating object of GameBoard. This will be later used to update UI elements.
        // Below code creates paint objects to render rectangle.
        light = new Paint(Paint.ANTI_ALIAS_FLAG);
        light.setStyle(Paint.Style.FILL); // This will fill rectangle.
        light.setColor(Color.rgb(173, 135, 98)); // Setting color to light brown.

        dark = new Paint(Paint.ANTI_ALIAS_FLAG);
        dark.setStyle(Paint.Style.FILL); // This will fill rectangle.
        dark.setColor(Color.rgb(101, 67, 33)); // Setting color to dark brown.

        border = new Paint(Paint.ANTI_ALIAS_FLAG);
        border.setStyle(Paint.Style.STROKE); // This will draw border of rectangle.
        border.setColor(Color.BLACK); // Setting color to black.

        selectedPiece = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectedPiece.setStyle(Paint.Style.FILL); // This is color of selected piece on board.
        selectedPiece.setColor(Color.GREEN); // Setting color to green.

        possibleMoves = new Paint(Paint.ANTI_ALIAS_FLAG);
        possibleMoves.setStyle(Paint.Style.FILL); // This will show suggested moves on board.
        possibleMoves.setColor(Color.YELLOW); // Setting color to yellow.

        // Below variable sets block size to 1/8 of screen size.
        boxSize = screenSize / 8; // This will be my box size.
        imageSize = boxSize - boxSize/4; // Image size will always be small than box size.
        // Below code initializes rectangle.
        square = new Rect(0, 0, boxSize, boxSize); // Creating box using rectangle of dimension boxSize*boxSize.

        pieceWeight.put("rabbit",1); // Adding weight for rabbit.
        pieceWeight.put("cat",2); // Adding weight for cat.
        pieceWeight.put("dog",3); // Adding weight for dog.
        pieceWeight.put("horse",4); // Adding weight for horse.
        pieceWeight.put("camel",5); // Adding weight for camel.
        pieceWeight.put("elephant",6); // Adding weight for elephant.

        if(!winnerDecided)
        {
            if(a.recordPresent()) // In case there are records in db table, resume game.
            {
                resetGame("ResumeGame","Resume"); // Calling function to resume game from last saved position.
            }
            else // In case there are no records in db table, reset game and continue.
            {
                resetGame("NewGame",""); // Calling function to reset positions of game pieces.
            }
        }
    }
    // Init Method Ends.

    // Below method will reset the board pieces. This function will be used to when a new game is started or reset button is pressed.
    public void resetGame(String gameStatus, String reason) {
        selectPossible = new String[8][8]; // Initializing 2D string array to keep board selected and possible moves piece blank.
        piece = new String[8][8]; // Initializing 2D-String array to keep all reset related functions here.
        playerTurn = "Gold"; // Initializing player turn to Gold.
        stepsLeft = 4; // Resetting stepsLeft to 4.
        if(gameStatus.compareTo("NewGame") == 0) // In case gameState is NewGame, do as below.
        {
            for(int i=0; i<8; i++) // This loop will handle rows.
            {
                for(int j=0; j<8; j++) // This loop will handle columns.
                {
                    if(i==0) // For Silver side 1st row.
                    {
                        piece[i][j] = "rabbit";
                    }
                    else if(i==1) // For Silver side 2nd row.
                    {
                        if(j==0 || j==7) // Positioning 2 Cat.
                        {
                            piece[i][j] = "cat";
                        }
                        else if(j==1 || j==6) // Positioning 2 Dog.
                        {
                            piece[i][j] = "dog";
                        }
                        else if(j==2 || j==5) // Positioning 2 Horse.
                        {
                            piece[i][j] = "horse";
                        }
                        else if(j==3) // Positioning 1 Camel.
                        {
                            piece[i][j] = "camel";
                        }
                        else // Positioning 1 Elephant.
                        {
                            piece[i][j] = "elephant";
                        }
                    }
                    else if(i==6) // For Gold side 2nd row.
                    {
                        if(j==0 || j==7) // Positioning 2 Cat.
                        {
                            piece[i][j] = "CAT";
                        }
                        else if(j==1 || j==6) // Positioning 2 Dog.
                        {
                            piece[i][j] = "DOG";
                        }
                        else if(j==2 || j==5) // Positioning 2 Horse.
                        {
                            piece[i][j] = "HORSE";
                        }
                        else if(j==3) // Positioning 1 Camel.
                        {
                            piece[i][j] = "CAMEL";
                        }
                        else // Positioning 1 Elephant.
                        {
                            piece[i][j] = "ELEPHANT";
                        }
                    }
                    else if(i==7) // For Gold side 1st row.
                    {
                        piece[i][j] = "RABBIT";
                    }
                }
            }
            gb.updatePlayerTurn("Gold"); // Updating player turn.
            gb.updateMessage("New game has been started."); // Update message on UI.
        }
        else if(gameStatus.compareTo("ResumeGame") == 0) // In case gameState is ResumeGame, do as below.
        {
            if(a.recordPresent()) // In case game is resumed or there are records after last undo move, do as below.
            {
                piece = a.getStringArray(); // This function will return 2D string array and is stored in piece.
                playerTurn = a.getPlayerTurn(); // Identifying player turn.
                stepsLeft = a.getStepsLeft(); // Identifying steps left.
                gb.updatePlayerTurn(playerTurn); // Updating player turn.
                if(reason.compareTo("Resume") == 0) // If game is resumed, do as below.
                {
                    gb.updateMessage("Game has been resumed."); // Update message on UI.
                }
                else if(reason.compareTo("UndoMove") == 0)
                {
                    gb.updateMessage("Success to undo the last move."); // Update message on UI.
                }
                else if(reason.compareTo("InvalidPull") == 0) // If game have InvalidPull, do as below.
                {
                    gb.updateMessage("Invalid position to pull your enemy piece.");
                }
            }
            else // In case there are no records in DB table, do as below.
            {
                resetGame("NewGame",""); // Call itself with NewGame argument.
            }
        }
        gb.updateStepsLeft(stepsLeft); // Updating steps left on UI.
    }
    // ResetGame ends here.

    // onDraw method is designed in such a way that it will draw board as per values in array of Player1 and Player2.
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Bitmap bitmap; // Creating bitmap.
        for(int i = 1; i < 9; i++) // This loop will handle rows. i is starting from 1 as we are using % for below calculation.
        {
            for (int j = 1; j < 9; j++) // This loop will handle columns. i is starting from 1 as we are using % for below calculation.
            {
                canvas.drawRect(square, border); // This will draw borders in defined color for each box.
                if(selectPossible[i-1][j-1]!= null && selectPossible[i-1][j-1].compareTo("selected") == 0) // In case there are any selected piece, do as below.
                {
                    canvas.drawRect(square, selectedPiece); // This will draw square in selectedPiece defined color.
                }
                else if(selectPossible[i-1][j-1]!= null && selectPossible[i-1][j-1].compareTo("possible") == 0) // In case there are any possible moved for selected piece, do as below.
                {
                    canvas.drawRect(square, possibleMoves); // This will draw square in possibleMoves defined color.
                }
                else // In case above 2 conditions are not satisfied, do as below.
                {
                    if(i%3==0 && j%3==0) // This condition will handle middle boxes dark.
                    {
                        canvas.drawRect(square, dark); // This will draw square in defined dark color.
                    }
                    else // If above is not true, do as below.
                    {
                        canvas.drawRect(square, light); // This will draw square in defined light color.
                    }
                }
                bitmap = identifyPiece(i-1,j-1); // Passing i-1 and j-1 as i & j are both starting from 1 but 2D array starts from 0.
                if(bitmap != null) // Incase bitmap is not null, draw image as below.
                {
                    bitmap = Bitmap.createScaledBitmap(bitmap,imageSize,imageSize,false); // Rescaling image.
                    canvas.drawBitmap(bitmap,(float)boxSize/10,(float)boxSize/10,null); // drawing image on canvas.
                }
                canvas.save(); // Save canvas.
                canvas.translate(boxSize, 0); // Translating canvas for columns.
            }
            canvas.translate(-screenSize,0); // Translating canvas to 0 column.
            canvas.translate(0, boxSize); // Translating canvas to next box in row.
        }
    }
    // onDraw Method Ends.

    // Below method will handle first time touch events.
    @Override
    public boolean onTouchEvent(MotionEvent ME) {
        super.onTouchEvent(ME);
        if(!winnerDecided && ME.getActionMasked() == MotionEvent.ACTION_DOWN && !touch && !pushTouch && !pullTouch) // In case of touch and pushTouch are both false, do as below.
        {
            touchxS = ME.getX(); // Assign x of player touch to global variable x of start position.
            touchyS = ME.getY(); // Assign y of player touch to global variable y of start position.
            DecimalFormat d = new DecimalFormat("####.#"); // Formatting touch coordinates to 1 decimal.
            touchxS = Double.parseDouble(d.format(touchxS)); // Applying format.
            touchyS = Double.parseDouble(d.format(touchyS)); // Applying format.
            selectPossible = new String[8][8];
            if(pieceMoveStart()) // If first touch to move a piece is valid, do as below.
            {
                if(!nearStrongerPiece()) // In case piece is not near opponent's stronger piece, do as below.
                {
                    touch = true; // Make touch to true so that next touch player does is for moving piece to next position.
                    highlightPossibleMoves(); // Calling function to identify possible moves.
                }
                else if(nearStrongerPiece() && nearFriendlyPiece()) // In case piece is near opponent's stronger piece and it have at least 1 friendly piece near it, do as below.
                {
                    touch = true; // Make touch to true so that next touch player does is for moving piece to next position.
                    highlightPossibleMoves(); // Calling function to identify possible moves.
                }
                else // In case piece is near opponent's stronger piece and there are no friendly piece near it, do as below.
                {
                    gb.updateMessage("Cannot move this piece as it is near opponent's stronger piece."); // Update message on UI.
                }
            }
            invalidate(); // Invalidate the canvas to take piece position highlighting effect.
            return true; // return true for onTouchEvent.
        }
        else if(!winnerDecided && ME.getActionMasked() == MotionEvent.ACTION_DOWN && touch && !pushTouch && !pullTouch) // In case touch is true and pushTouch is false, do as below.
        {
            touchxE = ME.getX(); // Assign x of player touch to global variable x of end position.
            touchyE = ME.getY(); // Assign y of player touch to global variable y of end position.
            DecimalFormat dE = new DecimalFormat("####.#"); // Formatting touch coordinates to 1 decimal.
            touchxE = Double.parseDouble(dE.format(touchxE)); // Applying format.
            touchyE = Double.parseDouble(dE.format(touchyE)); // Applying format.
            pieceMoveEnd(); // Calling pieceMoveEnd method. This method will handle push and pull event as well.
            invalidate(); // Invalidate the canvas to take piece position changed effect.
            return true; // return true for onTouchEvent.
        }
        else if(!winnerDecided && ME.getActionMasked() == MotionEvent.ACTION_DOWN && !touch && pushTouch && !pullTouch) // In case touch is false and pushTouch is true, do as below.
        {
            pushEndX = ME.getX(); // Assign x of player touch to global variable x of push position.
            pushEndY = ME.getY(); // Assign y of player touch to global variable y of push position.
            DecimalFormat dE = new DecimalFormat("####.#"); // Formatting touch coordinates to 1 decimal.
            pushEndX = Double.parseDouble(dE.format(pushEndX)); // Applying format.
            pushEndY = Double.parseDouble(dE.format(pushEndY)); // Applying format.
            if(pieceMoveEnd()) // Calling pieceMoveEnd method. This method will handle push and pull event as well.
            {
                pushTouch = false; // Setting pushTouch to false so that next touch will be for next move.
            }
            invalidate(); // Invalidate the canvas to take piece position changed effect.
            return true; // return true for onTouchEvent.
        }
        else if(!winnerDecided && ME.getActionMasked() == MotionEvent.ACTION_DOWN && !touch && !pushTouch && pullTouch) // In case touch is false and pushTouch is true, do as below.
        {
            pullStartX = ME.getX(); // Assign x of player touch to global variable x of pull position.
            pullStartY = ME.getY(); // Assign y of player touch to global variable y of pull position.
            DecimalFormat dE = new DecimalFormat("####.#"); // Formatting touch coordinates to 1 decimal.
            pullStartX = Double.parseDouble(dE.format(pullStartX)); // Applying format.
            pullStartY = Double.parseDouble(dE.format(pullStartY)); // Applying format.
            if(pieceMoveEnd()) // Calling pieceMoveEnd method. This method will handle push and pull event as well.
            {
                pullTouch = false; // Setting pushTouch to false so that next touch will be for next move.
            }
            invalidate(); // Invalidate the canvas to take piece position changed effect.
            return true; // return true for onTouchEvent.
        }
        return false; // In case none of above if is satisfied, return false.
    }
    // onTouchEvent method ends here.

    // Below method will handle first time identification of touched start.
    private boolean pieceMoveStart() {
        if(!touch && touchxS > 0.0 && touchyS > 0.0) // If touch is not blank, do as below.
        {
            for(int i=0; i<8; i++) // This loop handles rows.
            {
                if(touchyS >= boxSize*i && touchyS < boxSize*(i+1)) // If y position is satisfied, do as below.
                {
                    for(int j=0; j<8; j++) // This loop handles columns.
                    {
                        if(touchxS >= boxSize*j && touchxS < boxSize*(j+1)) // If x position is satisfied, do as below.
                        {
                            if(playerTurn.compareTo("Gold") == 0) // If player turn is Gold, do as below.
                            {
                                if(piece[i][j] != null && Character.isUpperCase(piece[i][j].charAt(0))) // If correct piece is selected to player Gold, do as below.
                                {
                                    selectPossible[i][j] = "selected"; // Set piece at position of i and j to selected for color change.
                                    gb.updateMessage("Piece for player Gold is selected."); // Update message on UI.
                                    pieceStartX = j; // Assign j to global variable for later use.
                                    pieceStartY = i; // Assign i to global variable for later use.
                                    return true; // Return true if everything works fine.
                                }
                                else // In case incorrect piece is selected for player gold, do as below.
                                {
                                    gb.updateMessage("Please select a valid piece of player: Gold."); // Update message on UI.
                                }
                            }
                            else if(playerTurn.compareTo("Silver")==0) // If player turn is Silver, do as below.
                            {
                                if(piece[i][j] != null && Character.isLowerCase(piece[i][j].charAt(0))) // If correct piece is selected to player Silver, do as below.
                                {
                                    selectPossible[i][j] = "selected"; // Set piece at position of i and j to selected for color change.
                                    gb.updateMessage("Piece for player Silver is selected."); // Update message on UI.
                                    pieceStartX = j; // Assign j to global variable for later use.
                                    pieceStartY = i; // Assign i to global variable for later use.
                                    return true; // Return true if everything works fine.
                                }
                                else // In case incorrect piece is selected for player silver, do as below.
                                {
                                    gb.updateMessage("Please select a valid piece of player: Silver."); // Update message on UI.
                                }
                            }
                        }
                    }
                }
            }
        }
        return false; // In case all of above conditions are not satisfied, return false.
    }
    // pieceMoveStart method ends here.

    // Below method will handle first time identification of touched end.
    private boolean pieceMoveEnd() {
        if(touch && touchxE > 0.0 && touchyE > 0.0) // In case second touch is valid, do as below.
        {
            touch = false; // Making touch to false. This will handle invalid touch.
            for(int i=0; i<8; i++) // This loop handles rows.
            {
                if(touchyE >= boxSize*i && touchyE < boxSize*(i+1)) // If match for y axis is found, do as below.
                {
                    for(int j=0; j<8; j++) // This loop handles columns.
                    {
                        if(touchxE >= boxSize*j && touchxE < boxSize*(j+1)) // If match for x axis is found, do as below.
                        {
                            if(selectPossible[i][j] != null && selectPossible[i][j].compareTo("possible") == 0) // Since we have already calculated possible moves, we will check if second touch is one of them or not.
                            {
                                if(piece[i][j] == null) // In case there is nothing present on end position, do as below.
                                {
                                    enemyWeakPieceAdjacentCount = 0; // Resetting variable to 0 as this may contain non 0 values from last move.
                                    if(stepsLeft>1 && nearWeakerEnemy()) // In case player have more than 1 steps left and have some weaker enemy piece near it, do as below.
                                    {
                                        gb.isPull(); // This will trigger dialog for user to select an option of pull.
                                        pullEndY = pieceStartY; // End Y of enemy piece to be pulled will be start Y of piece to be moved.
                                        pullEndX = pieceStartX; // End X of enemy piece to be pulled will be start X of piece to be moved.
                                    }
                                    piece[i][j] = piece[pieceStartY][pieceStartX]; // Change position.
                                    piece[pieceStartY][pieceStartX] = null; // Set previous position to null.
                                    stepsLeft = stepsLeft - 1; // Updating global variable for remaining steps.
                                    gb.updateStepsLeft(stepsLeft); // Updating steps left on UI.
                                    if(playerTurn.compareTo("Gold") == 0) // If player turn is Gold, do as below.
                                    {
                                        gb.updateMessage("There are steps left to be moved by player: Gold."); // Update message on UI.
                                    }
                                    else // If player turn is Silver, do as below.
                                    {
                                        gb.updateMessage("There are steps left to be moved by player: Silver."); // Update message on UI.
                                    }
                                    if(stepsLeft == 0) // In case there are no steps left by player, do as below.
                                    {
                                        stepsLeft = 4; // Reset stepsLeft to 4 for next player's turn.
                                        if(playerTurn.compareTo("Gold") == 0)
                                        {
                                            playerTurn = "Silver"; // Change player turn.
                                            gb.updateMessage("Player: Gold have finished it's turn."); // Update message on UI.
                                        }
                                        else if (playerTurn.compareTo("Silver") == 0)
                                        {
                                            playerTurn = "Gold"; // Change player turn.
                                            gb.updateMessage("Player: Silver have finished it's turn."); // Update message on UI.
                                        }
                                        gb.updateStepsLeft(stepsLeft); // Updating steps left on UI.
                                        gb.updatePlayerTurn(playerTurn); // Update player turn on UI.
                                    }
                                    selectPossible = new String[8][8]; // Reset 2D array.
                                    checkTrapBlocks(); // Check trap blocks for any elimination.
                                    return true;
                                }
                                else // In case there are some enemy piece present on end position, do as below.
                                {
                                    if(stepsLeft>1)
                                    {
                                        pushTouch = true; // Changing pushTouch to true so that next touch will handle end position of pushing piece.
                                        pushStartY = i; // Assigning i to pushY. This Y position is of piece to be moved.
                                        pushStartX = j; // Assigning j to pushX. This X position is of piece to be moved.
                                        highlightPossibleMoves(); // This will highlight all possible moves where enemy piece can be moved.
                                        gb.pushPosition(); // Calling method to display dialog box.
                                        checkTrapBlocks(); // Check trap blocks for any elimination.
                                        return true;
                                    }
                                    else
                                    {
                                        selectPossible = new String[8][8]; // Resetting 2D array for next move.
                                        gb.updateMessage("Cannot push your enemy piece as you only have 1 step left."); // Update message on UI.
                                    }
                                }
                            }
                            else // In case above is not true, do as below.
                            {
                                gb.updateMessage("Please select a valid position to move the piece."); // Update message on UI.
                            }
                        }
                    }
                }
            }
        }
        else if(pushTouch && pushEndX > 0.0 && pushEndY > 0.0) // In case pushTouch is true and we have non 0 values for x and y, do as below.
        {
            pushTouch = false; // Setting pushTouch to false. This will avoid any error in sensing touch in onTouchEvent method.
            for(int i=0; i<8; i++) // This loop handles rows.
            {
                if(pushEndY >= boxSize * i && pushEndY < boxSize * (i + 1)) // If match for y axis is found, do as below.
                {
                    for (int j = 0; j < 8; j++) // This loop handles columns.
                    {
                        if (pushEndX >= boxSize * j && pushEndX < boxSize * (j + 1)) // If match for x axis is found, do as below.
                        {
                            if(piece[i][j] == null && selectPossible[i][j] != null && selectPossible[i][j].compareTo("possible") == 0) // Since we have already calculated possible moves, we will check if second touch is one of them or not.
                            {
                                piece[i][j] = piece[pushStartY][pushStartX]; // Moving enemy piece to new position.
                                piece[pushStartY][pushStartX] = piece[pieceStartY][pieceStartX]; // Moving original piece to new position.
                                piece[pieceStartY][pieceStartX] = null; // Emptying position of original piece.
                                stepsLeft = stepsLeft - 2; // Decrementing steps left by 2 as push is also considered as 1 step.
                                gb.updateStepsLeft(stepsLeft); // Updating steps left on UI.
                                if(playerTurn.compareTo("Gold") == 0) // If player turn is Gold, do as below.
                                {
                                    gb.updateMessage("There are steps left to be moved by player: Gold."); // Update message on UI.
                                }
                                else // If player turn is Silver, do as below.
                                {
                                    gb.updateMessage("There are steps left to be moved by player: Silver."); // Update message on UI.
                                }
                                if (stepsLeft == 0) // In case there are no steps left by player, do as below.
                                {
                                    stepsLeft = 4; // Reset stepsLeft to 4 for next player's turn.
                                    if(playerTurn.compareTo("Gold") == 0) // If player turn is Gold, do as below.
                                    {
                                        playerTurn = "Silver"; // Change player turn.
                                        gb.updateMessage("Player: Gold have finished it's turn."); // Update message on UI.
                                    }
                                    else if (playerTurn.compareTo("Silver") == 0) // If player turn is Silver, do as below.
                                    {
                                        playerTurn = "Gold"; // Change player turn.
                                        gb.updateMessage("Player: Silver have finished it's turn."); // Update message on UI.
                                    }
                                    gb.updateStepsLeft(stepsLeft); // Updating steps left on UI.
                                    gb.updatePlayerTurn(playerTurn); // Update player turn on UI.
                                }
                                selectPossible = new String[8][8]; // Reset 2D array.
                                checkTrapBlocks(); // Check trap blocks for any elimination.
                                return true;
                            }
                            else // In case above is not true, do as below.
                            {
                                gb.updateMessage("Invalid position to push your enemy piece."); // Update message on UI.
                            }
                        }
                    }
                }
            }
        }
        else if(pullTouch && pullEndX > 0.0 && pullEndY > 0.0) // In case pullTouch is true and we have non 0 values for x and y, do as below.
        {
            pullTouch = false; // Setting pullTouch to false so that next touch will be for new move.
            for(int i=0; i<8; i++) // This loop handles rows.
            {
                if(pullStartY >= boxSize * i && pullStartY < boxSize * (i + 1)) // If match for y axis is found, do as below.
                {
                    for(int j = 0; j < 8; j++) // This loop handles columns.
                    {
                        if(pullStartX >= boxSize * j && pullStartX < boxSize * (j + 1)) // If match for x axis is found, do as below.
                        {
                            // In case enemy piece position is not null and distance of enemy piece is 1 block next to start position of moved piece, do as below.
                            if(piece[i][j] != null && piece[pullEndY][pullEndX] == null && (Math.abs(i-pullEndY) == 1 || Math.abs(j-pullEndX) == 1))
                            {
                                piece[pullEndY][pullEndX] = piece[i][j]; // Change position of enemy piece to new position.
                                piece[i][j] = null; // Set original position to null.
                                stepsLeft = stepsLeft - 1; // Decrementing steps left by 2 as push is also considered as 1 step.
                                gb.updateStepsLeft(stepsLeft); // Updating steps left on UI.
                                if(playerTurn.compareTo("Gold") == 0) // If player turn is Gold, do as below.
                                {
                                    gb.updateMessage("There are steps left to be moved by player: Gold."); // Update message on UI.
                                }
                                else // If player turn is Silver, do as below.
                                {
                                    gb.updateMessage("There are steps left to be moved by player: Silver."); // Update message on UI.
                                }
                                if (stepsLeft == 0) // In case there are no steps left by player, do as below.
                                {
                                    stepsLeft = 4; // Reset stepsLeft to 4 for next player's turn.
                                    if(playerTurn.compareTo("Gold") == 0) // If player turn is Gold, do as below.
                                    {
                                        playerTurn = "Silver"; // Change player turn.
                                        gb.updateMessage("Player: Gold have finished it's turn."); // Update message on UI.
                                    }
                                    else if (playerTurn.compareTo("Silver") == 0) // If player turn is Silver, do as below.
                                    {
                                        playerTurn = "Gold"; // Change player turn.
                                        gb.updateMessage("Player: Silver have finished it's turn."); // Update message on UI.
                                    }
                                    gb.updateStepsLeft(stepsLeft); // Updating steps left on UI.
                                    gb.updatePlayerTurn(playerTurn); // Update player turn on UI.
                                }
                                a.deleteLastRecord(); // Deleting last record in db table to make this move along with last one transactional.
                                checkTrapBlocks(); // Check trap blocks for any elimination.
                                return true;
                            }
                            else // In case piece selected is invalid, do as below.
                            {
                                gb.undoMove("InvalidPull"); // In case incorrect piece is selected, revert previous move as well.
                            }
                        }
                    }
                }
            }
        }
        selectPossible = new String[8][8]; // Reset 2D array. We are resetting here as we have dependent checks above in if conditions.
        return false; // In case none from above conditions are satisfied, return false.
    }
    // pieceMoveEnd method ends here.

    // Below method will return number of steps left in current turn.
    public int getStepsLeft()
    {
        return stepsLeft; // Return stepsLeft.
    }
    // getStepsLeft method ends here.

    // Below method will return Image name to be used for each data in piece 2D array.
    private Bitmap identifyPiece(int i, int j) {
        Bitmap b;
        String givenPiece = piece[i][j]; // Assigning piece of position i and j to givenPiece.
        if(givenPiece == null) // If givenPiece is null, return null.
            return null; // Returning null.
        if(givenPiece.compareTo("rabbit")==0) // If piece is rabbit in lower case, piece is silver rabbit.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.silverrabbit); // Create bitmap.
        else if(givenPiece.compareTo("RABBIT")==0) // If piece is rabbit in upper case, piece is gold rabbit.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.goldrabbit); // Create bitmap.
        else if(givenPiece.compareTo("cat")==0) // If piece is cat in lower case, piece is silver cat.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.silvercat); // Create bitmap.
        else if(givenPiece.compareTo("CAT")==0) // If piece is cat in upper case, piece is gold cat.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.goldcat); // Create bitmap.
        else if(givenPiece.compareTo("dog")==0) // If piece is dog in lower case, piece is silver dog.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.silverdog); // Create bitmap.
        else if(givenPiece.compareTo("DOG")==0) // If piece is dog in upper case, piece is gold dog.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.golddog); // Create bitmap.
        else if(givenPiece.compareTo("horse")==0) // If piece is horse in lower case, piece is silver horse.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.silverhorse); // Create bitmap.
        else if(givenPiece.compareTo("HORSE")==0) // If piece is horse in upper case, piece is gold horse.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.goldhorse); // Create bitmap.
        else if(givenPiece.compareTo("camel")==0) // If piece is camel in lower case, piece is silver camel.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.silvercamel); // Create bitmap.
        else if(givenPiece.compareTo("CAMEL")==0) // If piece is camel in upper case, piece is gold camel.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.goldcamel); // Create bitmap.
        else if(givenPiece.compareTo("elephant")==0) // If piece is elephant in lower case, piece is silver elephant.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.silverelephant); // Create bitmap.
        else if(givenPiece.compareTo("ELEPHANT")==0) // If piece is elephant in lower case, piece is gold elephant.
            b = BitmapFactory.decodeResource(getResources(),R.drawable.goldelephant); // Create bitmap.
        else
            b = null;
        return b; // Return bitmap.
    }
    // identifyPiece method ends here.

    // Below method will be called to highlight all the possible moves.
    private void highlightPossibleMoves() {
        // Declaration of required variables starts here.
        int i=0, j=0; // Initializing variables to 0.
        boolean checkOtherConditions = false; // This will depend on type of piece we are referring to.
        // Declaration of required variables ends here.

        if(touch) // In case touch is true, i.e. if touch is first touch of move, do as below.
        {
            i = pieceStartY; // Assign value of y.
            j = pieceStartX; // Assign value of x.
            checkOtherConditions = true; // Set this to true as we need to check all possible combinations.
            if(piece[i][j].compareTo("RABBIT") != 0 && (i+1)<8 && isWeakOrNull(i+1,j, true)) // Checking in +ve y direction for all possible moves.
            {
                selectPossible[i+1][j] = "possible"; // If true, set selectPossible array to "possible".
            }
            if(piece[i][j].compareTo("rabbit") != 0 && (i-1)>=0 && isWeakOrNull(i-1,j, true)) // Checking in -ve y direction for all possible moves.
            {
                selectPossible[i-1][j] = "possible"; // If true, set selectPossible array to "possible".
            }
        }
        else if(pushTouch) // In case touch is push touch, do as below. Here we only require to check for null places on board.
        {
            selectPossible = new String[8][8]; // Initialize 2D array.
            selectPossible[pieceStartY][pieceStartX] = "selected"; // Plot original piece.
            selectPossible[pushStartY][pushStartX] = "selected"; // Plot 2nd piece we are going to push.
            i = pushStartY; // Assign value of y.
            j = pushStartX; // Assign value of x.
            checkOtherConditions = false; // Set this to false as we need to check only for null positions.
            if((i+1)<8 && isWeakOrNull(i+1,j, false)) // Checking in +ve y direction for all possible moves.
            {
                selectPossible[i+1][j] = "possible"; // If true, set selectPossible array to "possible".
            }
            if((i-1)>=0 && isWeakOrNull(i-1,j, false)) // Checking in -ve y direction for all possible moves.
            {
                selectPossible[i-1][j] = "possible"; // If true, set selectPossible array to "possible".
            }
        }
        if((j+1)<8 && isWeakOrNull(i,j+1, checkOtherConditions)) // Checking in +ve x direction for all possible moves.
        {
            selectPossible[i][j+1] = "possible"; // If true, set selectPossible array to "possible".
        }
        if((j-1)>=0 && isWeakOrNull(i,j-1, checkOtherConditions)) // Checking in -ve x direction for all possible moves.
        {
            selectPossible[i][j-1] = "possible"; // If true, set selectPossible array to "possible".
        }
    }
    // highlightPossibleMoves method ends here.

    // Below method will be called to set pull to true.
    public void setPull() {
        pullTouch = true; // Setting pullTouch to true so that next step will be identifying piece to be pulled.
        gb.updateMessage("Select enemy piece you want to pull.");
    }
    // setPull method ends here.

    // Below method will be called to check if passed piece is weak than selected piece.
    private boolean isWeakOrNull(int i, int j, boolean checkOthers) {
        if(piece[i][j] == null) // In case of any touch including push or pull, this condition will always be checked.
        {
            return true; // Return true if above satisfies.
        }
        else if(checkOthers && playerTurn.compareTo("Gold")==0 && Character.isLowerCase(piece[i][j].charAt(0))) // In case of push or pull, this will not be checked.
        {
            return pieceWeight.get(piece[i][j]) < pieceWeight.get(piece[pieceStartY][pieceStartX].toLowerCase()); // This will be return true or false based on values in HM.
        }
        else if(checkOthers && playerTurn.compareTo("Silver")==0 && Character.isUpperCase(piece[i][j].charAt(0))) // In case of push or pull, this will not be checked.
        {
            return pieceWeight.get(piece[i][j].toLowerCase()) < pieceWeight.get(piece[pieceStartY][pieceStartX]); // This will be return true or false based on values in HM.
        }
        return false; // In case none from above satisfies, return false.
    }
    // isWeakOrNull method ends here.

    // Below method will be called to check if there are any stronger piece near currently selected piece.
    public boolean nearStrongerPiece() {
        boolean decision = false;
        int i = pieceStartY;
        int j = pieceStartX;
        if(playerTurn.compareTo("Gold") == 0 && piece[i][j] != null) // In case player turn is Gold, do as below.
        {
            if(i+1<8 && piece[i+1][j] != null && Character.isLowerCase(piece[i+1][j].charAt(0)) && pieceWeight.get(piece[i][j].toLowerCase())<pieceWeight.get(piece[i+1][j]))
            {
                decision = true; // Set decision to true.
            }
            if(i-1>=0 && piece[i-1][j] != null && Character.isLowerCase(piece[i-1][j].charAt(0)) && pieceWeight.get(piece[i][j].toLowerCase())<pieceWeight.get(piece[i-1][j]))
            {
                decision = true; // Set decision to true.
            }
            if(j+1<8 && piece[i][j+1] != null && Character.isLowerCase(piece[i][j+1].charAt(0)) && pieceWeight.get(piece[i][j].toLowerCase())<pieceWeight.get(piece[i][j+1]))
            {
                decision = true; // Set decision to true.
            }
            if(j-1>=0 && piece[i][j-1] != null && Character.isLowerCase(piece[i][j-1].charAt(0)) && pieceWeight.get(piece[i][j].toLowerCase())<pieceWeight.get(piece[i][j-1]))
            {
                decision = true; // Set decision to true.
            }
        }
        else if(playerTurn.compareTo("Silver") == 0 && piece[i][j] != null) // In case player turn is Silver, do as below.
        {
            if(i+1<8 && piece[i+1][j] != null && Character.isUpperCase(piece[i+1][j].charAt(0)) && pieceWeight.get(piece[i][j])<pieceWeight.get(piece[i+1][j].toLowerCase()))
            {
                decision = true; // Set decision to true.
            }
            if(i-1>=0 && piece[i-1][j] != null && Character.isUpperCase(piece[i-1][j].charAt(0)) && pieceWeight.get(piece[i][j])<pieceWeight.get(piece[i-1][j].toLowerCase()))
            {
                decision = true; // Set decision to true.
            }
            if(j+1<8 && piece[i][j+1] != null && Character.isUpperCase(piece[i][j+1].charAt(0)) && pieceWeight.get(piece[i][j])<pieceWeight.get(piece[i][j+1].toLowerCase()))
            {
                decision = true; // Set decision to true.
            }
            if(j-1>=0 && piece[i][j-1] != null && Character.isUpperCase(piece[i][j-1].charAt(0)) && pieceWeight.get(piece[i][j])<pieceWeight.get(piece[i][j-1].toLowerCase()))
            {
                decision = true; // Set decision to true.
            }
        }
        return decision; // Return decision. Here, decision will be false in case none from above conditions are satisfied.
    }
    // nearStrongerPiece method ends here.

    // Below method will be called to check if there are any stronger piece near currently selected piece.
    public boolean nearFriendlyPiece() {
        boolean decision = false;
        int i = pieceStartY;
        int j = pieceStartX;
        if(playerTurn.compareTo("Gold") == 0 && piece[i][j] != null) // In case player turn is Gold, do as below.
        {
            if(i+1<8 && piece[i+1][j] != null && Character.isUpperCase(piece[i+1][j].charAt(0)))
            {
                decision = true; // Set decision to true.
            }
            if(i-1>=0 && piece[i-1][j] != null && Character.isUpperCase(piece[i-1][j].charAt(0)))
            {
                decision = true; // Set decision to true.
            }
            if(j+1<8 && piece[i][j+1] != null && Character.isUpperCase(piece[i][j+1].charAt(0)))
            {
                decision = true; // Set decision to true.
            }
            if(j-1>=0 && piece[i][j-1] != null && Character.isUpperCase(piece[i][j-1].charAt(0)))
            {
                decision = true; // Set decision to true.
            }
        }
        else if(playerTurn.compareTo("Silver") == 0 && piece[i][j] != null) // In case player turn is Silver, do as below.
        {
            if(i+1<8 && piece[i+1][j] != null && Character.isLowerCase(piece[i+1][j].charAt(0)))
            {
                decision = true; // Set decision to true.
            }
            if(i-1>=0 && piece[i-1][j] != null && Character.isLowerCase(piece[i-1][j].charAt(0)))
            {
                decision = true; // Set decision to true.
            }
            if(j+1<8 && piece[i][j+1] != null && Character.isLowerCase(piece[i][j+1].charAt(0)))
            {
                decision = true; // Set decision to true.
            }
            if(j-1>=0 && piece[i][j-1] != null && Character.isLowerCase(piece[i][j-1].charAt(0)))
            {
                decision = true; // Set decision to true.
            }
        }
        return decision; // Return decision. Here, decision will be false in case none from above conditions are satisfied.
    }
    // nearFriendlyPiece method ends here.

    // Below method will be called to check if there are any weaker enemy piece near currently selected piece.
    public boolean nearWeakerEnemy() {
        boolean decision = false;
        int i = pieceStartY;
        int j = pieceStartX;
        if(playerTurn.compareTo("Gold") == 0 && piece[i][j] != null) // In case player turn is Gold, do as below.
        {
            if(i+1<8 && piece[i+1][j] != null && Character.isLowerCase(piece[i+1][j].charAt(0)) && pieceWeight.get(piece[i][j].toLowerCase())>pieceWeight.get(piece[i+1][j]))
            {
                enemyWeakPieceAdjacentCount = enemyWeakPieceAdjacentCount+1; // Increasing variable count to identify how many weaker piece of enemy are adjacent to current piece.
                decision = true; // Set decision to true.
            }
            if(i-1>=0 && piece[i-1][j] != null && Character.isLowerCase(piece[i-1][j].charAt(0)) && pieceWeight.get(piece[i][j].toLowerCase())>pieceWeight.get(piece[i-1][j]))
            {
                enemyWeakPieceAdjacentCount = enemyWeakPieceAdjacentCount+1; // Increasing variable count to identify how many weaker piece of enemy are adjacent to current piece.
                decision = true; // Set decision to true.
            }
            if(j+1<8 && piece[i][j+1] != null && Character.isLowerCase(piece[i][j+1].charAt(0)) && pieceWeight.get(piece[i][j].toLowerCase())>pieceWeight.get(piece[i][j+1]))
            {
                enemyWeakPieceAdjacentCount = enemyWeakPieceAdjacentCount+1; // Increasing variable count to identify how many weaker piece of enemy are adjacent to current piece.
                decision = true; // Set decision to true.
            }
            if(j-1>=0 && piece[i][j-1] != null && Character.isLowerCase(piece[i][j-1].charAt(0)) && pieceWeight.get(piece[i][j].toLowerCase())>pieceWeight.get(piece[i][j-1]))
            {
                enemyWeakPieceAdjacentCount = enemyWeakPieceAdjacentCount+1; // Increasing variable count to identify how many weaker piece of enemy are adjacent to current piece.
                decision = true; // Set decision to true.
            }
        }
        else if(playerTurn.compareTo("Silver") == 0 && piece[i][j] != null) // In case player turn is Silver, do as below.
        {
            if(i+1<8 && piece[i+1][j] != null && Character.isUpperCase(piece[i+1][j].charAt(0)) && pieceWeight.get(piece[i][j])>pieceWeight.get(piece[i+1][j].toLowerCase()))
            {
                enemyWeakPieceAdjacentCount = enemyWeakPieceAdjacentCount+1; // Increasing variable count to identify how many weaker piece of enemy are adjacent to current piece.
                decision = true; // Set decision to true.
            }
            if(i-1>=0 && piece[i-1][j] != null && Character.isUpperCase(piece[i-1][j].charAt(0)) && pieceWeight.get(piece[i][j])>pieceWeight.get(piece[i-1][j].toLowerCase()))
            {
                enemyWeakPieceAdjacentCount = enemyWeakPieceAdjacentCount+1; // Increasing variable count to identify how many weaker piece of enemy are adjacent to current piece.
                decision = true; // Set decision to true.
            }
            if(j+1<8 && piece[i][j+1] != null && Character.isUpperCase(piece[i][j+1].charAt(0)) && pieceWeight.get(piece[i][j])>pieceWeight.get(piece[i][j+1].toLowerCase()))
            {
                enemyWeakPieceAdjacentCount = enemyWeakPieceAdjacentCount+1; // Increasing variable count to identify how many weaker piece of enemy are adjacent to current piece.
                decision = true; // Set decision to true.
            }
            if(j-1>=0 && piece[i][j-1] != null && Character.isUpperCase(piece[i][j-1].charAt(0)) && pieceWeight.get(piece[i][j])>pieceWeight.get(piece[i][j-1].toLowerCase()))
            {
                enemyWeakPieceAdjacentCount = enemyWeakPieceAdjacentCount+1; // Increasing variable count to identify how many weaker piece of enemy are adjacent to current piece.
                decision = true; // Set decision to true.
            }
        }
        return decision; // Return decision. Here, decision will be false in case none from above conditions are satisfied.
    }
    // nearWeakerEnemy method ends here.

    // Below method will be called when player clicks on Finish Move button from UI.
    public void confirmFinishTurn() {
        if(stepsLeft < 4) // If at least 1 step is moved by player, do as below.
        {
            stepsLeft = 4; // Reset stepsLeft.
            selectPossible = new String[8][8]; // Reset 2D selectPossible array.
            touch = false; // Reset touch to false.
            if(playerTurn.compareTo("Gold") == 0) // In case player turn is Gold, do as below.
            {
                playerTurn = "Silver"; // Set playerTurn to Silver.
                gb.updateMessage("Player: Gold have finished it's turn."); // Update message on UI.
            }
            else // In case player turn is Silver, do as below.
            {
                playerTurn = "Gold"; // Set playerTurn to Gold.
                gb.updateMessage("Player: Silver have finished it's turn."); // Update message on UI.
            }
            gb.updatePlayerTurn(playerTurn); // Updating player turn on UI.
            gb.updateStepsLeft(stepsLeft);
            checkTrapBlocks(); // Calling checkTrapBlocks to check further dependent functions.
        }
        else // In case no steps are moved by player, refuse to finish move.
        {
            gb.updateMessage("Please move at least 1 step before finishing the turn."); // Update message on UI.
        }
    }
    // confirmFinishTurn method ends here.

    // Below method will be called to check traps.
    private void checkTrapBlocks() {
        for(int i=1; i<=8; i++) // This loop handles rows.
        {
            if(i%3 == 0) // In case i%3 is 0 i.e. if trap box's row is looped, do as below.
            {
                for(int j=1; j<=8; j++) // This loop handles columns.
                {
                    int y = i-1; // Setting y position.
                    int x = j-1; // Setting x position.
                    if(j%3 == 0 && piece[y][x] != null) // If j%3 is 0 i.e. if its a trap box, and if it's not null, do as below.
                    {
                        if(Character.isUpperCase(piece[y][x].charAt(0))) // If the piece is of player Gold, do as below.
                        {
                            boolean makeNull = true; // Initializing makeNull to true. In case this remains true, a piece from trap block will be removed.
                            if(piece[y-1][x] != null && Character.isUpperCase(piece[y-1][x].charAt(0))) // If Gold piece is present above block of trap box with gold piece in it, do as below.
                            {
                                makeNull = false; // Make variable false.
                            }
                            if(piece[y+1][x] != null && Character.isUpperCase(piece[y+1][x].charAt(0))) // If Gold piece is present below block of trap box with gold piece in it, do as below.
                            {
                                makeNull = false; // Make variable false.
                            }
                            if(piece[y][x-1] != null && Character.isUpperCase(piece[y][x-1].charAt(0))) // If Gold piece is present left of trap box with gold piece in it, do as below.
                            {
                                makeNull = false; // Make variable false.
                            }
                            if(piece[y][x+1] != null && Character.isUpperCase(piece[y][x+1].charAt(0))) // If Gold piece is present right of trap box with gold piece in it, do as below.
                            {
                                makeNull = false; // Make variable false.
                            }
                            if(makeNull) // If value is true, do as below.
                            {
                                if(piece[y][x].compareTo("RABBIT") == 0) // Checking if piece we are eliminating is rabbit? If yes, do as below.
                                    goldRabbit = goldRabbit - 1; // Decrement rabbit count.
                                piece[y][x] = null; // Make trap box null.
                            }
                        }
                        else // If the piece is of player Silver, do as below.
                        {
                            boolean makeNull = true; // Initializing makeNull to true. In case this remains true, a piece from trap block will be removed.
                            if(piece[y-1][x] != null && Character.isLowerCase(piece[y-1][x].charAt(0))) // If Silver piece is present above block of trap box with silver piece in it, do as below.
                            {
                                makeNull = false; // Make variable false.
                            }
                            if(piece[y+1][x] != null && Character.isLowerCase(piece[y+1][x].charAt(0))) // If Silver piece is present below block of trap box with silver piece in it, do as below.
                            {
                                makeNull = false; // Make variable false.
                            }
                            if(piece[y][x-1] != null && Character.isLowerCase(piece[y][x-1].charAt(0))) // If Silver piece is present left of trap box with silver piece in it, do as below.
                            {
                                makeNull = false; // Make variable false.
                            }
                            if(piece[y][x+1] != null && Character.isLowerCase(piece[y][x+1].charAt(0))) // If Silver piece is present right of trap box with silver piece in it, do as below.
                            {
                                makeNull = false; // Make variable false.
                            }
                            if(makeNull) // If value is true, do as below.
                            {
                                if(piece[y][x].compareTo("rabbit") == 0) // Checking if piece we are eliminating is rabbit? If yes, do as below.
                                    silverRabbit = silverRabbit - 1; // Decrement rabbit count.
                                piece[y][x] = null; // Make trap box null.
                            }
                        }
                    }
                }
            }
        }
        saveGameData(); // Calling saveData method to save all the data of game in db.
        checkGameStatus(); // Calling to check if player have won the game.
    }
    // checkTraps method ends here.

    // Below method will be called to write date in DB table.
    private void saveGameData() {
        if(a.storeData(playerTurn,stepsLeft,piece)) // Passing correct parameters in the function to save game.
        {
            Log.i("Save Game","Game data successfully saved.");
        }
        else
        {
            Log.i("Save Game","Error occurred while saving Game data.");
        }
    }
    // saveDate ends here.

    // Below method will be called after 2nd touch and player moves successfully.
    private void checkGameStatus() {
        String winnerMessage = ""; // This will contain reason why player is winner.
        boolean playerHaveMoves = false; // This will decide if there are any possible moves or not.
        for(int i=0; i<8; i++) // This loop will handle columns.
        {
            if(piece[0][i] != null && piece[0][i].compareTo("RABBIT") == 0) // In case rabbit of player gold reaches other end of board, do as below.
            {
                winner = "Gold"; // Declare winner.
                winnerMessage = "Rabbit of "+winner+" has reached the other end and hence, "+winner+" has won this game."; // Setting winnerMessage to be displayed on UI later.
                winnerDecided = true; // Set variable to true.
                break; // Break this loop.
            }
            else if(piece[7][i] != null && piece[7][i].compareTo("rabbit") == 0) // In case rabbit of player silver reaches other end of board, do as below.
            {
                winner = "Silver"; // Declare winner.
                winnerMessage = "Rabbit of "+winner+" has reached the other end and hence, "+winner+" has won this game."; // Setting winnerMessage to be displayed on UI later.
                winnerDecided = true; // Set variable to true.
                break; // Break this loop.
            }
        }
        if(!winnerDecided && goldRabbit == 0) // In case all the rabbits of player Gold are removed from board, do as below.
        {
            winner = "Silver"; // Updating the winner in global variable.
            winnerMessage = "There are no Gold rabbits left on the board and hence, "+winner+" has won this game."; // Setting winnerMessage to be displayed on UI later.
            winnerDecided = true; // Set variable to true.
        }
        else if(!winnerDecided && silverRabbit == 0) // In case all the rabbits of player Silver are removed from board, do as below.
        {
            winner = "Gold"; // Updating the winner in global variable.
            winnerMessage = "There are no Silver rabbits left on the board and hence, "+winner+" has won this game."; // Setting winnerMessage to be displayed on UI later.
            winnerDecided = true; // Set variable to true.
        }
        if(!winnerDecided) // In case winner is not found using above 2 logics, do as below.
        {
            for(int i=0; i<8; i++) // This loop handles rows.
            {
                for(int j=0; j<8; j++) // This loop handles columns.
                {
                    if(playerTurn.compareTo("Gold") == 0 && piece[i][j] != null && Character.isUpperCase(piece[i][j].charAt(0))) // Checking for player Gold.
                    {
                        if(haveMoves(i,j,true)) // If player still have moves even for 1 piece, do as below.
                        {
                            playerHaveMoves = true; // Make variable true.
                            break; // Break the loop.
                        }
                    }
                    else if(playerTurn.compareTo("Silver") == 0 && piece[i][j] != null && Character.isLowerCase(piece[i][j].charAt(0))) // Checking for player Silver.
                    {
                        if(haveMoves(i,j,true)) // If player still have moves even for 1 piece, do as below.
                        {
                            playerHaveMoves = true; // Make variable true.
                            break; // Break the loop.
                        }
                    }
                }
            }
            if(!playerHaveMoves) // If player does not have any more possible moves, do as below.
            {
                if(playerTurn.compareTo("Gold")==0)
                    winner = "Silver"; // Updating the winner in global variable.
                else if(playerTurn.compareTo("Silver")==0)
                    winner = "Gold"; // Updating the winner in global variable.
                winnerMessage = "There are no steps left for "+playerTurn+" to move and hence, "+winner+" has won this game."; // Setting winnerMessage to be displayed on UI later.
                winnerDecided = true; // Set variable to true.
            }
        }
        if(winnerDecided) // In case any player wins by sending rabbit in other side, game ends and no more moves are possible.
        {
            stepsLeft = 0;
            gb.updateStepsLeft(0); // Steps will be 0.
            gb.updateMessage(winnerMessage); // Update message on UI.
            a.truncateDBTable(); // Deleting all data of last game.
            gb.finishGame(winner); // Calling finish Game function along with winner as parameter.
        }
    }
    // checkGameStatus method ends here.

    // Below method will be called to highlight all the possible moves.
    private boolean haveMoves(int i, int j, boolean check)
    {
        boolean isIt = true;
        if(piece[i][j].compareTo("RABBIT") != 0 && (i+1)<8 && (checkStrongEnemy(i,j,i+1,j) && checkCanMove(i, j, i + 1, j, check))) // Checking in +ve y direction for all possible moves.
        {
            isIt = false; // Setting variable to true.
        }
        if(piece[i][j].compareTo("rabbit") != 0 && (i-1)>=0 && (checkStrongEnemy(i,j,i-1,j) && checkCanMove(i, j, i - 1, j, check))) // Checking in -ve y direction for all possible moves.
        {
            isIt = false; // Setting variable to true.
        }
        if((j+1)<8 && (checkStrongEnemy(i,j,i,j+1) && checkCanMove(i, j, i, j + 1, check))) // Checking in +ve x direction for all possible moves.
        {
            isIt = false; // Setting variable to true.
        }
        if((j-1)>=0 && (checkStrongEnemy(i,j,i,j-1) && checkCanMove(i, j, i, j - 1, check))) // Checking in -ve x direction for all possible moves.
        {
            isIt = false; // Setting variable to true.
        }
        return isIt; // Returning final result after above calculations.
    }
    // haveMoves method ends here.

    // Below method will be called to check if near blocks are null or have weak enemy pieces.
    public boolean checkCanMove(int i, int j, int k, int l, boolean check) {
        if(piece[k][l] == null)
            return false;
        if(check && playerTurn.compareTo("Gold") == 0 && piece[k][l] != null && Character.isLowerCase(piece[k][l].charAt(0)) && pieceWeight.get(piece[i][j].toLowerCase())>pieceWeight.get(piece[k][l]) && haveMoves(k,l,false))
            return false;
        else if(check && playerTurn.compareTo("Silver") == 0 && piece[k][l] != null && Character.isUpperCase(piece[k][l].charAt(0)) && pieceWeight.get(piece[i][j])>pieceWeight.get(piece[k][l].toLowerCase()) && haveMoves(k,l,false))
            return false;
        else
            return true;
    }
    // checkWeakOrNull method ends here.

    // Below method will be called to check if near blocks are null or have weak enemy pieces.
    public boolean checkStrongEnemy(int i, int j, int k, int l) {
        if(playerTurn.compareTo("Gold") == 0 && piece[k][l] != null && Character.isLowerCase(piece[k][l].charAt(0)) && pieceWeight.get(piece[i][j].toLowerCase())<pieceWeight.get(piece[k][l]))
            return true;
        else if(playerTurn.compareTo("Silver") == 0 && piece[k][l] != null && Character.isUpperCase(piece[k][l].charAt(0)) && pieceWeight.get(piece[i][j])<pieceWeight.get(piece[k][l].toLowerCase()))
            return true;
        else
            return false;
    }
    // checkWeakOrNull method ends here.

    // Below method will be used to set winnerDecided variable.
    public void setWinnerDecided() {
        winner = ""; // Resetting winner.
        winnerDecided = false; // Resetting winnerDecided.
        goldRabbit = 8; // Resetting gold rabbit count.
        silverRabbit = 8; // Resetting silver rabbit count.
    }
    // setWinnerDecided method ends here.

    // Below method will be used to get winnerDecided variable.
    public boolean getWinnerDecided() {
        return !winnerDecided; // Returning winnerDecided.
    }
    // setWinnerDecided method ends here.
}
