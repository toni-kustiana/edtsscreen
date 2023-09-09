package id.co.edtslib.edtsscreen.nfc;

import android.nfc.NfcAdapter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Utils {
    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public static String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    public static long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (byte aByte : bytes) {
            long value = aByte & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }

    public static long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffL;
            result += value * factor;
            factor *= 256L;
        }
        return result;
    }

    public static byte[] hexToByteArray(String str) {
        byte[] result = new byte[str.length() / 2];
        for (int i = 0; i < result.length; i++) {
            int index = i * 2;
            int j = Integer.parseInt(str.substring(index, index + 2), 16);
            result[i] = (byte) j;
        }
        return result;
    }

    public static int toInt32(byte[] bytes, int index) {
        return ByteBuffer.wrap(bytes, index, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static void checkNfcStatus(NfcAdapter nfcAdapter, Runnable onUnsupported, Runnable onEnabled, Runnable onDisabled) {
        if (nfcAdapter == null) {
            onUnsupported.run();
            return;
        }
        if(nfcAdapter.isEnabled()) {
            onEnabled.run();
        } else {
            onDisabled.run();
        }
    }

}