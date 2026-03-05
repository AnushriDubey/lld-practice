package com.conceptcoding.myprjects;

// 1. Simple enum for pieces
enum PieceType {
    X, O, EMPTY
}

// 2. Cell - just holds piece
class Cell {
    private PieceType piece;
    
    Cell() {
        this.piece = PieceType.EMPTY;
    }
    
    boolean isEmpty() {
        return piece == PieceType.EMPTY;
    }
    
    void setPiece(PieceType piece) {
        if (!isEmpty()) {
            throw new CellAlreadyOccupiedException();
        }
        this.piece = piece;
    }
    
    PieceType getPiece() {
        return piece;
    }
}

// 3. Board - manages grid + win detection
class Board {
    private Cell[][] cells;
    private int size;
    
    Board(int size) {
        this.size = size;
        initializeBoard();
    }
    
    private void initializeBoard() {
        cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
            }
        }
    }
    
    boolean makeMove(int row, int col, PieceType piece) {
        if (!isValidMove(row, col)) {
            return false;
        }
        cells[row][col].setPiece(piece);
        return true;
    }
    
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < size && 
               col >= 0 && col < size && 
               cells[row][col].isEmpty();
    }
    
    // ⭐ Key method - check winner
    PieceType checkWinner() {
        // Check rows
        for (int i = 0; i < size; i++) {
            if (checkRow(i)) return cells[i][0].getPiece();
        }
        
        // Check columns
        for (int j = 0; j < size; j++) {
            if (checkColumn(j)) return cells[0][j].getPiece();
        }
        
        // Check diagonals
        if (checkDiagonal()) return cells[0][0].getPiece();
        if (checkAntiDiagonal()) return cells[0][size-1].getPiece();
        
        return PieceType.EMPTY;
    }
    
    private boolean checkRow(int row) {
        PieceType first = cells[row][0].getPiece();
        if (first == PieceType.EMPTY) return false;
        
        for (int j = 1; j < size; j++) {
            if (cells[row][j].getPiece() != first) return false;
        }
        return true;
    }
    
    private boolean checkColumn(int col) {
        PieceType first = cells[0][col].getPiece();
        if (first == PieceType.EMPTY) return false;
        
        for (int i = 1; i < size; i++) {
            if (cells[i][col].getPiece() != first) return false;
        }
        return true;
    }
    
    private boolean checkDiagonal() {
        PieceType first = cells[0][0].getPiece();
        if (first == PieceType.EMPTY) return false;
        
        for (int i = 1; i < size; i++) {
            if (cells[i][i].getPiece() != first) return false;
        }
        return true;
    }
    
    private boolean checkAntiDiagonal() {
        PieceType first = cells[0][size-1].getPiece();
        if (first == PieceType.EMPTY) return false;
        
        for (int i = 1; i < size; i++) {
            if (cells[i][size-1-i].getPiece() != first) return false;
        }
        return true;
    }
    
    boolean isFull() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (cells[i][j].isEmpty()) return false;
            }
        }
        return true;
    }
    
    void printBoard() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                PieceType piece = cells[i][j].getPiece();
                System.out.print(piece == PieceType.EMPTY ? "-" : piece);
                System.out.print(" ");
            }
            System.out.println();
        }
    }
}

// 4. Player
class Player {
    private String name;
    private PieceType pieceType;
    
    Player(String name, PieceType pieceType) {
        this.name = name;
        this.pieceType = pieceType;
    }
    
    String getName() { return name; }
    PieceType getPieceType() { return pieceType; }
}

// 5. Game - orchestrates everything
class TicTacToeGame {
    private Board board;
    private Player player1;
    private Player player2;
    private Player currentPlayer;
    private Scanner scanner;
    
    TicTacToeGame(int boardSize) {
        this.board = new Board(boardSize);
        this.scanner = new Scanner(System.in);
        initializePlayers();
    }
    
    private void initializePlayers() {
        System.out.print("Player 1 name: ");
        String name1 = scanner.nextLine();
        player1 = new Player(name1, PieceType.X);
        
        System.out.print("Player 2 name: ");
        String name2 = scanner.nextLine();
        player2 = new Player(name2, PieceType.O);
        
        currentPlayer = player1;
    }
    
    void play() {
        while (true) {
            board.printBoard();
            System.out.println(currentPlayer.getName() + "'s turn (" + 
                             currentPlayer.getPieceType() + ")");
            
            // Get move
            int row = getValidInput("Enter row: ");
            int col = getValidInput("Enter column: ");
            
            // Make move
            if (!board.makeMove(row, col, currentPlayer.getPieceType())) {
                System.out.println("Invalid move! Try again.");
                continue;
            }
            
            // Check winner
            PieceType winner = board.checkWinner();
            if (winner != PieceType.EMPTY) {
                board.printBoard();
                System.out.println(currentPlayer.getName() + " wins!");
                break;
            }
            
            // Check draw
            if (board.isFull()) {
                board.printBoard();
                System.out.println("It's a draw!");
                break;
            }
            
            // Switch player
            currentPlayer = (currentPlayer == player1) ? player2 : player1;
        }
    }
    
    private int getValidInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextInt();
    }
}

// 6. Main
class Main {
    public static void main(String[] args) {
        TicTacToeGame game = new TicTacToeGame(3);
        game.play();
    }
}