public class Ubung1 {
    public static void main(String[] args) {
        System.out.println(readDecimal("ab2--9", 2));
        // StartPos 3, EndPos 4
    }

    /**
     * LSB indicated whether the value is eben or odd. For a number to be odd the
     * +1 is needed because all 2^potenz are even by nature
     * @param c
     * @return 0 or 1 depending on if the number is even or odd
     */
    public static int someTestMethod(int c) {
        return c & 1;
    }

    /**
     * Extract the number starting at a specific position in the string
     * @param s: Entire string
     * @param beg: First position where a number is located
     * @return Number / subset from the param s
     */
    public static long readDecimal(String s, int beg) {
        // Number 0-9 -> ASCII 48-57
        int currentPosition = beg;
        for(int i = beg; i < s.length(); i++) {
            int currentAsciiValue = s.charAt(i);
            if(currentAsciiValue < 48 || currentAsciiValue > 57){
                break;
            }
            currentPosition = i;
        }
        long result = 0;
        int currentFactor = 1;
        for(int j = 0; j <= (currentPosition - beg); j++) {
            result += (long) (s.charAt(currentPosition - j) - 48) * currentFactor;
            currentFactor *= 10;
        }
        return result;
    }
}
