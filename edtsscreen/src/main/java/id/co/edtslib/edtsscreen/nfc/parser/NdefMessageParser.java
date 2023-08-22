package id.co.edtslib.edtsscreen.nfc.parser;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import java.util.ArrayList;
import java.util.List;

import id.co.edtslib.edtsscreen.nfc.record.ParsedNdefRecord;
import id.co.edtslib.edtsscreen.nfc.record.SmartPoster;
import id.co.edtslib.edtsscreen.nfc.record.TextRecord;
import id.co.edtslib.edtsscreen.nfc.record.UriRecord;

public class NdefMessageParser {

    private NdefMessageParser() {
    }

    public static List<ParsedNdefRecord> parse(NdefMessage message) {
        return getRecords(message.getRecords());
    }

    public static List<ParsedNdefRecord> getRecords(NdefRecord[] records) {
        List<ParsedNdefRecord> elements = new ArrayList<>();

        for (final NdefRecord record : records) {
            if (UriRecord.isUri(record)) {
                elements.add(UriRecord.parse(record));
            } else if (TextRecord.isText(record)) {
                elements.add(TextRecord.parse(record));
            } else if (SmartPoster.isPoster(record)) {
                elements.add(SmartPoster.parse(record));
            } else {
                elements.add(() -> new String(record.getPayload()));
            }
        }

        return elements;
    }
}
