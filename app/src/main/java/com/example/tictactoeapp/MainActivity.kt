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
     * Places initialized as 0's in initializeBoardRep method
     */
    private var boardRepresent = arrayOf<Array<Int>>()

    private var playerOneTurn = true

    private var round = 0

    private var playerOnePoints = 0

    private var playerTwoPoints = 0

    private lateinit var textViewPlayer1: TextView

    private lateinit var textViewPlayer2: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        textViewPlayer1 = findViewById<TextView>(R.id.textViewP1)
        textViewPlayer2 = findViewById<TextView>(R.id.textViewP2)

        // Color textViewP1 to indicate current turn
        textViewPlayer1.setTextColor(Color.GREEN)

        initializeBoardRep()
    }

    // Initialize board representation with zeros to represent empty spaces
    private fun initializeBoardRep(){
        for (i in 0..2){
            var array = arrayOf<Int>()
            for (j in 0..2) {
                array += 0
            }
            boardRepresent += array
        }
    }

    // Reset board representation after game
    private fun resetBoardRep(){
        for (i in 0..2){
            for (j in 0..2){
                boardRepresent[i][j] = 0
            }
        }
    }


    fun buttonClick(view: View){

        val selectButton = view as Button
        var cellID = IntArray(2)
        when (selectButton.id){
            R.id.button0 -> { cellID[0] = 0 ; cellID[1] = 0 }
            R.id.button1 -> { cellID[0] = 0 ; cellID[1] = 1 }
            R.id.button2 -> { cellID[0] = 0 ; cellID[1] = 2 }
            R.id.button3 -> { cellID[0] = 1 ; cellID[1] = 0 }
            R.id.button4 -> { cellID[0] = 1 ; cellID[1] = 1 }
            R.id.button5 -> { cellID[0] = 1 ; cellID[1] = 2 }
            R.id.button6 -> { cellID[0] = 2 ; cellID[1] = 0 }
            R.id.button7 -> { cellID[0] = 2 ; cellID[1] = 1 }
            R.id.button8 -> { cellID[0] = 2 ; cellID[1] = 2 }
        }

        //Toast.makeText(this, "Button ID: $cellID", Toast.LENGTH_SHORT).show()

        //claimSpot(cellID = cellID, selectButton = selectButton)

        if (playerOneTurn){
            selectButton.text = "X"
            selectButton.setBackgroundColor(Color.GREEN)
            val xCoord = cellID[0]
            val yCoord = cellID[1]
            boardRepresent[xCoord][yCoord] = 1
        } else {
            selectButton.text = "O"
            selectButton.setBackgroundColor(Color.CYAN)
            val xCoord = cellID[0]
            val yCoord = cellID[1]
            boardRepresent[xCoord][yCoord] = 2
        }

        round++

        selectButton.isEnabled = false

        var winStatus: Int? = checkForWin()
        if (winStatus == null){
            if (playerOneTurn){
                textViewPlayer1.setTextColor(Color.BLACK)
                textViewPlayer2.setTextColor(Color.CYAN)
            } else {
                textViewPlayer1.setTextColor(Color.GREEN)
                textViewPlayer2.setTextColor(Color.BLACK)
            }
            playerOneTurn = !playerOneTurn
        } else if (winStatus == 1) {
            playerOnePoints++
            Toast.makeText(this, "PLAYER ONE WINS!", Toast.LENGTH_SHORT).show()
            updatePointsText()
            disableTiles()
        } else if (winStatus == -1){
            playerTwoPoints++
            Toast.makeText(this, "PLAYER TWO WINS!", Toast.LENGTH_SHORT).show()
            updatePointsText()
            disableTiles()
        } else if (winStatus == 0) {
            Toast.makeText(this, "DRAW...", Toast.LENGTH_SHORT).show()
            disableTiles()
        }

    }


    private fun checkForWin(): Int? {

        var gameWon = false
        var winStatus: Int? = null

        var i = 0
        while (i < 3){
            // Check rows for win
            if (boardRepresent[i][0] == boardRepresent[i][1] &&
                boardRepresent[i][0] == boardRepresent[i][2] &&
                boardRepresent[i][0] != 0
            ) {
                gameWon = true
            }
            //  Check columns for win
            if (boardRepresent[0][i] == boardRepresent[1][i] &&
                boardRepresent[0][i] == boardRepresent[2][i] &&
                boardRepresent[0][i] != 0
            ) {
                gameWon = true
            }

            i++
        }

        // Check left-to-right diagonal
        if (boardRepresent[0][0] == boardRepresent[1][1] &&
            boardRepresent[0][0] == boardRepresent[2][2] &&
            boardRepresent[0][0] != 0
        ) {
            gameWon = true
        }
        // Check right-to-left diagonal
        if (boardRepresent[0][2] == boardRepresent[1][1] &&
            boardRepresent[0][2] == boardRepresent[2][0] &&
            boardRepresent[0][2] != 0
        ) {
            gameWon = true
        }

        if (gameWon && playerOneTurn){
            winStatus = 1
        } else if (gameWon && !playerOneTurn){
            winStatus = -1
        } else if (!gameWon && round == 9){
            winStatus = 0
        }

        return winStatus
    }

    private fun minimax(boardRep: Array<Array<Int>>, depth: Int, maximizingPlayer: Boolean): Int{
        var winStatus = checkForWin()
        if (winStatus != null){
            return winStatus
        } else if (maximizingPlayer){
            var bestScore = Int.MIN_VALUE
            for (i in 0..2){
                for (j in 0..2){
                    if (boardRep[i][j] == 0){
                        boardRep[i][j] = 1
                        var score = minimax(boardRep, depth+1, false)
                        boardRep[i][j] = 0
                        bestScore = Math.max(score, bestScore)
                    }
                }
            }
            return bestScore
        } else {
            // AI is the minimizing player
            var bestScore = Int.MAX_VALUE
            for (i in 0..2){
                for (j in 0..2){
                    if (boardRep[i][j] == 0){
                        boardRep[i][j] = 2
                        var score = minimax(boardRep, depth+1, true)
                        boardRep[i][j] = 0
                        bestScore = Math.min(score, bestScore)
                    }
                }
            }
            return bestScore
        }
    }

    private fun aiMove(){
        var bestScore = Int.MAX_VALUE
        var bestMove: IntArray = intArrayOf(0, 0)
        var copyBoardRep = boardRepresent.copyOf()
        for (i in 0..2){
            for (j in 0..2){
                if (copyBoardRep[i][j] == 0){
                    copyBoardRep[i][j] = 2
                    var score = minimax(copyBoardRep, 0, true)
                    copyBoardRep[i][j] = 0
                    if (score < bestScore){
                        bestScore = score
                        bestMove[0] = i
                        bestMove[1] = j
                    }
                }
            }
        }
        boardRepresent[bestMove[0]][bestMove[1]] = 2
        var button: Button? = null
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

        button?.text = "O"
        button?.setBackgroundColor(Color.CYAN)


    }


    @SuppressLint("SetTextI18n")
    private fun updatePointsText() {
        textViewPlayer1.text = "Player 1: $playerOnePoints"
        textViewPlayer2.text = "Player 2: $playerTwoPoints"
    }


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

        round = 0

    }


    fun setUpNextRound(view: View){

        resetBoard()

    }


    fun resetGame(view: View){

        playerOnePoints = 0
        playerTwoPoints = 0
        updatePointsText()

        resetBoard()

    }


}