public class LaboonCrypt {

    public static boolean VERBOSE;
    public static boolean VERYVERBOSE;
    public static boolean ULTRAVERBOSE;

    /**
     * Genereate initial matrix
     * @param in
     * @return String[][]
     */
    public static String[][] generateMatrix(String in) {
        String[][] matrix = new String[12][12];
        String[] blocks = LaboonHash.divideString(in);
        String[] res = new String[1];
        String lhs = "1AB0";
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (i == 0 && j == 0) {
                    matrix[i][j] = LaboonHash.laboonHash(lhs, blocks);
                }
                else {
                    matrix[i][j] = LaboonHash.laboonHash(lhs, res);
                }
                res = LaboonHash.divideString(matrix[i][j]);
                if (ULTRAVERBOSE) {
                    System.out.println("Final result: "+matrix[i][j]);
                }
            }
        }
        return matrix;
    }

    public static int calculateDownSpaces(char c) {
        return c * 11;
    }

    public static int calculateRightSpaces(char c) {
        return (c + 3) * 7;
    }

    /**
     * Apply crypt function to initial matrix
     * @param in
     * @param matrix
     * @return String[][]
     */
    public static String[][] cryptMatrix(String in, String[][] matrix) {
        int down, right, xPos = 0, yPos = 0;
        String str;
        for (int c = 0; c < in.length(); c++) {
            down = calculateDownSpaces(in.charAt(c));
            right = calculateRightSpaces(in.charAt(c));
            for (int i = 0; i < down; i++) {
                yPos++;
                if (yPos >= 12) {
                    yPos = 0;
                }                
            }
            for (int j = 0; j < right; j++) {
                xPos++;
                if (xPos >= 12) {
                    xPos = 0;
                }                
            }
            str = LaboonHash.laboonHash("1AB0", LaboonHash.divideString(matrix[yPos][xPos]));
            if (VERYVERBOSE) {
                System.out.println("Moving "+down+" down and "+right+" right - modifying ["+yPos+", "+xPos+"] from "+matrix[yPos][xPos]+" to "+str);                
            }
            if (ULTRAVERBOSE) {
                System.out.println("Final result: "+str);
            }
            matrix[yPos][xPos] = str;
        }
        return matrix;
    }

    public static String finalHash(String[][] matrix) {
        StringBuilder concat = new StringBuilder();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                concat.append(matrix[i][j]);
            }
        }
        return concat.toString();
    }

    public static void printMatrix(String[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (j == 11) {
                    System.out.println(matrix[i][j]);
                }
                else {
                    System.out.print(matrix[i][j]+"  ");
                }
            }
        }
    }

    public static void printUsageAndExit() {
        System.err.println("Usage:\njava LaboonCrypt *string* *verbosity_flag*\nVerbosity flag can be omitted for hash output only\nOther options: -verbose -veryverbose -ultraverbose");
        System.exit(1);
    }
    
    public static void main(String[] args) {
        if (args.length <= 0 || args.length > 2) {
            printUsageAndExit();
        }
        VERBOSE = false;
        VERYVERBOSE = false;
        ULTRAVERBOSE = false;
        LaboonHash.setVerbose(false);
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("-verbose")) {
                VERBOSE = true;
            }
            else if (args[1].equalsIgnoreCase("-veryverbose")) {
                VERBOSE = true;
                VERYVERBOSE = true;
            }
            else if (args[1].equalsIgnoreCase("-ultraverbose")) {
                VERBOSE = true;
                VERYVERBOSE = true;
                ULTRAVERBOSE = true;
                LaboonHash.setVerbose(true);
            }
            else {
                printUsageAndExit();
            }
        }
        String[][] matrix = generateMatrix(args[0]);
        if (VERBOSE) {
            System.out.println("Initial array:");
            printMatrix(matrix);
        }
        String[] res;
        matrix = cryptMatrix(args[0], matrix);
        String result = finalHash(matrix);
        if (VERBOSE) {
            System.out.println("Final array:");
            printMatrix(matrix);
        }
        res = LaboonHash.divideString(result);
        result = LaboonHash.laboonHash("1AB0", res);
        System.out.println("LaboonCrypt hash:  "+result);
    }
}