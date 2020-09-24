package com.example.tictactoeapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    /* (3 X 3) 2D array representing the game board - place 1 in spaces claimed by Player 1,
     * 2 in places claimed by Player 2
     *
     * Empty spaces represented by 0's
     *
     * Initialized in initializeBoardRep method
     */
    private var boardRepresention = arrayOf<Array<Int>>()

    private var playerOneTurn = true

    private var playerOnePoints = 0
    private var playerTwoPoints = 0

    /* Use lateinit variables to ensure initialization occurs after setContentView() method is called
     * (avoid NPE)
     */
    private lateinit var textViewPlayer1: TextView
    private lateinit var textViewPlayer2: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize lateinit variables with text views
        textViewPlayer1 = findViewById<TextView>(R.id.textViewP1)
        textViewPlayer2 = findViewById<TextView>(R.id.textViewP2)

        // Color textViewP1 to indicate current turn
        textViewPlayer1.setTextColor(Color.GREEN)

        generateBoardRep()
    }


    /* Generate a two-dimensional array that represents the game board; elements are initialized as
     * zeros to represent empty spaces.
     */
    private fun generateBoardRep(){
        for (i in 0..2){
            var array = arrayOf<Int>()
            for (j in 0..2) {
                array += 0
            }
            boardRepresention += array
        }
    }


    // Reset board representation (to all zeros) after completion of round/game
    private fun resetBoardRep(){
        for (i in 0..2){
            for (j in 0..2){
                boardRepresention[i][j] = 0
            }
        }
    }


    /* Executes each time one of the empty spaces (spaces on game board are implemented as buttons)
     * is clicked by the user during their turn.
     */
    fun buttonClick(view: View){
        /* The code within this function is only executed if the player selects an enabled button
         * while it is their turn
         */
        if (playerOneTurn) {
            val selectButton = view as Button
            var cellID = IntArray(2)
            /* Assign two values to the cellID array according to which button is clicked; this is
             * done to associate the clicked button with its corresponding row and column values in
             * the boardRepresentation 2D array.
             */
            when (selectButton.id) {
                R.id.button0 -> { cellID[0] = 0; cellID[1] = 0 }
                R.id.button1 -> { cellID[0] = 0; cellID[1] = 1 }
                R.id.button2 -> { cellID[0] = 0; cellID[1] = 2 }
                R.id.button3 -> { cellID[0] = 1; cellID[1] = 0 }
                R.id.button4 -> { cellID[0] = 1; cellID[1] = 1 }
                R.id.button5 -> { cellID[0] = 1; cellID[1] = 2 }
                R.id.button6 -> { cellID[0] = 2; cellID[1] = 0 }
                R.id.button7 -> { cellID[0] = 2; cellID[1] = 1 }
                R.id.button8 -> { cellID[0] = 2; cellID[1] = 2 }
            }
            /* Set button text as the appropriate symbol and change the button color to better
             * indicate that the space has been taken
             */
            selectButton.text = "X"
            selectButton.setBackgroundColor(Color.GREEN)
            // Disable button to prevent it from being selected again
            selectButton.isEnabled = false
            // Indicate that the spot has been selected by the human player in boardRepresentation
            val xCoord = cellID[0]
            val yCoord = cellID[1]
            boardRepresention[xCoord][yCoord] = 1
            /* If the game is over (by win or draw), the postMoveActions() function will announce
             * the winner (with a Toast), disable buttons, and update points. Otherwise, it will
             * return null, indicating that the AI should take its turn.
             */
            var gameStatus = postMoveActions()
            if (gameStatus == null) {
                playerOneTurn = false
                aiMove()
                gameStatus = postMoveActions()
                if (gameStatus == null) { playerOneTurn = true }
            }
        }
    }


    /* Function checks for all possible winning patterns and returns a value of 1 or -1 to indicate
     * the winning player (Player One and PLayer Two, respectively). If a draw has occurred (all
     * spots have been taken without any player achieving a winning combination), the function
     * returns 0.
     */
    private fun checkForWin(): Int? {

        var winner: Int? = null
        var winStatus: Int? = null
        var i = 0

        /* Count opens spaces - if there are zero open spaces and no player has won the game,
         * a draw has occurred
         */
        var openSpacesCount = 0
        for (i in 0..2){
            for (j in 0..2){
                if (boardRepresention[i][j] == 0){
                    openSpacesCount++
                }
            }
        }

        // Check row i and column i for a winning pattern, where 0 <= i <= 3
        while (i < 3){
            // Check rows for win
            if (boardRepresention[i][0] == boardRepresention[i][1] &&
                boardRepresention[i][0] == boardRepresention[i][2] &&
                boardRepresention[i][0] != 0
            ) {
                winner = boardRepresention[i][0]
            }
            //  Check columns for win
            if (boardRepresention[0][i] == boardRepresention[1][i] &&
                boardRepresention[0][i] == boardRepresention[2][i] &&
                boardRepresention[0][i] != 0
            ) {
                winner = boardRepresention[0][i]
            }

            i++
        }

        // Check left-to-right diagonal
        if (boardRepresention[0][0] == boardRepresention[1][1] &&
            boardRepresention[0][0] == boardRepresention[2][2] &&
            boardRepresention[0][0] != 0
        ) {
            winner = boardRepresention[0][0]
        }
        // Check right-to-left diagonal
        if (boardRepresention[0][2] == boardRepresention[1][1] &&
            boardRepresention[0][2] == boardRepresention[2][0] &&
            boardRepresention[0][2] != 0
        ) {
            winner = boardRepresention[0][2]
        }

        when {
            winner == 1 -> {
                winStatus = 1
            }
            winner == 2 -> {
                winStatus = -1
            }
            openSpacesCount == 0 -> {
                winStatus = 0
            }
        }
        return winStatus
    }


    /* Function carries out the actions required after the game space is checked for a win/draw.
     * If the game has not yet finished (no win or draw), then the colors of the player text views
     * (displaying "Player #" and score) are changed to indicate which player's turn it is.
     * Otherwise, a Toast is shown indicating which player has won or if a draw has occurred,
     * player points are updated, and the buttons representing board spaces are disabled.
     */
    private fun postMoveActions(): Int?{
        // Check the status of the game using the checkForWin() function
        var winStatus: Int? = checkForWin()
        /* If the game has not ended, change the color of the player text views to indicate which
         * player's turn it is
         */
        if (winStatus == null) {
            if (playerOneTurn) {
                textViewPlayer1.setTextColor(Color.BLACK)
                textViewPlayer2.setTextColor(Color.CYAN)
            } else {
                textViewPlayer1.setTextColor(Color.GREEN)
                textViewPlayer2.setTextColor(Color.BLACK)
            }
        /* If a player has won, show a Toast message declaring the winner, update the tally of points, and
         * disable all game tiles (spaces) so that they cannot be altered
         */
        } else if (winStatus == 1) {
            playerOnePoints++
            Toast.makeText(this, "PLAYER ONE WINS!", Toast.LENGTH_SHORT).show()
            updatePointsText()
            disableTiles()
        } else if (winStatus == -1) {
            playerTwoPoints++
            Toast.makeText(this, "PLAYER TWO WINS!", Toast.LENGTH_SHORT).show()
            updatePointsText()
            disableTiles()
        /* If there is a draw, show a Toast message declaring such and disable all game tiles (spaces)
         * so that they cannot be altered
         */
        } else if (winStatus == 0) {
            Toast.makeText(this, "DRAW...", Toast.LENGTH_SHORT).show()
            disableTiles()
        }
        return winStatus
    }


    /** MINIMAX ARTIFICIAL INTELLIGENCE ALGORITHM that enables the AI player to select the optimal
     * move at each stage of the game.
     *
     * One player is considered to be the maximizing player, while the other is the minimizing
     * player. This aligns with the checkForWin() function's process of returning 1 if Player One
     * wins (maximizing player) and -1 if Player Two wins (minimizing player) (and 0 (neutral) for
     * draw).
     *
     * The function is recursive, with the base case being a non-null winStatus, indicating that
     * the "game" being evaluated has ended. *I use quotations because this does not necessarily
     * describe the game being played by the user, but one of the games generated by the minimax
     * algorithm's evaluations*
     *
     * If the base case is not met, then one of two code blocks is executed, depending on whether
     * the maximizingPlayer argument is true or false, which switch back and forth as the function
     * is called recursively.
     *
     * *The minimizing player is described first, as the AI in this implementation is the minimizing
     * player*
     * The bestScore variable is set to the minimum value of Int, the first open space in the game
     * board is selected, and the function is called recursively, with true being passed in as the,
     * value for the maximizingPlayer parameter. This then causes the maximizingPlayer == true
     * code block to be executed for this recursive call. Therefore, the next move(s) evaluated by the
     * algorithm will be those of the maximizing player (user), and the recursive calls of this
     * function call will cause the moves of the minimizing player to be evaluated. This back-and-forth
     * execution mimics the turns of the game and will end once the base case is met, indicating
     * that this "game" has concluded, and  the resulting winStatus value will be passed back up the
     * function calls.
     *
     * The returned score is compared to the current best score. Given that -1 represents the
     * minimizing player (AI) winning, a lesser number is evaluated as better than a greater one.
     * If lesser, that score is set as the new best score. Once this process has been carried out
     * for all empty spaces on the game board, the best (minimum) score is returned.
     *
     * These "games" are played for every possible game configuration starting from the current
     * configuration of the game being played by the user and the AI.
     *
     * The bigger-picture outcome of this process is that the first call(s) to the
     * minimax() function (found in the aiMove() function) will return scores that will be used to
     * determine the optimal move among all open spaces in the game board for the current
     * configuration of the game, where winning is the most optimal and a draw is optimal to losing.
     * The AI plays every possible game starting from a given configuration and only plays the
     * moves that it knows will lead to a win, or at worst, a draw.
     **/
    private fun minimax(boardRep: Array<Array<Int>>, depth: Int, maximizingPlayer: Boolean): Int {
        var winStatus = checkForWin()

        // Base case - evaluated game has ended
        if (winStatus != null) {
            return winStatus
        }
        if (maximizingPlayer){
            var bestScore = Int.MIN_VALUE
            // Evaluate all possible games for every empty space on the game board
            for (i in 0..2){
                for (j in 0..2){
                    if (boardRep[i][j] == 0){
                        boardRep[i][j] = 1
                        // Notice that maximizingPlayer value is switched to false - representing
                        // a change in turns for the game being evaluated
                        var score = minimax(boardRep, depth+1, false)
                        boardRep[i][j] = 0
                        if (score > bestScore){
                            bestScore = score
                        }
                    }
                }
            }
            return bestScore
        } else {
            // AI is the minimizing player
            var bestScore = Int.MAX_VALUE
            // Evaluate all possible games for every empty space on the game board
            for (i in 0..2){
                for (j in 0..2){
                    if (boardRep[i][j] == 0){
                        boardRep[i][j] = 2
                        // Notice that maximizingPlayer value is switched to false - representing
                        // a change in turns for the game being evaluated
                        var score = minimax(boardRep, depth+1, true)
                        boardRep[i][j] = 0
                        if (score < bestScore){
                            bestScore = score
                        }
                    }
                }
            }
            return bestScore
        }
    }


    /* Function uses the minimax() function to select the optimal move, then executes that optimal
     * move, carrying out the move of the AI player
     */
    private fun aiMove(){
        var bestScore = Int.MAX_VALUE
        var bestMove: IntArray = intArrayOf(0, 0)
        /* Call the minimax() function for all open spaces and find the one that returns the optimal
         * (smallest) value - this minimum value represents the best possible move given the board's
         * current configuration
         */
        for (i in 0..2){
            for (j in 0..2){
                if (boardRepresention[i][j] == 0){
                    boardRepresention[i][j] = 2
                    var score = minimax(boardRepresention, 0, true)
                    boardRepresention[i][j] = 0
                    if (score < bestScore){
                        bestScore = score
                        bestMove[0] = i
                        bestMove[1] = j
                    }
                }
            }
        }
        // Indicate the selected spot in the boardRepresentation array
        boardRepresention[bestMove[0]][bestMove[1]] = 2
        var button: Button? = null
        // Find the button that corresponds to the selected space
        if (bestMove[0] == 0 && bestMove[1] == 0){
            button = findViewById(R.id.button0)
        } else if (bestMove[0] == 0 && bestMove[1] == 1) {
            button = findViewById(R.id.button1)
        } else if (bestMove[0] == 0 && bestMove[1] == 2) {
            button = findViewById(R.id.button2)
        } else if (bestMove[0] == 1 && bestMove[1] == 0) {
            button = findViewById(R.id.button3)
        } else if (bestMove[0] == 1 && bestMove[1] == 1) {
            button = findViewById(R.id.button4)
        } else if (bestMove[0] == 1 && bestMove[1] == 2) {
            button = findViewById(R.id.button5)
        } else if (bestMove[0] == 2 && bestMove[1] == 0) {
            button = findViewById(R.id.button6)
        } else if (bestMove[0] == 2 && bestMove[1] == 1) {
            button = findViewById(R.id.button7)
        } else if (bestMove[0] == 2 && bestMove[1] == 2) {
            button = findViewById(R.id.button8)
        }

        // Set button text and color to indicate Player2 has claimed that space and disable button
        button?.text = "O"
        button?.setBackgroundColor(Color.CYAN)
        button?.isEnabled = false

    }


    /* Function updates Player1 and Player2 text views to display correct point values after a game
     * has ended
     */
    @SuppressLint("SetTextI18n")
    private fun updatePointsText() {
        textViewPlayer1.text = "Player 1: $playerOnePoints"
        textViewPlayer2.text = "Player 2: $playerTwoPoints"
    }


    // Function disables all game tile buttons so that they cannot be altered after a game has ended
    private fun disableTiles(){
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)
        // Iterate through buttons and disable all
        for (i in 0..2) {
            val row: TableRow = tableLayout.getChildAt(i) as TableRow
            for (j in 0..2) {
                // Get button at index j of given row
                val button = row.getChildAt(j) as Button
                button.isEnabled = false
            }
        }

    }


    /* Function resets all game tile buttons after the current game has ended - button text and
     * color are reset to an empty String and the default button color, and all buttons are enabled
     */
    private fun resetBoard() {
        val tableLayout = findViewById<TableLayout>(R.id.tableLayout)
        // Reset UI - Change text to empty strings, reset colors, and enable buttons
        for (i in 0..2) {
            val row: TableRow = tableLayout.getChildAt(i) as TableRow
            for (j in 0..2) {
                // Get button at index j of given row
                val button = row.getChildAt(j) as Button
                button.text = ""
                button.setBackgroundResource(R.color.tileColor)
                button.isEnabled = true
            }
        }
        // Reset 2D array representing board (boardRepresent)
        resetBoardRep()
        playerOneTurn = true
        // Reset textViewP2's color to black
        textViewPlayer2.setTextColor(Color.BLACK)
        // Color textViewP1 to indicate current turn
        textViewPlayer1.setTextColor(Color.GREEN)

    }


    /* Function only resets game board; Player1 and Player2 text views (which display their points)
     * are NOT reset (as they are in the below resetGame() function).
     *
     * This function is called at the start of a new round.
     */
    fun setUpNextRound(view: View){
        resetBoard()
    }


    /* Function resets game board and Player1 and Player2 text views (which display players' points)
     * are reset to zero.
     *
     * This function is called at the start of a new game.
     */
    fun resetGame(view: View){
        playerOnePoints = 0
        playerTwoPoints = 0
        updatePointsText()
        resetBoard()
    }


}