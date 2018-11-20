import java.lang.Math;
import java.util.Arrays;

public class LaboonHash {

    // Size of blocks (in bytes)
    public static final int BLOCK_SIZE = 8;

    // Size of results of compressin function (in bytes)
    public static final int RESULT_SIZE = 2;

    // Flag for verbose info
    public static boolean VERBOSE;

    /**
     * @param s string block to pad
     * @param len length of original string
     * @return String padded version of block
     */

    public static String pad(String s, int len) {
        int sizeToPad = BLOCK_SIZE - s.length();
        int modValue = (int) Math.pow(16, sizeToPad);
        int moddedLen = len % modValue;
        //String toHex = Integer.toHexString(moddedLen);
        String padded = String.format("%0" + sizeToPad + "x", moddedLen);
        return padded;
    }

    /**
     * Pad the final block of the array if necessary.
     * Necessary when the final block size is less than BLOCK_SIZE.
     * All other blocks besides the last one are guaranteed to be BLOCK_SIZE
     * bytes long, so this is the only one to check.
     * @see pad() method for algorithm
     * @param String[] initial blocks
     * @return String[] padded (if necessary)
     */

    public static String[] strengthenIfNecessary(int origLength,
                                                 String[] stringBlocks) {
        String finalBlock = stringBlocks[stringBlocks.length - 1];
        int finalBlockLength = finalBlock.length();
        if (finalBlockLength < BLOCK_SIZE) {
            String paddedBlock = finalBlock + pad(finalBlock, origLength);
            stringBlocks[stringBlocks.length - 1] = paddedBlock;
        }
        return stringBlocks;
    }

    public static int[] compressOne(String lhs, String rhs) {
        int[] result = new int[4];
        for (int i = 0; i <= 3; i++) {
            result[i] = lhs.charAt(i) + rhs.charAt(3-i);
        }
        result = compressTwo(result, rhs);
        return result;
    }

    public static int[] compressTwo(int[] res, String rhs) {
        int[] result = new int[4];
        for (int i = 0; i <= 3; i++) {
            result[i] = res[i] ^ rhs.charAt(7-i);
        }
        result = compressThree(result);
        return result;        
    }

    public static int[] compressThree(int[] res) {
        int[] result = new int[4];
        for (int i = 0; i <= 3; i++) {
            res[i] = res[i] ^ res[3-i];
        }
        result = res;
        return result;
    }

    /**
     * string is divided into blocks of 8
     * @param str
     * @return String[]
     */
 
    public static String[] divideString(String str) {
        int len = str.length();
        int start = 0;
        int end = 8;
        String [] result;
        if (len % 8 != 0) {
            result = new String[(len/8)+1];
        }
        else {
            result = new String[(len/8)];
        }
        if (VERBOSE) {
            System.out.print("Padded string: ");
        }
        for (int i = 0; i < result.length; i++) {
            if (end <= len) {
                result[i] = str.substring(start, end);
                start += 8;
                end += 8;
                if (VERBOSE) {
                    System.out.print(result[i]);
                }
            }
            else {
                result[i] = str.substring(start);
                result = strengthenIfNecessary(len, result);
                if (VERBOSE){
                    System.out.println(result[i]);
                }
                break;
            }
        }
        if (VERBOSE) {
            printBlocks(result);
        }
        return result;
    }

    /**
     * Given a string and String array, return its LaboonHash in String format.
     * @param toHash - string to hash
     * @return byte[] - LaboonHash digest of string
     */
    public static String laboonHash(String lhs, String[] rhs) {
        String hex;
        for (String str : rhs) {
            if (VERBOSE) {
                System.out.print("Iterating with " + lhs + " / " + str + " = ");
            }
            int[] result = compressOne(lhs, str);
            StringBuilder res = new StringBuilder();
            for (int c : result) {
                hex = Integer.toHexString(c % 16);
                res.append(hex.toUpperCase());
            }
            lhs = res.toString();
            if (VERBOSE) {
                System.out.println(lhs);
            }
        }
        return lhs;
    }

    public static void setVerbose(boolean b) {
        VERBOSE = b;
    }

    /**
     * Print usage information and exit program with exit code 1.
     */
    public static void printUsageAndExit() {
        System.err.println("Usage:\njava LaboonHash *string* *verbosity_flag*\nVerbosity flag can be omitted for hash output only\nOther options: -verbose");
        System.exit(1);
    }

    public static void printBlocks(String[] blocks) {
        System.out.println("Blocks:");
        for (String str : blocks) {
            System.out.println(str);
        }
    }

    public static void main(String[] args) {
        if (args.length <= 0 || args.length > 2) {
            printUsageAndExit();
        }
        VERBOSE = false;
        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("-verbose")) {
                VERBOSE = true;
            }
            else {
                printUsageAndExit();
            }
        }
        String [] blocks = divideString(args[0]);
        if (VERBOSE) {
            printBlocks(blocks);
        }
        String result = laboonHash("1AB0", blocks);
        System.out.println("Final result: "+result);
    }
}