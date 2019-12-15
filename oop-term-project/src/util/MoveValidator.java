package util;

import board.Board;
import pieces.Piece;
import pieces.PieceSet;
import pieces.Piece.Color;

import java.util.List;

public class MoveValidator {

    private static MoveValidator ourInstance = new MoveValidator();

    public static MoveValidator getInstance() {
        return ourInstance;
    }

    private MoveValidator() {
        currentMoveColor = Piece.Color.WHITE;
    }

    private static Piece.Color currentMoveColor;

    public static boolean validateMove(Move move) {
        return validateMove(move, false);
    }

    public static boolean validateMove(Move move, boolean ignoreColorCheck) {
        // check for out of bounds
        if (move.getDestinationFile() < 'a' || move.getDestinationFile() > 'h'
                || move.getDestinationRank() < 1 || move.getDestinationRank() > 8) {
            return false;
        }

        // check for valid origin
        if (move.getPiece() == null) {
            return false;
        }

        // check for valid color
        if (!move.getPiece().getColor().equals(currentMoveColor) && !ignoreColorCheck) {
            return false;
        }

        // check for valid destination
        if (move.getCapturedPiece() != null) {
            if (move.getPiece().getColor().equals(move.getCapturedPiece().getColor())) {
                return false;
            }
        }

        // check for piece rule
        if (!move.getPiece().validateMove(move)) {
            return false;
        }

        // check for clear path
        if (!validateClearPath(move)) {         
            return false;
        }

        currentMoveColor = currentMoveColor.equals(Piece.Color.WHITE) ? Piece.Color.BLACK : Piece.Color.WHITE;
        return true;
    }

    public static boolean isCheckMove(Move move) {
        // TODO-check
        // create new move and check if this move can catches the opponent king next time.
        //  public Move(Piece piece, char originFile, int originRank, char destinationFile, int destinationRank) 
        Move temp=new Move(move.getPiece(), move.getDestinationFile(), move.getDestinationRank(), 
                            PieceSet.getOpponentKingFile(move.getPiece().getColor()), PieceSet.getOpponentKingRank(move.getPiece().getColor()));
        return validateMove(temp, true);
    }

    public static boolean isCheckMate(Move move) {
        // TODO-check
        return false;
    }

    private static boolean validateClearPath(Move move) {
        // TODO-movement

        int fileDirection = Integer.signum(move.getDestinationFile() - move.getOriginFile());
        int rankDirection = Integer.signum(move.getDestinationRank() - move.getOriginRank());

        // one square executeMove
        if (Math.abs(move.getDestinationFile() - move.getOriginFile()) <= 1
            && Math.abs(move.getDestinationRank() - move.getOriginRank()) <= 1) {
            return true;
        }

        // l-executeMove
        if ((Math.abs(move.getDestinationFile() - move.getOriginFile()) == 1
                && Math.abs(move.getDestinationRank() - move.getOriginRank()) == 2)
            || (Math.abs(move.getDestinationFile() - move.getOriginFile()) == 2
                && Math.abs(move.getDestinationRank() - move.getOriginRank()) == 1)) {
            return true;
        }

        // diagonal executeMove
        if (Math.abs(move.getDestinationFile() - move.getOriginFile())
                == Math.abs(move.getDestinationRank() - move.getOriginRank())) {
            for (int file = move.getOriginFile() + fileDirection, rank = move.getOriginRank() + rankDirection;
                 file != move.getDestinationFile() && rank != move.getDestinationRank();
                 file += fileDirection, rank += rankDirection) {
                if (Board.getSquare((char) file, rank).getCurrentPiece() != null) {
                    return false;
                }
            }
            return true;
        }

        // along file (different rank)
        if (move.getDestinationFile() - move.getOriginFile() == 0
                && move.getDestinationRank() - move.getOriginRank() != 0) {
            for (int rank = move.getOriginRank() + rankDirection; rank != move.getDestinationRank(); rank += rankDirection) {
                if (Board.getSquare(move.getOriginFile(), rank).getCurrentPiece() != null) {
                    return false;
                }
            }
            return true;
        }

        // along rank (different file)
        if (move.getDestinationFile() - move.getOriginFile() != 0
                && move.getDestinationRank() - move.getOriginRank() == 0) {
            for (char file = (char) (move.getOriginFile() + fileDirection); file != move.getDestinationFile(); file += fileDirection) {
                if (Board.getSquare(file, move.getOriginRank()).getCurrentPiece() != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
