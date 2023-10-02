/*
 * Created on 05.09.2014
 */

/**
 * @author Wolfgang Weck
 */
public class Ubung1_UTF8Converter {

    /**
     * Convert decimal unicode number to utf-8 formatted byte representation
     * @param unicodeDec Unicode number of the symbol
     * @return Utf-8 formatted byte array
     */
    public static byte[] codePointToUTF(int unicodeDec) {
        if(unicodeDec == 0) {
            return null;
        }

        byte[] utfBytes;

        // 1. Convert decimal number to bit string
        String bitString = decimalToBitString(unicodeDec);
        int byteCount = getNumberOfUTFBytes(bitString.length());
        utfBytes = new byte[byteCount];

        // 2. Fill bit string into format defined by utf-8
        if(byteCount == 1) {
            // Due to 0xxxxxxx utf format no modification needed
            utfBytes[0] = (byte)Integer.parseInt(bitString,2);
        } else {
            for(int j = byteCount-1; j >= 0; j--){
                // Take last 7 bits and convert them to their number representation
                String utfContent;
                int utfFormatPrefix;
                if(j > 0) {
                    utfContent = bitString.substring(bitString.length()-6);
                    bitString = bitString.substring(0, bitString.length()-6);

                    // Prefix for all following bits 10xxxxxx
                    utfFormatPrefix = 0b1000_0000;
                } else {
                    utfContent = bitString;
                    utfFormatPrefix = getUtfPrefixForLeadingBit(byteCount);
                }
                int suffixAsNumber = Integer.parseInt(utfContent,2);

                // Add combine utf prefix and actual content e.g.
                // | 00110011 (content)
                // | 10xxxxxx (format)
                // v 10110011 utf byte
                int content = utfFormatPrefix | suffixAsNumber;
                utfBytes[j] = (byte)content;
            }
        }
        return utfBytes;
    }

    /**
     * Convert UTF byte array to decimal number
     * @param bytes: UTF-8 formatted content
     * @return Decimal representation
     */
    public static int UTFtoCodePoint(byte[] bytes) {
        if (isValidUTF8(bytes)) {
            StringBuilder b = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                byte current = bytes[i];

                // Some nice stuff from github :-)https://stackoverflow.com/a/12310078
                String bitRepresentation = String.format("%8s", Integer.toBinaryString(current & 0xFF)).replace(' ', '0');

                if (i == 0) {
                    b.append(bitRepresentation, bytes.length, 8);
                } else {
                    b.append(bitRepresentation, 2, 8);
                }
            }

            String bitRepresentation = b.reverse().toString();
            int codePoint = 0;
            for (int j = 0; j < bitRepresentation.length(); j++) {
                int currentBit = bitRepresentation.codePointAt(j) - 48;
                codePoint += currentBit == 1 ? 1 << j : 0;
            }
            return codePoint;
        } else return 0;
    }

    /**
     * Generates the number with correct amount of leading 1
     * @param byteCount: Number of times the 1 should be duplicated
     * @return Utf-8 format/prefix
     */
    private static int getUtfPrefixForLeadingBit(int byteCount){
        // Create first byte which defines how much content will follow
        int prefix = 0b0000_0000;
        for (int i = 0; i < byteCount; i++) {
            prefix = prefix >> 1 | 0b1000_0000;
        }
        return prefix;
    }

    /**
     * Determines the number of bytes needed to present the
     * @param contentLength: Length of the content which ought to be encoded to UTF-8
     * @return Amount of bytes needed
     */
    private static int getNumberOfUTFBytes(int contentLength) {
        // FIXME ... a bit ugly :-)
        if (contentLength <= 7) return 1;
        if (contentLength <= 11) return 2;
        if (contentLength <= 16) return 3;
        if (contentLength <= 21) return 4;
        return 0;
    }

    /**
     * Convert number to bit string
     * @param dec
     * @return
     */
    private static String decimalToBitString(int dec) {
        StringBuilder output = new StringBuilder();
        while(dec > 0) {
            output.append(dec%2 == 0 ? "0" : "1");
            dec = dec/2;
        }
        return output.reverse().toString();
    }

    private static boolean isValidUTF8(byte[] bytes) {
        if (bytes.length == 1) return (bytes[0] & 0b1000_0000) == 0;
        else if (bytes.length == 2) return ((bytes[0] & 0b1110_0000) == 0b1100_0000)
                && isFollowup(bytes[1]);
        else if (bytes.length == 3) return ((bytes[0] & 0b1111_0000) == 0b1110_0000)
                && isFollowup(bytes[1]) && isFollowup(bytes[2]);
        else if (bytes.length == 4) return ((bytes[0] & 0b1111_1000) == 0b1111_0000)
                && isFollowup(bytes[1]) && isFollowup(bytes[2]) && isFollowup(bytes[3]);
        else return false;
    }

    private static boolean isFollowup(byte b) {
        return (b & 0b1100_0000) == 0b1000_0000;
    }
}
