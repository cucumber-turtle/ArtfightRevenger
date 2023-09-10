package test;

import org.junit.jupiter.api.Test;
import main.java.Persistency.Record;
import main.java.Utils.AttackInfo;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class TestRecord {
    @Test
    public void testRecord() {
        Record record = new Record("test-record");
        AttackInfo attack = new AttackInfo("attacker", "attack", "attack-link");
        record.addRevenge(attack);
        File file = new File("test-record.csv");
        assertTrue(file.exists());
        file.delete();
    }
}